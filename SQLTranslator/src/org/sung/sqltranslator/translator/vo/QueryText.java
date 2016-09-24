package org.sung.sqltranslator.translator.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sung.sqltranslator.translator.parser.QueryParser;


public class QueryText {

	private String parentKey;

	private String key;

	private String text;

	private String tobeText;

	/**
	 * select, update, insert, delete
	 */
	private String queryType;

	private QueryParser parser;

	/**
	 * table name and alias info
	 */
	private List<Table> tables;

	private Map<String, String> tableNmMap = new HashMap<String, String>();
	
	/**
	 * contains all Columns using in this sql. this is only for columns used without table alias. key is column name
	 */
	private Map<String, MetaColumn> columnMap = new HashMap<String, MetaColumn>();

	private boolean isLoadedMetaColumnMap = false;

	/**
	 * @return the tobeText
	 */
	public String getTobeText() {
		return tobeText;
	}

	/**
	 * @param tobeText the tobeText to set
	 */
	public void setTobeText(String tobeText) {
		this.tobeText = tobeText;
	}

	/**
	 * @return the isLoadedMetaColumnMap
	 */
	public boolean isLoadedMetaColumnMap() {
		return isLoadedMetaColumnMap;
	}

	/**
	 * @param isLoadedMetaColumnMap the isLoadedMetaColumnMap to set
	 */
	public void setLoadedMetaColumnMap(boolean isLoadedMetaColumnMap) {
		this.isLoadedMetaColumnMap = isLoadedMetaColumnMap;
	}

	public void putAllMetaColumn(List<MetaColumn> metaColumns) {
		for (MetaColumn mc : metaColumns) {
			columnMap.put(mc.getName(), mc);
		}
	}

	public void putAllMetaColumn(Map<String, MetaColumn> metaColumns) {
		columnMap.putAll(metaColumns);
	}

	public MetaColumn getMetaColumn(String columnName) {
		return columnMap.get(columnName);
	}

	public Map<String, MetaColumn> getAllMetaColumn() {
		return columnMap;
	}

	/**
	 * @return the tableNmMap
	 */
	public Map<String, String> getTableNmMap() {
		return tableNmMap;
	}

	/**
	 * columns using in select clause, this is for resultMap
	 */
	private List<Column> columnsInSelect;

	/**
	 * all columns using in this query
	 */
	private List<Column> columns;

	public String getTableNameByAlias(String tableAlias) {
		return tableNmMap.get(tableAlias.toUpperCase());
	}

	/**
	 * @return the parser
	 */
	public QueryParser getParser() {
		return parser;
	}

	/**
	 * @param parser the parser to set
	 */
	public void setParser(QueryParser parser) {
		this.parser = parser;
	}

	/**
	 * @return the queryType
	 */
	public String getQueryType() {
		return queryType;
	}

	/**
	 * @param queryType the queryType to set
	 */
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	/**
	 * @return the tables
	 */
	public List<Table> getTables() {
		return tables;
	}

	/**
	 * @param tables the tables to set
	 */
	public void setTables(List<Table> tables) {
		if(tables == null) return;
		
		this.tables = tables;
		for (Table t : tables) {
			if (t.getAlias() != null && !"".equals(t.getAlias())) {
				tableNmMap.put(t.getAlias().toUpperCase(), t.getName());
			}
		}
	}

	// public void addTable(Table table) {
	// this.tables.add(table);
	// }
	//
	// public void addAllTables(List<Table> tables) {
	// this.tables.addAll(tables);
	// }

	/**
	 * @return the columnsInSelect
	 */
	public List<Column> getColumnsInSelect() {
		return columnsInSelect;
	}

	// public void addColumnsInSelect(Column column) {
	// this.columnsInSelect.add(column);
	// }

	/**
	 * @param columnsInSelect the columnsInSelect to set
	 */
	public void setColumnsInSelect(List<Column> columnsInSelect) {
		this.columnsInSelect = columnsInSelect;
	}

	/**
	 * @return the columns
	 */
	public List<Column> getColumns() {
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	// public void addColumns(List<Column> columns) {
	// this.columns.addAll(columns);
	// }
	//
	// public void addColumn(Column column) {
	// this.columns.add(column);
	// }

	// public void addAllColumns(List<Column> columns) {
	// if (columns == null)
	// return;
	// this.columns.addAll(columns);
	// }

	/**
	 * In case of inline view. the alias will be used when outer query's column name translation.
	 */
	private String alias;

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @param alias the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * @return the parentKey
	 */
	public String getParentKey() {
		return parentKey;
	}

	/**
	 * @param parentKey the parentKey to set
	 */
	public void setParentKey(String parentKey) {
		this.parentKey = parentKey;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	public boolean isSubquery() {
		if (key.startsWith("@"))
			return true;
		else
			return false;
	}

}
