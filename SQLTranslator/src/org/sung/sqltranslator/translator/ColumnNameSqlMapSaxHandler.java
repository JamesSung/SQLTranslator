package org.sung.sqltranslator.translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sung.sqltranslator.translator.parser.QueryDivider;
import org.sung.sqltranslator.translator.vo.Query;
import org.sung.sqltranslator.translator.vo.Result;
import org.sung.sqltranslator.translator.vo.ResultMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ColumnNameSqlMapSaxHandler extends DefaultHandler{
	
	private static Log log = LogFactory.getLog(ColumnNameSqlMapSaxHandler.class);

	private static final String SQLSTATEMENT_ELEMENT = "select, insert, update, delete";

	private static final String RESULTMAP = "resultMap";

	private static final String RESULT = "result";

	private static QueryDivider queryDivider = new QueryDivider();

	private Map<String, ResultMap> resultMaps = new HashMap<String, ResultMap>();

	private ResultMap resultMap;

	private Map<String, String> parentIdMap = new HashMap<String, String>();

	private List<Result> results;

	private Result result;

	private List<Query> querys = new ArrayList<Query>();

	public List<Query> getQuerys() {
		return querys;
	}
	
	String sqlId = null;

	StringBuffer sqlText = null;

	private Query query = null;

	private int count;
    
	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	@Override
    public void startElement(String namespaceURI,
                             String localName,
                             String qName,
                             Attributes attributes) throws SAXException {
		if (SQLSTATEMENT_ELEMENT.indexOf(qName) >= 0) {
			configSQLStatement(qName, attributes);
		} else if (RESULTMAP.equals(qName)) {
			configResultMap(attributes);
		} else if (RESULT.equals(qName)) {
			configResult(attributes);
		}
    }
    
    public void endElement(String namespaceURI, String localName,
    	    String qName) throws SAXException {
		if (SQLSTATEMENT_ELEMENT.indexOf(qName) >= 0) {
			if (sqlId != null) {
				querys.add(query);
			}

			if (log.isInfoEnabled())
				log.info(sqlId + " : " + sqlText.toString());

			query.setSqlText(sqlText.toString());
			try {
				query = queryDivider.divide(query);
			} catch (Exception e) {
				query.setFailed(true);
				query.setFailMsg(e.getMessage());
				log.error("ERR SQLID : " + query.getId());
				e.printStackTrace();
			}

			sqlId = null;
			sqlText = null;
		} else if (RESULTMAP.equals(qName)) {
			resultMap.setResults(results);
			resultMaps.put(resultMap.getId(), resultMap);
		} else if (RESULT.equals(qName)) {
			results.add(result);
		}
    }

	private void configResultMap(Attributes attributes) throws SAXException {
		resultMap = new ResultMap();
		results = new ArrayList<Result>();

		resultMap.setClazz(attributes.getValue("class"));
		resultMap.setExtendz(attributes.getValue("extends"));
		resultMap.setGroupBy(attributes.getValue("groupBy"));
		resultMap.setId(attributes.getValue("id"));
		resultMap.setXmlName(attributes.getValue("xmlName"));

		String parentId = parentIdMap.get(resultMap.getId());
		if (parentId != null) {
			resultMaps.get(parentId).addChild(resultMap);
			if (log.isDebugEnabled()) {
				log.debug("Adding ResultMap : " + resultMap.getId() + " to " + parentId);
			}
		}
	}

	private void configResult(Attributes attributes) throws SAXException {
		result = new Result();
		String childResultMap = attributes.getValue("resultMap");
		result.setColumn(attributes.getValue("column"));
		result.setColumnIndex(attributes.getValue("columnIndex"));
		result.setJavaType(attributes.getValue("javaType"));
		result.setJdbcType(attributes.getValue("jdbcType"));
		result.setNotNullColumn(attributes.getValue("notNullColumn"));
		result.setNullValue(attributes.getValue("nullValue"));
		result.setProperty(attributes.getValue("property"));
		result.setResultMap(childResultMap);
		result.setSelect(attributes.getValue("select"));
		result.setTypeHandler(attributes.getValue("typeHandler"));

		if (childResultMap != null) {
			parentIdMap.put(childResultMap, this.resultMap.getId());
			int dotIdx = childResultMap.indexOf(".");
			if (dotIdx > 0)
				parentIdMap.put(childResultMap.substring(dotIdx + 1), this.resultMap.getId());

		}

	}

	private void configSQLStatement(String tagName, Attributes attributes) throws SAXException {
		sqlId = attributes.getValue("id");
		sqlText = new StringBuffer();

		query = new Query();
		query.setQueryType(tagName);
		query.setId(sqlId);
		if (attributes.getValue("resultMap") != null)
			query.setResultMap(resultMaps.get(attributes.getValue("resultMap")));

		count++;

	}

    public void characters(char[] ch, int start, int length)
    	    throws SAXException {
    	
		if (sqlId == null)
			return;
    	
    	String chrs = new String(ch, start, length);
		// System.out.println(sqlId + " : " + chrs);

		sqlText.append(chrs);
    }

	/**
	 * @return the resultmap
	 */
	public static String getResultmap() {
		return RESULTMAP;
	}

}
