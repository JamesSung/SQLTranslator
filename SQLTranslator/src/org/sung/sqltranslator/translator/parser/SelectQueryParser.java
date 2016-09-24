package org.sung.sqltranslator.translator.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sung.sqltranslator.translator.vo.Column;
import org.sung.sqltranslator.translator.vo.QueryText;
import org.sung.sqltranslator.translator.vo.Table;


public class SelectQueryParser extends AbstractQueryParser{

	private static Log log = LogFactory.getLog(SelectQueryParser.class);

	@Override
	void parseQuery(QueryText qt) {
		// TODO
		// 1. scalar query, inline view
		// 2. the case of using same table alias to different table or inline view
		String sql = qt.getText();
		sql = sql.replaceAll("\\s", " ");
		sql = sql.replaceAll("select ", "SELECT ");
		sql = sql.replaceAll(" from ", " FROM ");
		sql = sql.replaceAll(" join ", " JOIN ");
		sql = sql.replaceAll(" on ", " ON ");
		sql = sql.replaceAll(" order ", " ORDER ");
		sql = sql.replaceAll(" group ", " GROUP ");
		sql = sql.replaceAll(" by ", " BY ");
		sql = sql.replaceAll(" where ", " WHERE ");
		sql = QueryUtil.removeComment(sql);

		if (log.isDebugEnabled()) {
			log.debug("sql key : " + qt.getKey());
			log.debug("sql text : " + qt.getText());
			log.debug("replaced space sql text : " + sql);
		}

		List<Column> selectCols = parseSelectClause(sql);
		List<Table> tabs = extractTable(sql);
		List<Column> cols = extractLeftColumns(sql);

		qt.setColumnsInSelect(selectCols);
		qt.setTables(tabs);
		qt.setColumns(cols);
		// qt.addAllColumns(selectCols);

	}

	List<Column> parseSelectClause(String sql) {

		int iSelect = sql.indexOf("SELECT");
		int iFrom = sql.indexOf("FROM");

		if (iSelect < 0) {
			log.warn("No select clause!!");
			return null;
		}

		String sc = sql.substring(iSelect + 6, iFrom);
		return getColumns(sc, true);

	}

	private static String[] tableDelims = { ",", "JOIN" };

	private static String[] removeKeywds = { "LEFT", "RIGHT", "OUTER" };

	@Override
	List<Table> extractTable(String sql) {

		List<Table> tabs = new ArrayList<Table>();
		if (log.isDebugEnabled()) {
			log.debug("input : " + sql);
		}

		int iFrom = sql.indexOf("FROM");
		int iWhere = sql.indexOf("WHERE");
		if (iFrom < 0) {
			log.warn("No from clause!!");
			return null;
		}

		String tmp = null;
		if(iWhere < 0){
			tmp = QueryUtil.removeKeyWords(sql.substring(iFrom + 4), removeKeywds);
			int ordIdx = tmp.indexOf("ORDER");

			if (ordIdx > 0)
				tmp = tmp.substring(0, ordIdx);

			int grpIdx = tmp.indexOf("GROUP");
			if (grpIdx > 0)
				tmp = tmp.substring(0, grpIdx);

		}else
			tmp = QueryUtil.removeKeyWords(sql.substring(iFrom + 4, iWhere), removeKeywds);

		tmp = QueryUtil.removeCommaInBrace(tmp);

		for (String delim : tableDelims) {
			tmp = tmp.replaceAll(delim, "|");
		}

		String[] tokens = tmp.split("\\|");
		int rmIdx = -1;
		for (int i = 0; i < tokens.length; i++) {
			rmIdx = tokens[i].indexOf(" ON ");
			if (rmIdx > 0) {
				tokens[i] = tokens[i].substring(0, rmIdx);
			}

			// if (log.isDebugEnabled()) {
			// log.debug("token : " + tokens[i]);
			// }
		}

		Table t = null;
		int aliasIdx = -1;
		String tabNm = null;
		for (String token : tokens) {
			token = token.trim();

			aliasIdx = token.lastIndexOf(" ");
			t = new Table();
			if (aliasIdx > 0) {
				t.setAlias(token.substring(aliasIdx + 1));
				tabNm = token.substring(0, aliasIdx);
			} else {
				tabNm = token;
			}

			if (tabNm.startsWith("("))// when subquery
				t.setName(tabNm.substring(1, tabNm.indexOf(")")).trim());
			else
				t.setName(tabNm);

			tabs.add(t);
		}

		if (log.isDebugEnabled()) {
			for (Table tab : tabs) {
				log.debug(" table : " + tab.getName() + ", alias : " + tab.getAlias());
			}
		}

		return tabs;

	}

	private List<Column> extractLeftColumns(String sql) {
		StringBuffer sb = new StringBuffer();
		if (log.isDebugEnabled()) {
			log.debug("input : " + sql);
		}

		int iFrom = sql.indexOf("FROM");
		int iWhere = sql.indexOf("WHERE");

		String tmp = null;
		if (iWhere < 0)
			tmp = QueryUtil.removeKeyWords(sql.substring(iFrom + 4), removeKeywds);
		else
			tmp = QueryUtil.removeKeyWords(sql.substring(iFrom + 4, iWhere), removeKeywds);

		tmp = QueryUtil.removeCommaInBrace(tmp);

		for (String delim : tableDelims) {
			tmp = tmp.replaceAll(delim, "|");
		}

		String[] tokens = tmp.split("\\|");
		int rmIdx = -1;
		for (int i = 0; i < tokens.length; i++) {
			rmIdx = tokens[i].indexOf(" ON ");
			if (rmIdx > 0) {
				sb.append(tokens[i].substring(rmIdx + 4));
			}
		}

		if (iWhere > 0)
			sb.append(sql.substring(iWhere + 5));
		else{
			int byIdx = sql.indexOf(" BY ");
			if (byIdx > 0)
				sb.append(sql.substring(byIdx + 4));
		}
			

		if (log.isDebugEnabled()) {
			log.debug("sql : " + sb);
		}

		return getColumns(sb.toString(), false);

	}


}
