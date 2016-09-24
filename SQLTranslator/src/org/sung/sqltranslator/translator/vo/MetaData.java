package org.sung.sqltranslator.translator.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaData {

	Map<String, MetaTable> tableMap = new HashMap<String, MetaTable>();

	/**
	 * @return the tableMap
	 */
	public Map<String, MetaTable> getTableMap() {
		return tableMap;
	}

	public MetaTable getMetaTable(String tableName) {
		MetaTable mt = tableMap.get(tableName);
		// if (mt == null)
		// throw new RuntimeException("Cann't find a MetaTable for table name => " + tableName);
		return mt;
	}

	public List<MetaTable> getMetaTables(List<String> tables) {
		List<MetaTable> tabs = new ArrayList<MetaTable>();
		for (String tab : tables) {
			tabs.add(getMetaTable(tab));
		}

		return tabs;
	}

	public void addAllMetaTable(List<MetaTable> tables) {
		for (MetaTable table : tables) {
			tableMap.put(table.getName(), table);
		}
	}

	public void addMetaTable(MetaTable mataTable) {

		tableMap.put(mataTable.getName(), mataTable);

	}

}