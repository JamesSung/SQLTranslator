package org.sung.sqltranslator.translator.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sung.sqltranslator.translator.vo.Column;
import org.sung.sqltranslator.translator.vo.QueryText;
import org.sung.sqltranslator.translator.vo.Table;


public class UpdateQueryParser extends AbstractQueryParser {
	private static Log log = LogFactory.getLog(UpdateQueryParser.class);

	private static String[] removeKeywds = { "UPDATE", "FROM" };

	@Override
	void parseQuery(QueryText qt) {
		String sql = qt.getText();
		sql = sql.replaceAll("\\s", " ");
		sql = sql.replaceAll("where", "WHERE");
		sql = sql.replaceAll("set", "SET");
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
		String alias = tabs.get(0).getAlias();
		if (alias != null)
			sql = sql.replaceFirst(alias, "");

		sql = sql.replaceFirst("SET", "");
		sql = sql.replaceFirst("WHERE", "");

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
		int setIdx = sql.indexOf("SET");
		String table = null;
		if (setIdx > 0) {
			table = sql.substring(0, setIdx).trim();
		} else {
			table = sql.trim();
		}

		int spaceIdx = table.indexOf(" ");
		if (spaceIdx < 0) {
			tab.setName(table);
		} else {
			tab.setName(table.substring(0, spaceIdx));
			tab.setAlias(table.substring(spaceIdx).trim());
		}

		tabs.add(tab);
		if (log.isDebugEnabled()) {
			log.debug("table : " + tab.getName() + ", alias : " + tab.getAlias());
		}

		return tabs;
	}


}