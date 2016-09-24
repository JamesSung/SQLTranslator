package org.sung.sqltranslator.translator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sung.sqltranslator.AbstractAnalizer;
import org.sung.sqltranslator.translator.vo.Column;
import org.sung.sqltranslator.translator.vo.MetaData;
import org.sung.sqltranslator.translator.vo.Query;
import org.sung.sqltranslator.translator.vo.QueryText;
import org.sung.sqltranslator.translator.vo.Result;
import org.sung.sqltranslator.translator.vo.ResultMap;
import org.sung.sqltranslator.translator.vo.Table;
import org.xml.sax.helpers.DefaultHandler;


/**
 * change column name in sql using meta column name.
 * 
 * @author shsung
 * 
 */
public class ColumnNameTranslator extends AbstractAnalizer {

	private static Log log = LogFactory.getLog(ColumnNameTranslator.class);

	private static int totSqlCount;

	private static int succSqlCount;

	public static String getResult() {
		return "Total Sql Count : " + totSqlCount + "\r\nSuccessed Count :  " + succSqlCount;
	}
	@Override
	public boolean isInteresting(File f) {
		if (log.isDebugEnabled())
			log.debug("parent : " + f.getParent());
		if (f.getParent().endsWith("maps") && f.getName().endsWith(".xml"))
			return true;
		else
			return false;
	}

	@Override
	public void processFile(File f) {
		if (log.isDebugEnabled())
			log.debug("processing : " + f.getName());
		ColumnNameSqlMapSaxHandler handler = new ColumnNameSqlMapSaxHandler();
		parse(f, handler);

		totSqlCount += handler.getCount();

		List<Query> querys = handler.getQuerys();

		MetaData md = ColumnNameMetaDataLoader.getInstance().getMetaData();
		
		new ColumnNameDataMapper().applyMetaData(querys, md);

		// if(log.isDebugEnabled()){
		// List<QueryText> qts = null;
		// List<Column> cols = null;
		// for(Query query : querys){
		// log.debug("sqlId : " + query.getId());
		// qts = query.getSubsqls();
		// for( QueryText qt : qts){
		// cols = qt.getColumnsInSelect();
		// if (cols == null)
		// cols = qt.getColumns();
		// else
		// cols.addAll(qt.getColumns());
		// log.debug("key : " + qt.getKey());
		// log.debug("sql : " + qt.getText());
		// for (Column col : cols) {
		// log.debug(col.toString());
		// }
		// }
		// }
		// }

		translate(querys);

		writeResultMap(f.getPath().substring(12).replaceAll("\\\\", "_"), querys);

		for (Query q : querys) {
			if (!q.isFailed())
				succSqlCount++;
		}

	}

	private void translate(List<Query> querys) {
		
		List<QueryText> qts = null;
		for(Query query : querys){
			if (query.isFailed())
				continue;
			qts = query.getSubsqls();
			try {
				for (QueryText qt : qts) {
					if ("select".equals(qt.getQueryType())) {
						transSelectType(qt);
					} else {
						transEtcType(qt);
					}
				}

				// translate resultmap
				if ("select".equals(query.getQueryType())) {
					transResultMap(query);
				}

			} catch (Exception e) {
				query.setFailed(true);
				query.setFailMsg("ERROR : " + e.getMessage());
				log.error("ERR SQLID : " + query.getId());
				e.printStackTrace();
			}
		}
			
	}

	private void transResultMap(Query q) {
		ResultMap m = q.getResultMap();
		if (m == null) {
			log.warn("No ResultMap for SQLID : " + q.getId());
			return;
		}

		List<QueryText> qts = q.getSubsqls();
		List<Column> cols = null;
		if (qts.get(0).getColumnsInSelect().size() > 0) {
			cols = qts.get(0).getColumnsInSelect();
		} else {
			cols = qts.get(1).getColumnsInSelect();
		}

		Map<String, String> colMap = new HashMap<String, String>();
		Map<String, String> descMap = new HashMap<String, String>();
		for (Column c : cols) {
			if (c.getAlias() == null || (c.getTableAlias() == null && c.getAlias().equals(c.getName()))) {
				colMap.put(c.getName().toUpperCase(), c.getTobeName());
				descMap.put(c.getName().toUpperCase(), c.getDesc());
			} else if (c.getAlias() != null && !c.getAlias().equals(c.getName())) {
				colMap.put(c.getAlias().toUpperCase(), c.getAlias());
				descMap.put(c.getAlias().toUpperCase(), c.getDesc());
			} else {
				log.warn("column : " + c.getFullName());
			}
		}

		List<Result> rts = getAllResults(m);

		String tobe = null;
		for (Result r : rts) {
			tobe = r.getColumn() == null ? null : colMap.get(r.getColumn().toUpperCase());
			if (tobe == null) {
				log.warn("No mapping column for [" + q.getId() + " resultMap column : " + r.getColumn() + "]");
				continue;
			}
			r.setDesc(descMap.get(r.getColumn().toUpperCase()));
			r.setTobeColumn(tobe);
		}

	}

	private List<Result> getAllResults(ResultMap m) {
		if (log.isDebugEnabled())
			log.debug("Adding results of [" + m.getId() + "]");

		List<Result> rts = m.getResults();

		if (m.hasChild()) {
			List<ResultMap> childs = m.getChilds();
			for (ResultMap child : childs) {
				rts.addAll(getAllResults(child));
			}
		}

		return rts;
	}

	private void transSelectType(QueryText qt) {
		List<Column> selCols = null;
		List<Column> cols = null;
		selCols = qt.getColumnsInSelect();
		String sql = qt.getText();

		int fromIdx = sql.indexOf("FROM");
		String select = sql.substring(0, fromIdx);
		String etc = sql.substring(fromIdx);
		if (log.isDebugEnabled()) {
			log.debug("key : " + qt.getKey());
			log.debug("sql : " + sql);
			log.debug("select : " + select);
		}
		for (Column col : selCols) {
			if (col.getTobeName() == null)
				continue;
			// log.debug(col.toString());
			// log.debug(col.getFullName());
			// log.debug(col.getFullTobeName());
			select = select.replaceAll(col.getFullNameForReplace(), col.getFullTobeName());
		}

		List<Table> tabs = qt.getTables();
		for (Table tab : tabs) {
			if (tab.getName().startsWith("@"))
				continue;
			etc = etc.replaceAll(tab.getName(), tab.getTobeName());
		}

		cols = qt.getColumns();
		for (Column col : cols) {
			if (col.getTobeName() == null)
				continue;
			// etc = etc.replaceAll(col.getFullName(), col.getFullTobeName());
			etc = replaceColumn(etc, col);

		}
		qt.setTobeText(select + etc);

		if (log.isDebugEnabled()) {
			log.debug("tobesql : " + qt.getTobeText());

		}
	}

	private void transEtcType(QueryText qt) {
		String sql = qt.getText();
		if (log.isDebugEnabled()) {
			log.debug("key : " + qt.getKey());
			log.debug("sql : " + sql);
		}
		List<Column> cols = qt.getColumns();
		for (Column col : cols) {
			if (col.getTobeName() == null)
				continue;
			// sql = sql.replaceAll(col.getFullName(), col.getFullTobeName());
			sql = replaceColumn(sql, col);
		}
		List<Table> tabs = qt.getTables();
		for (Table tab : tabs) {
			if (tab.getName().startsWith("@"))
				continue;
			sql = sql.replaceAll(tab.getName(), tab.getTobeName());
		}

		qt.setTobeText(sql);
		if (log.isDebugEnabled()) {
			log.debug("tobesql : " + qt.getTobeText());

		}
	}

	private String replaceColumn(String sql, Column col) {
		if (log.isDebugEnabled()) {
			log.debug("Column : " + sql);
		}
		if (col.getTableAlias() != null) {
			return sql.replaceAll(col.getFullName(), col.getFullTobeName());
		} else {
			int idx = sql.indexOf(col.getName());
			int len = col.getName().length();
			while (idx >= 0) {
				if ("#".equals(sql.substring(idx - 1, idx))) {
					idx = sql.indexOf(col.getName(), idx + len);
					continue;
				}
				sql = sql.substring(0, idx) + col.getTobeName() + sql.substring(idx + len);
				idx = sql.indexOf(col.getName(), idx + col.getTobeName().length());
			}
		}
		return sql;
	}

	private String replaceVariable(String sql) {
		String varCls = "#";
		if (log.isDebugEnabled()) {
			log.debug("input : " + sql);
		}
		int sIdx = sql.indexOf(varCls);
		if (sIdx < 0) {
			return sql;
		}

		int eIdx = -1;

		String var = null;
		while (sIdx >= 0) {
			eIdx = sql.indexOf(varCls, sIdx + 1);
			var = sql.substring(sIdx + 1, eIdx).toUpperCase();
			if (var.indexOf(":") < 0 || var.indexOf(":CHAR") > 0)
				sql = sql.substring(0, sIdx) + "\'0\'" + sql.substring(eIdx + 1);
			else if (var.indexOf(":DATE") > 0)
				sql = sql.substring(0, sIdx) + "TO_DATE(\'2000-01-01\')" + sql.substring(eIdx + 1);
			else
				sql = sql.substring(0, sIdx) + "0" + sql.substring(eIdx + 1);

			sIdx = sql.indexOf(varCls);
		}

		if (log.isDebugEnabled()) {
			log.debug("result : " + sql);
		}

		return sql;
	}

	public static void parse(File xml, DefaultHandler handler) {
		try {
			// parser
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();

			// input stream & parsing
			InputStream is = new FileInputStream(xml);
			parser.parse(is, handler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void write(String pathName, List<Query> querys) {

		try {
			String outFileName = "./mapping_sql/" + pathName;
			FileOutputStream fileOut = new FileOutputStream(outFileName);

			fileOut.write(("<sql file=\"" + outFileName.substring(outFileName.lastIndexOf("_") + 1) + "\">\r\n").getBytes());

			for (Query q : querys) {
				fileOut.write((getQueryOut(q)).getBytes());
			}

			fileOut.write("</sql>".getBytes());

			fileOut.flush();
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeResultMap(String pathName, List<Query> querys) {

		try {
			String outFileName = "./mapping_resultmap/" + pathName;
			FileOutputStream fileOut = new FileOutputStream(outFileName);

			fileOut.write(("<sql file=\"" + outFileName.substring(outFileName.lastIndexOf("_") + 1) + "\">\r\n").getBytes());

			Map<String, String> map = new HashMap<String, String>();
			String mapId = null;

			for (Query q : querys) {
				mapId = q.getResultMapId();
				log.debug("mapId : " + mapId);
				if (mapId == null || map.get(mapId) != null || q.getResultMap() == null)
					continue;

				fileOut.write((getQueryOut(q)).getBytes());
				map.put(mapId, "");
			}

			fileOut.write("</sql>".getBytes());

			fileOut.flush();
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// private String getQueryOut(Query q) {
	// StringBuffer sb = new StringBuffer();
	// sb.append("\r\n<query id=\"").append(q.getId()).append("\" ");
	// if ("select".equals(q.getQueryType()))
	// sb.append("resultMap=\"").append(q.getResultMap()).append("\"");
	// sb.append(">\r\n");
	//
	// if (q.isFailed()) {
	// sb.append(q.getFailMsg()).append("\r\n");
	// } else {
	// String tobe = getFinalSql(q);
	// sb.append(getTranslatedColumnInfo(q)).append("\r\n");
	// sb.append("TO-BE : \r\n");
	// sb.append(tobe).append("\r\n\r\n");
	// sb.append("TO-BE TEST : \r\n");
	// sb.append(replaceVariable(tobe)).append("\r\n\r\n");
	// sb.append("AS-IS : \r\n");
	// sb.append(getFinalAsisSql(q)).append("\r\n");
	// }
	//
	// sb.append("</query>\r\n");
	// return sb.toString();
	// }

	private String getQueryOut(Query q) {
		StringBuffer sb = new StringBuffer();
		if (!q.isFailed())
			sb.append(getResultMapInfo(q.getResultMap()));

		sb.append("\r\n<query id=\"").append(q.getId()).append("\" ");
		if ("select".equals(q.getQueryType()))
			sb.append("resultMap=\"").append(q.getResultMapId()).append("\"");
		sb.append(">\r\n");

		if (q.isFailed()) {
			sb.append(q.getFailMsg()).append("\r\n");
		} else {
			String tobe = getFinalSql(q);
			sb.append(getTranslatedColumnInfo(q)).append("\r\n");
			sb.append("TO-BE : \r\n");
			sb.append(tobe).append("\r\n");
			// sb.append("TO-BE TEST : \r\n");
			// sb.append(replaceVariable(tobe)).append("\r\n");
			sb.append("AS-IS : \r\n");
			sb.append(getFinalAsisSql(q)).append("\r\n");
		}

		sb.append("</query>\r\n");
		return sb.toString();
	}

	private String getFinalSql(Query q) {
		String sql = null;
		List<QueryText> qts = q.getSubsqls();
		int size = qts.size();
		try {
			for (int i = 0; i < size; i++) {
				if (i == 0) {
					sql = qts.get(i).getTobeText();
				} else {
					sql = sql.replaceFirst(qts.get(i).getKey(), qts.get(i).getTobeText());
				}
			}
		} catch (Exception e) {
			sql = "ERROR : " + e.getMessage();
		}
		if (log.isDebugEnabled())
			log.debug("Final sql : " + sql);

		return "    " + sql.trim();
	}

	private String getFinalAsisSql(Query q) {
		String sql = null;
		List<QueryText> qts = q.getSubsqls();
		int size = qts.size();
		try {
			for (int i = 0; i < size; i++) {
				if (i == 0) {
					sql = qts.get(i).getText();
				} else {
					sql = sql.replaceFirst(qts.get(i).getKey(), qts.get(i).getText());
				}
			}
		} catch (Exception e) {
			sql = "ERROR : " + e.getMessage();
		}
		if (log.isDebugEnabled())
			log.debug("Final sql : " + sql);

		return "    " + sql.trim();
	}

	private String getResultMapInfo(ResultMap m) {
		if (log.isDebugEnabled()) {
			log.debug("processing " + m.getId());
		}

		StringBuffer sb = new StringBuffer();
		sb.append("<resultMap id=\"").append(m.getId()).append("\" class=\"").append(m.getClazz()).append("\" ");
		if (m.getExtendz() != null)
			sb.append("extends=\"").append(m.getExtendz()).append("\" ");
		if (m.getXmlName() != null)
			sb.append("xmlName=\"").append(m.getXmlName()).append("\" ");
		if (m.getGroupBy() != null)
			sb.append("groupBy=\"").append(m.getGroupBy()).append("\" ");

		sb.append(">\r\n");

		List<Result> rts = m.getResults();
		for (Result rt : rts) {
			sb.append("\t<result property=\"").append(rt.getProperty()).append("\" ");
			if (rt.getTobeColumn() != null)
				sb.append("column=\"").append(rt.getTobeColumn()).append("\" ");
			else if (rt.getColumn() != null)
				sb.append("column=\"").append(rt.getColumn()).append("\" ");

			if (rt.getJavaType() != null)
				sb.append("javaType=\"").append(rt.getJavaType()).append("\" ");
			if (rt.getColumnIndex() != null)
				sb.append("columnIndex=\"").append(rt.getColumnIndex()).append("\" ");
			if (rt.getJdbcType() != null)
				sb.append("jdbcType=\"").append(rt.getJdbcType()).append("\" ");
			if (rt.getNullValue() != null)
				sb.append("nullValue=\"").append(rt.getNullValue()).append("\" ");
			if (rt.getNotNullColumn() != null)
				sb.append("notNullColumn=\"").append(rt.getNotNullColumn()).append("\" ");
			if (rt.getSelect() != null)
				sb.append("select=\"").append(rt.getSelect()).append("\" ");
			if (rt.getResultMap() != null)
				sb.append("resultMap=\"").append(rt.getResultMap()).append("\" ");
			if (rt.getTypeHandler() != null)
				sb.append("typeHandler=\"").append(rt.getTypeHandler()).append("\" ");

			sb.append("/><!-- ").append(rt.getColumn()).append(", ").append(rt.getDesc()).append(" -->\r\n");
		}

		sb.append("</resultMap>\r\n");

		if (m.hasChild()) {
			List<ResultMap> childs = m.getChilds();
			for (ResultMap child : childs) {
				sb.append(getResultMapInfo(child));
			}
		}

		return sb.toString();
	}

	private String getTranslatedColumnInfo(Query q) {
		StringBuffer msb = new StringBuffer("<!-- \r\n    1. Column infos for SqlMap : ");
		StringBuffer tsb = new StringBuffer("    2. Translated Columns : ");
		StringBuffer fsb = new StringBuffer("    3. Failed Columns : ");
		StringBuffer tabsb = new StringBuffer("    4. Translated Tables : ");
		List<QueryText> qts = q.getSubsqls();
		List<Column> cols = null;
		List<Column> scols = new ArrayList<Column>();
		List<Table> tabs = null;
		int i = 0;
		for (QueryText qt : qts) {
			cols = new ArrayList<Column>();
			if ("select".equals(qt.getQueryType())) {
				cols.addAll(qt.getColumnsInSelect());
				if ("select".equals(q.getQueryType()) && qt.getColumnsInSelect().size() > 0 && i < 1) {
					scols.addAll(qt.getColumnsInSelect());
					for (Column c : scols) {
						if (c.getAlias() == null || (c.getTableAlias() == null && c.getAlias().equals(c.getName()))) {
							msb.append("[").append(c.getName()).append(" => ").append(c.getTobeName()).append("], ");
							log.debug(msb);
						}
					}
					i++;
				}
			}

			cols.addAll(qt.getColumns());
			for (Column c : cols) {
				if (c.getTobeName() == null)
					fsb.append(c.getFullName()).append(", ");
				// else if (c.getAlias() == null)
				tsb.append("[").append(c.getFullName()).append(" => ").append(c.getFullTobeName()).append(":").append(c.getDesc()).append("], ");
			}


			tabs = qt.getTables();
			for (Table t : tabs) {
				if (t.getName().startsWith("@"))
					continue;
				tabsb.append("[").append(t.getName()).append(" => ").append(t.getTobeName()).append("], ");
			}
		}

		msb.append(" \r\n");
		tsb.append(" \r\n");
		fsb.append(" \r\n");
		tabsb.append(" -->\r\n");
		return msb.append(tsb).append(fsb).append(tabsb).toString();
	}

}