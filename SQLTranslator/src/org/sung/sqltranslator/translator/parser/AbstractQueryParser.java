package org.sung.sqltranslator.translator.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sung.sqltranslator.translator.vo.Column;
import org.sung.sqltranslator.translator.vo.QueryText;
import org.sung.sqltranslator.translator.vo.Table;


public abstract class AbstractQueryParser implements QueryParser {
	private static Log log = LogFactory.getLog(AbstractQueryParser.class);

	public void parse(QueryText qt) {
		// String sql = qt.getText();
		parseQuery(qt);
	}
	
	/**
	 * parse s
	 * 
	 * @Method Name : parseQuery
	 */
	abstract void parseQuery(QueryText queryText);

	abstract List<Table> extractTable(String sql);

	List<Column> getColumns(String text, boolean isFromSelectCaluse) {
		List<Column> cols = new ArrayList<Column>();
		
		if (log.isDebugEnabled()) {
			log.debug("input : " + text);
		}

		text = QueryUtil.removeCommaInBrace(text);
		if (!isFromSelectCaluse)
			text = QueryUtil.removeExtras(text);
		text = QueryUtil.removeKeyWords(text);
		text = QueryUtil.removeFunctions(text);
		
		if (log.isDebugEnabled()) {
			log.debug("refined : " + text);
		}

		String[] colArr = text.split(",");
		for (String col : colArr) {
			cols.addAll(getColumn(col, isFromSelectCaluse));
		}

		cols = arrangeColumns(cols);

		if (log.isDebugEnabled()) {
			for (Column col : cols) {
				log.debug(" column : " + col.getName() + ", alias : " + col.getAlias() + ", table alias : " + col.getTableAlias());
			}
		}

		return cols;
	}

	private List<Column> arrangeColumns(List<Column> cols){
		Map<String, Column> m = new HashMap<String, Column>();
		
		for(Column col : cols){
			m.put(col.getTableAlias() + col.getName() + col.getAlias(), col);
		}

		Collection<Column> vals = m.values();
		List<Column> uniList = new ArrayList<Column>();
		for (Column val : vals) {
			uniList.add(val);
		}

		return (List<Column>) QueryUtil.reverseOrderedList(uniList, new String[] { "nameLenth" });
		
	}

	private List<Column> getColumn(String column, boolean isFromSelectCaluse) {
		List<Column> cols = new ArrayList<Column>();
		String alias = null;
		column = column.trim();
		int aliasIdx = column.lastIndexOf(" ");
		if (isFromSelectCaluse && aliasIdx > 0) {
			alias = column.substring(aliasIdx + 1);
			column = column.substring(0, aliasIdx);
		}

		String delimedCol = QueryUtil.getDelimitedString(column);
		String[] delCols = delimedCol.split("\\|");
		Column col = null;
		int dotIdx = -1;
		for (String delCol : delCols) {

			// when alias only remained, it dosen't create Column object.
			// because alias name dosen't need to be translated.
			if (delCol == null || "".equals(delCol.trim()) || delCol.startsWith("'") || NumberUtils.isDigits(delCol) || delCol.startsWith("@")
					|| "END".equals(delCol) || "IN".equals(delCol) || "AS".equals(delCol)) // IN,END,AS keyword didn't removed at
																							// QueryUtil.removeKeyword
				continue;

			col = new Column();
			col.setAlias(alias);
			col.setName(delCol);
			dotIdx = delCol.indexOf(".");
			if (dotIdx > 0) {
				col.setName(delCol.substring(dotIdx + 1));// exclude table alias.
				col.setTableAlias(delCol.substring(0, dotIdx));
			}
			cols.add(col);
		}

		return cols;
	}
}
