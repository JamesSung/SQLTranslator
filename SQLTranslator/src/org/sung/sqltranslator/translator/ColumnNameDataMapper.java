package org.sung.sqltranslator.translator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sung.sqltranslator.translator.vo.Column;
import org.sung.sqltranslator.translator.vo.MetaColumn;
import org.sung.sqltranslator.translator.vo.MetaData;
import org.sung.sqltranslator.translator.vo.MetaTable;
import org.sung.sqltranslator.translator.vo.Query;
import org.sung.sqltranslator.translator.vo.QueryText;
import org.sung.sqltranslator.translator.vo.Table;


public class ColumnNameDataMapper {
	private static Log log = LogFactory.getLog(ColumnNameDataMapper.class);

	void applyMetaData(List<Query> querys, MetaData md) {
		applyMetaTable(querys, md);
		applyMetaColumnForSelectColumns(querys, md);
		applyMetaColumnForEtcColumns(querys, md);
	}

	private void applyMetaTable(List<Query> querys, MetaData md) {
		List<QueryText> subQs = null;
		List<Table> tabs = null;
		MetaTable mt = null;
		QUERY_LOOP: for (Query q : querys) {
			subQs = q.getSubsqls();
			for (QueryText qt : subQs) {
				tabs = qt.getTables();
				if (tabs == null) {
					q.setFailed(true);
					q.setFailMsg("No Tables ");
					continue QUERY_LOOP;
				}
				for (Table tab : tabs) {
					if (tab.getName().startsWith("@"))
						continue;
					mt = md.getMetaTable(tab.getName().toUpperCase());
					if (mt == null) {// when @ or no meta data.
					// throw new RuntimeException("No Tobe Table [SQLID : " + q.getId() + ", SQLKEY : " + qt.getKey() + ", TABLE : " + tab.getName()
					// + "]");
						log.warn("No Tobe Table [SQLID : " + q.getId() + ", SQLKEY : " + qt.getKey() + ", TABLE : " + tab.getName().toUpperCase()
								+ "]");
						q.setFailed(true);
						q.setFailMsg("No Tobe Table for [ " + tab.getName() + "]");
						continue QUERY_LOOP;
					}
					tab.setTobeName(mt.getTobeName());
					qt.putAllMetaColumn(mt.getColumnMap());
					q.putMetaTable(mt);
					if (log.isDebugEnabled())
						log.debug("Add Table [SQLID : " + q.getId() + ", SQLKEY : " + qt.getKey() + ", TABLE : " + tab.getName() + "]");
				}
			}
		}

	}

	/**
	 * 
	 * Desc : supposition : select columns in subquery don't use outer query's columns
	 * @Method Name : applyMetaColumnForSelectColumns
	 * @param querys
	 * @param md
	 */
	private void applyMetaColumnForSelectColumns(List<Query> querys, MetaData md) {
		List<QueryText> subQs = null;
		List<Column> cols = null;
		for (Query q : querys) {
			if (q.isFailed())
				continue;

			subQs = q.getSubsqls();
			if(log.isDebugEnabled())
				log.debug("sqlid : " + q.getId());
			try {
				for (QueryText qt : subQs) {
					if (!"select".equals(qt.getQueryType()))
						continue;
					if (log.isDebugEnabled())
						log.debug("QueryText : " + qt);
					applyMetaColumnForSelectColumns(q, qt);
				}
			} catch (Exception e) {
				q.setFailed(true);
				q.setFailMsg(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private void applyMetaColumnForEtcColumns(List<Query> querys, MetaData md) {
		List<QueryText> subQs = null;
		List<Column> cols = null;
		for (Query q : querys) {
			if (q.isFailed())
				continue;
			subQs = q.getSubsqls();
			for (QueryText qt : subQs) {
				applyMetaColumnForEtcColumns(q, qt);
			}
		}
	}

	private void applyMetaColumnForEtcColumns(Query query, QueryText qt) {
		List<Table> tabs = qt.getTables();
		QueryText child = null;
		QueryText parent = null;
		String colTabAlias = null;
		MetaTable mt = null;
		MetaColumn mc = null;
		List<Column> cols = qt.getColumns();
		for (Column col : cols) {
			colTabAlias = col.getTableAlias();
			if (colTabAlias != null) {
				mt = query.getMetaTable(qt.getTableNameByAlias(colTabAlias));
				// log.debug("colTabAlias : " + mt.toString());
				if (mt != null) {
					mc = mt.getMetaColumn(col.getName().toUpperCase());
				} else if (qt.getParentKey() != null) {
					parent = query.getSubQueryText(qt.getParentKey());
					if (parent == null) {
						throw new RuntimeException("Can't find parent for SQLID : " + query.getId() + ", key : " + qt.getKey() + ", parent key : "
								+ qt.getParentKey());
					}
					mt = query.getMetaTable(parent.getTableNameByAlias(colTabAlias));
					if (mt != null) {
						mc = mt.getMetaColumn(col.getName().toUpperCase());
					}
				}

			} else {
				mc = qt.getMetaColumn(col.getName().toUpperCase());
			}

			if (mc == null) {
				log.warn("No matching meta column [SQLID : " + query.getId() + ", SQLKEY : " + qt.getKey() + ", COLUMN : " + col.toString() + "]");
				continue;
			} else {
				col.setTobeName(mc.getTobeName());
				col.setDesc(mc.getDesc());
				if (log.isDebugEnabled())
					log.debug("Add meta column [SQLID : " + query.getId() + ", SQLKEY : " + qt.getKey() + ", COLUMN : " + col.toString() + "]");
			}

		}
	}

	/**
	 * 
	 * Desc : process for select columns
	 * @Method Name : applyMetaColumnForSelectColumns
	 * @param query
	 * @param qt
	 */
	private void applyMetaColumnForSelectColumns(Query query, QueryText qt) {
		List<Table> tabs = qt.getTables();
		QueryText child = null;
		String colTabAlias = null;
		MetaTable mt = null;
		MetaColumn mc = null;
		boolean isSubquery = qt.isSubquery();

		for (Table t : tabs) {
			if (t.getName().startsWith("@") && query.getMetaTable(t.getName()) == null) {
				child = query.getSubQueryText(t.getName());
				// child.setParentKey(qt.getKey());==> setting divide sql
				applyMetaColumnForSelectColumns(query, child);
			}

		}

		List<Column> selCols = qt.getColumnsInSelect();
		Map<String, MetaColumn> selColMap = new HashMap<String, MetaColumn>();
		for (Column col : selCols) {

			colTabAlias = col.getTableAlias();
			if (colTabAlias != null) {

				// log.debug(colTabAlias + " : " + qt.getTableNameByAlias(colTabAlias));
				mt = query.getMetaTable(qt.getTableNameByAlias(colTabAlias).toUpperCase());
				mc = mt.getMetaColumn(col.getName().toUpperCase());
				// log.debug(mt.toString());

			} else {
				mc = qt.getMetaColumn(col.getName().toUpperCase());
			}

			if (mc == null) {
				log.warn("No matching meta column [SQLID : " + query.getId() + ", SQLKEY : " + qt.getKey() + ", COLUMN : " + col.toString() + "]");
			}

			if (mc != null) {
				col.setTobeName(mc.getTobeName());
				col.setDesc(mc.getDesc());
			}

			if (isSubquery && mc != null) {
				// if (mc == null) {
				// mc = new MetaColumn();
				// mc.setName(col.getName().toUpperCase());
				// mc.setTobeName(col.getTobeName());
				// mc.setDesc("");
				// }
				if (col.getAlias() != null) {
					mc = mc.clone();
					mc.setName(col.getAlias().toUpperCase());
					mc.setTobeName(col.getAlias().toUpperCase());
				}
				if (log.isDebugEnabled()) {
					log.debug("Add meta column [SQLID : " + query.getId() + ", SQLKEY : " + qt.getKey() + ", COLUMN : " + col.toString() + "]");
					log.debug("Add meta column [SQLID : " + query.getId() + ", SQLKEY : " + qt.getKey() + ", COLUMN : " + mc.toString() + "]");
				}
				selColMap.put(mc.getName(), mc);
			}
		}

		if (isSubquery) {
			// add to parent's column map
			QueryText parent = query.getSubQueryText(qt.getParentKey());
			if (parent == null) {
				throw new RuntimeException("Can't find parent for SQLID : " + query.getId() + ", key : " + qt.getKey() + ", parent key : "
						+ qt.getParentKey());
			}

			parent.putAllMetaColumn(selColMap);

			mt = new MetaTable();
			mt.setName(qt.getKey());
			mt.setColumnMap(selColMap);
			query.putMetaTable(mt);
			if (log.isDebugEnabled())
				log.debug("Add MetaTable into for the subquery [" + qt.getKey() + "]  " + mt.toString());
		}
	}

}