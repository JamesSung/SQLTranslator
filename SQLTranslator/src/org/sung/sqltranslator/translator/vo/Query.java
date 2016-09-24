package org.sung.sqltranslator.translator.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Query {

	/**
	 * select, update, delete, insert
	 */
	private String queryType;

	private String id;

	private String parameterClass;

	private ResultMap resultMap;
	
	private boolean isFailed;

	private String failMsg;

	/**
	 * @return the isFailed
	 */
	public boolean isFailed() {
		return isFailed;
	}

	/**
	 * @param isFailed the isFailed to set
	 */
	public void setFailed(boolean isFailed) {
		this.isFailed = isFailed;
	}

	/**
	 * @return the failMsg
	 */
	public String getFailMsg() {
		return failMsg;
	}

	/**
	 * @param failMsg the failMsg to set
	 */
	public void setFailMsg(String failMsg) {
		this.failMsg = failMsg;
	}

	/**
	 * key is table alias
	 */
	private Map<String, String> tableNmMap = new HashMap<String, String>();

	/**
	 * key is as-is table name.
	 */
	private Map<String, MetaTable> metaTableMap = new HashMap<String, MetaTable>();

	/**
	 * original(not processed) query
	 */
	private String sqlText;

	/**
	 * sub query list
	 */
	private List<QueryText> subsqls = new ArrayList<QueryText>();

	private Map<String, QueryText> subSqlMap = new HashMap<String, QueryText>();

	public void putMetaTable(MetaTable mt) {
		if (mt.getName() == null)
				return;
		metaTableMap.put(mt.getName().toUpperCase(), mt);
	}

	public MetaTable getMetaTable(String tableName) {
		if (tableName == null)
			return null;
		return metaTableMap.get(tableName.toUpperCase());
	}

	public void putAllTableNameMap(Map<String, String> tableNmMap) {
		this.tableNmMap.putAll(tableNmMap);
	}

	public String getTableNameByAlias(String tableAlias) {
		return tableNmMap.get(tableAlias);
	}

	// public void addSubsql(QueryText queryText) {
	// subsqls.add(queryText);
	// }

	/**
	 * @return the sqls
	 */
	public List<QueryText> getSubsqls() {
		return subsqls;
	}

	/**
	 * @param sqls the sqls to set
	 */
	public void setSubsqls(List<QueryText> subsqls) {
		this.subsqls = subsqls;

		for (QueryText subsql : subsqls) {
			subSqlMap.put(subsql.getKey(), subsql);
		}
	}

	public QueryText getSubQueryText(String key) {
		return subSqlMap.get(key);
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
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the parameterClass
	 */
	public String getParameterClass() {
		return parameterClass;
	}

	/**
	 * @param parameterClass the parameterClass to set
	 */
	public void setParameterClass(String parameterClass) {
		this.parameterClass = parameterClass;
	}

	/**
	 * @return the resultMap
	 */
	public ResultMap getResultMap() {
		return resultMap;
	}

	public String getResultMapId() {
		if (resultMap == null)
			return "";
		return resultMap.getId();
	}

	/**
	 * @param resultMap the resultMap to set
	 */
	public void setResultMap(ResultMap resultMap) {
		this.resultMap = resultMap;
	}

	/**
	 * @return the sqlText
	 */
	public String getSqlText() {
		return sqlText;
	}

	/**
	 * @param sqlText the sqlText to set
	 */
	public void setSqlText(String sqlText) {
		this.sqlText = sqlText;
	}

}