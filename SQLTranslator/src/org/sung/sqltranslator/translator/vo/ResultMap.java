package org.sung.sqltranslator.translator.vo;

import java.util.ArrayList;
import java.util.List;

public class ResultMap {

	private String id;

	private String clazz;

	private String extendz;

	private String xmlName;

	private String groupBy;

	private List<Result> results;

	private List<ResultMap> childs = new ArrayList<ResultMap>();

	public boolean hasChild() {
		return childs.size() > 0 ? true : false;
	}

	public void addChild(ResultMap child) {
		childs.add(child);
	}

	/**
	 * @return the childs
	 */
	public List<ResultMap> getChilds() {
		return childs;
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
	 * @return the clazz
	 */
	public String getClazz() {
		return clazz;
	}

	/**
	 * @param clazz the clazz to set
	 */
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	/**
	 * @return the extendz
	 */
	public String getExtendz() {
		return extendz;
	}

	/**
	 * @param extendz the extendz to set
	 */
	public void setExtendz(String extendz) {
		this.extendz = extendz;
	}

	/**
	 * @return the xmlName
	 */
	public String getXmlName() {
		return xmlName;
	}

	/**
	 * @param xmlName the xmlName to set
	 */
	public void setXmlName(String xmlName) {
		this.xmlName = xmlName;
	}

	/**
	 * @return the groupBy
	 */
	public String getGroupBy() {
		return groupBy;
	}

	/**
	 * @param groupBy the groupBy to set
	 */
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	/**
	 * @return the results
	 */
	public List<Result> getResults() {
		return results;
	}

	/**
	 * @param results the results to set
	 */
	public void setResults(List<Result> results) {
		this.results = results;
	}

}