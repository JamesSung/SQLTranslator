package org.sung.sqltranslator.translator.vo;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaTable {
	private String name;

	private String tobeName;

	Map<String, MetaColumn> columnMap = new HashMap<String, MetaColumn>();

	/**
	 * @param columnMap the columnMap to set
	 */
	public void setColumnMap(Map<String, MetaColumn> columnMap) {
		this.columnMap = columnMap;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the tobeName
	 */
	public String getTobeName() {
		return tobeName;
	}

	/**
	 * @param tobeName the tobeName to set
	 */
	public void setTobeName(String tobeName) {
		this.tobeName = tobeName;
	}

	/**
	 * @return the columnMap
	 */
	public Map<String, MetaColumn> getColumnMap() {
		return columnMap;
	}

	public MetaColumn getMetaColumn(String columnName) {
		MetaColumn mc = columnMap.get(columnName);
		// if (mc == null)
		// throw new RuntimeException("Cann't find a MetaColumn for column name => " + columnName);

		return mc;
	}

	public void addAllColumns(List<MetaColumn> columns) {
		for (MetaColumn mc : columns) {
			columnMap.put(mc.getName(), mc);
		}
	}

	public void addColumn(MetaColumn column) {
		columnMap.put(column.getName(), column);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("table \r\n name : ").append(name).append(", tobe : ").append(tobeName);
		sb.append("\r\n column : ");
		Collection<MetaColumn> cols = columnMap.values();
		for (MetaColumn col : cols) {
			sb.append(col.toString());
		}

		return sb.toString();
	}

}
