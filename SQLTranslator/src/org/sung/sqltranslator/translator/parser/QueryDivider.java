package org.sung.sqltranslator.translator.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sung.sqltranslator.translator.vo.Query;
import org.sung.sqltranslator.translator.vo.QueryText;

public class QueryDivider {
	private static Log log = LogFactory.getLog(QueryDivider.class);

	private static Map<String, QueryParser> queryParserMap = new HashMap<String, QueryParser>();

	static {
		queryParserMap.put("select", new SelectQueryParser());
		queryParserMap.put("insert", new InsertQueryParser());
		queryParserMap.put("update", new UpdateQueryParser());
		queryParserMap.put("delete", new DeleteQueryParser());
	}
	
	public Query divide(Query query) {

		String sql = query.getSqlText();

		if (log.isDebugEnabled())
			log.debug("SQL : " + sql);

		sql = sql.replaceAll("select", "SELECT");// to upper case
		sql = sql.replaceAll("from", "FROM");// to upper case
		sql = sql.replaceAll("union ", "UNION ");// to upper case
		sql = sql.replaceAll("\\)\\)", "\\) \\)");// '))'있는 경우 버그있음.


		// TODO inline view를 사용한 경우 outer query의 column변환 고려해야 함.
		List<QueryText> subSqls = findSubquery(sql, "top", null);// including most outer query which is not sub query

		subSqls = arangeSubSql(subSqls);

		if (subSqls.size() == 0) {// when no subquery
			QueryText qt = new QueryText();
			qt.setKey("top");
			qt.setText(sql);
			qt.setQueryType(getQueryType(sql));
			qt.setParser(getParser(qt.getQueryType()));
			subSqls.add(qt);
		}

		if (log.isErrorEnabled()) {
			int i = 0;
			for (QueryText ssql : subSqls) {
				log.debug("subsql[" + i + "]  key : " + ssql.getKey());
				log.debug("subsql[" + i + "]  parent key : " + ssql.getParentKey());
				log.debug("subsql[" + i++ + "] text : " + ssql.getText());
			}

			// StringBuffer all = new StringBuffer();
			// for (QueryText ssql : subSqls) {
			// all.append(ssql.getText());
			// }
			// log.debug("merged sql : " + all.toString());

		}

		query.setSubsqls(subSqls);

		QueryParsingUtil.parse(query);

		return query;
	}

	private List<QueryText> arangeSubSql(List<QueryText> subSqls) {
		List<QueryText> rst = new ArrayList<QueryText>();
		int size = subSqls.size();
		QueryText qt = null;
		for (int i = size - 1; i >= 0; i--) {
			qt = subSqls.get(i);

			// if (qt.getKey().startsWith("top") && qt.getText().startsWith(")")) {
			if (qt.getText().startsWith(")")) {
				subSqls.get(i - 2).setText(subSqls.get(i - 2).getText() + qt.getText());
			} 
		}
		for (int i = 0; i < size; i++) {
			qt = subSqls.get(i);

			// if (!qt.getKey().startsWith("top") || !qt.getText().startsWith(")")) {
			if (!qt.getText().startsWith(")")) {
				rst.add(subSqls.get(i));
			} 
		}

		return rst;
	}

	/**
	 * 
	 * Desc : finding sub query using brace.
	 * @Method Name : findSubquery
	 * @param sql
	 * @return
	 */
	private List<QueryText> findSubquery(String sql, String key, String parentKey) {
		List<QueryText> subSqls = new ArrayList<QueryText>();
		// if (log.isDebugEnabled()) {
		// log.debug("key : " + key);
		// log.debug("parentKey : " + parentKey);
		// log.debug("sql : " + sql);
		// }

		if (isInsertSelect(sql)) {// insert select dosen't use brace for select clause
			int selIdx = sql.indexOf("SELECT");
			String select = sql.substring(selIdx);
			String selKey = getKey(select);

			QueryText qt = new QueryText();
			qt.setKey("top");
			qt.setQueryType(getQueryType(sql));
			qt.setParser(getParser(qt.getQueryType()));
			qt.setText(sql.substring(0, selIdx) + selKey);

			subSqls.add(qt);

			qt = new QueryText();
			qt.setKey(selKey);
			qt.setParentKey("top");
			qt.setQueryType(getQueryType(select));
			qt.setParser(getParser(qt.getQueryType()));
			qt.setText(select);

			subSqls.add(qt);

			return subSqls;
		}

		String subSql = null;
		// System.out.println("SQL : " + sql);
		sql = sql.replaceAll("\r", "\\^r");
		sql = sql.replaceAll("\n", "\\^n");

		// if (log.isDebugEnabled())
		// log.debug("before removeWhiteCharBeforeSelect SQL : " + sql);

		sql = removeWhiteCharBeforeSelect(sql);
		
		// if (log.isDebugEnabled())
		// log.debug("after removeWhiteCharBeforeSelect SQL : " + sql);

		int fromIdx = sql.indexOf("(SELECT");
		if (fromIdx < 0) {
			return subSqls;// couldn't handle subquery using without brace.
		}

		int endIdx = findEndIndexOfSubquery(sql, fromIdx);
		
		subSql = sql.substring(fromIdx + 1, endIdx);// exclude brace
		subSql = subSql.replaceAll("\\^r", "\r");
		subSql = subSql.replaceAll("\\^n", "\n");

		if (log.isDebugEnabled())
			log.debug("SUBSQL : " + subSql);

		int otherFromIdx = sql.indexOf("SELECT", endIdx);
		
		String bf = sql.substring(0, fromIdx + 1);
		bf = bf.replaceAll("\\^r", "\r");
		bf = bf.replaceAll("\\^n", "\n");

		String subKey = getKey(subSql);

		QueryText qt = new QueryText();
		qt.setKey(key);
		qt.setParentKey(parentKey);
		qt.setQueryType(getQueryType(bf));
		qt.setParser(getParser(qt.getQueryType()));

		if (otherFromIdx > 0) {
			qt.setText(bf + subKey);
		} else {
			String af = sql.substring(endIdx);
			af = af.replaceAll("\\^r", "\r");
			af = af.replaceAll("\\^n", "\n");
			qt.setText(bf + subKey + af);
		}
		subSqls.add(qt);

		// in case sub query include another subquery.
		fromIdx = subSql.indexOf("(SELECT");
		if (fromIdx > 0) {
			subSqls.addAll(findSubquery(subSql, subKey, key));
		} else {

			QueryText sqt = new QueryText();
			sqt.setKey(subKey);
			sqt.setParentKey(key);
			sqt.setText(subSql);
			sqt.setQueryType(getQueryType(subSql));
			sqt.setParser(getParser(sqt.getQueryType()));
			// sqt.setAlias("");// TODO setting inline view alias
			subSqls.add(sqt);
		}

		if (otherFromIdx > 0) {// in case subquery occured in parallel.
			subSqls.addAll(findSubquery(sql.substring(endIdx), key, key));
		}

		return subSqls;
	}
	
	private QueryParser getParser(String queryType){
		if (queryType == null)
			return null;
		return queryParserMap.get(queryType.toLowerCase());
	}

	private String getQueryType(String sql) {
		if (log.isDebugEnabled())
			log.debug("input : " + sql);

		Pattern p = Pattern.compile("[A-Za-z]{6}");
		Matcher m = p.matcher(sql);

		String qType = null;
		if (m.find()) {
			// System.out.println( s);
			qType = m.group();
		} else {
			return qType;
		}

		return qType.toLowerCase();
	}

	private boolean isInsertSelect(String sql) {
		sql = sql.replaceAll("\\s", " ");
		sql = sql.replaceAll("insert", "INSERT");
		sql = sql.replaceAll("values", "VALUES");

		if (sql.trim().startsWith("INSERT")) {
			int valIdx = sql.indexOf("VALUES");

			if (valIdx > 0 && (" ".equals(sql.substring(valIdx - 1, valIdx)) || ")".equals(sql.substring(valIdx - 1, valIdx)))
					&& (" ".equals(sql.substring(valIdx + 6, valIdx + 7)) || "(".equals(sql.substring(valIdx + 6, valIdx + 7))))
				return false;
			else if (sql.indexOf("SELECT") > 0)
				return true;
			else
				return false;
		}

		return false;

	}

	private String getKey(String str) {
		int hashVal = str.hashCode();
		if (hashVal < 0)
			hashVal = hashVal * -1;

		return "@" + String.valueOf(hashVal);
	}

	private int findEndIndexOfSubquery(String sql, int baseIndex) {
		
		int endIdx = sql.indexOf(")", baseIndex);
		if (endIdx > 0) {
			// System.out.println("baseIndex : " + baseIndex);
			// System.out.println("endIdx : " + endIdx);
			String subText = sql.substring(sql.indexOf("(SELECT") + 1, endIdx);// exclude brace
			// System.out.println("subText : " + subText);

			int openCnt = QueryUtil.howMany(subText, "\\(");
			int closeCnt = QueryUtil.howMany(subText, "\\)");
			if (openCnt == closeCnt){
				return endIdx;
			}else{
				return findEndIndexOfSubquery(sql, endIdx + 1);
			}
		}
		
		return endIdx;
	}


	/**
	 * remove white space between '(' and 'select'
	 * @Method Name : removeWhiteCharBeforeSelect
	 * @param sql
	 * @return
	 */
	private String removeWhiteCharBeforeSelect(String sql) {

		int baseIdx = sql.indexOf("(");
		if (baseIdx < 0) {
			return sql;
		}

		int sidx = sql.indexOf("SELECT", baseIdx);

		if (log.isDebugEnabled())
			log.debug("sidx : " + sidx);

		if (sidx < 0)
			return sql;

		baseIdx = sql.lastIndexOf("(", sidx);


		if (log.isDebugEnabled()) {
			log.debug("sql.substring(0, sidx) : " + sql.substring(0, sidx));
			log.debug("bidx : " + baseIdx);
		}

		if (baseIdx + 1 == sidx)
			return sql;

		String btText = sql.substring(baseIdx + 1, sidx);
		btText = btText.replaceAll("\\^r", "");
		btText = btText.replaceAll("\\^n", "");

		if (btText.replaceAll("\\s", "").length() == 0)
			sql = sql.substring(0, baseIdx + 1) + sql.substring(sidx);

		if (log.isDebugEnabled())
			log.debug("removeWhiteCharBeforeSelect.SQL : " + sql);

		return sql;
	}

}
