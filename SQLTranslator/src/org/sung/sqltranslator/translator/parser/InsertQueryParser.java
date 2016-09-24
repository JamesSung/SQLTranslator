package org.sung.sqltranslator.translator.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sung.sqltranslator.translator.vo.Column;
import org.sung.sqltranslator.translator.vo.QueryText;
import org.sung.sqltranslator.translator.vo.Table;


public class InsertQueryParser extends AbstractQueryParser {
	private static Log log = LogFactory.getLog(InsertQueryParser.class);

	private static String[] removeKeywds = { "INSERT", "INTO", "VALUES" };

	@Override
	void parseQuery(QueryText qt) {
		String sql = qt.getText();
		sql = sql.replaceAll("\\s", " ");
		sql = QueryUtil.removeComment(sql);
		
		if (log.isDebugEnabled()) {
			log.debug("sql key : " + qt.getKey());
			log.debug("sql text : " + qt.getText());
			log.debug("replaced space sql text : " + sql);
		}
		
		sql = QueryUtil.removeKeyWords(sql, removeKeywds);
		sql = sql.trim();
		
		List<Table> tabs = extractTable(sql);

		// remove table
		sql = sql.replaceFirst(tabs.get(0).getName(), "");

		List<Column> cols = getColumns(sql, false);

		qt.setTables(tabs);
		qt.setColumns(cols);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sung.sourceanalizer.translator.parser.AbstractQueryParser#extractTable(java.lang.String)
	 */
	@Override
	List<Table> extractTable(String sql) {
		List<Table> tabs = new ArrayList<Table>();
		if (log.isDebugEnabled()) {
			log.debug("input : " + sql);
		}

		Table tab = new Table();
		int braceIdx = sql.indexOf("(");
		if (braceIdx > 0) {
			tab.setName(sql.substring(0, braceIdx).trim());
		} else {
			int atIdx = sql.indexOf("@");
			tab.setName(sql.substring(0, atIdx).trim());
		}

		tabs.add(tab);
		if (log.isDebugEnabled()) {
			log.debug("table : " + tab.getName() + ", alias : " + tab.getAlias());
		}

		return tabs;
	}

}