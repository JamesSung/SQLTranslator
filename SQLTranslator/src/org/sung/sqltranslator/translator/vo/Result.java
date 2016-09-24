package org.sung.sqltranslator.translator.vo;

public class Result {
	private String property;

	private String javaType;

	private String column;

	private String tobeColumn;

	private String columnIndex;

	private String jdbcType;

	private String nullValue;

	private String notNullColumn;

	private String select;

	private String resultMap;

	private String typeHandler;

	private String desc;

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * @param property the property to set
	 */
	public void setProperty(String property) {
		this.property = property;
	}

	/**
	 * @return the javaType
	 */
	public String getJavaType() {
		return javaType;
	}

	/**
	 * @param javaType the javaType to set
	 */
	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

	/**
	 * @return the column
	 */
	public String getColumn() {
		return column;
	}

	/**
	 * @param column the column to set
	 */
	public void setColumn(String column) {
		this.column = column;
	}

	/**
	 * @return the tobeColumn
	 */
	public String getTobeColumn() {
		return tobeColumn;
	}

	/**
	 * @param tobeColumn the tobeColumn to set
	 */
	public void setTobeColumn(String tobeColumn) {
		this.tobeColumn = tobeColumn;
	}

	/**
	 * @return the columnIndex
	 */
	public String getColumnIndex() {
		return columnIndex;
	}

	/**
	 * @param columnIndex the columnIndex to set
	 */
	public void setColumnIndex(String columnIndex) {
		this.columnIndex = columnIndex;
	}

	/**
	 * @return the jdbcType
	 */
	public String getJdbcType() {
		return jdbcType;
	}

	/**
	 * @param jdbcType the jdbcType to set
	 */
	public void setJdbcType(String jdbcType) {
		this.jdbcType = jdbcType;
	}

	/**
	 * @return the nullValue
	 */
	public String getNullValue() {
		return nullValue;
	}

	/**
	 * @param nullValue the nullValue to set
	 */
	public void setNullValue(String nullValue) {
		this.nullValue = nullValue;
	}

	/**
	 * @return the notNullColumn
	 */
	public String getNotNullColumn() {
		return notNullColumn;
	}

	/**
	 * @param notNullColumn the notNullColumn to set
	 */
	public void setNotNullColumn(String notNullColumn) {
		this.notNullColumn = notNullColumn;
	}

	/**
	 * @return the select
	 */
	public String getSelect() {
		return select;
	}

	/**
	 * @param select the select to set
	 */
	public void setSelect(String select) {
		this.select = select;
	}

	/**
	 * @return the resultMap
	 */
	public String getResultMap() {
		return resultMap;
	}

	/**
	 * @param resultMap the resultMap to set
	 */
	public void setResultMap(String resultMap) {
		this.resultMap = resultMap;
	}

	/**
	 * @return the typeHandler
	 */
	public String getTypeHandler() {
		return typeHandler;
	}

	/**
	 * @param typeHandler the typeHandler to set
	 */
	public void setTypeHandler(String typeHandler) {
		this.typeHandler = typeHandler;
	}

}