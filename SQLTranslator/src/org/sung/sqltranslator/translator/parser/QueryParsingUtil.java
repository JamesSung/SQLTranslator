package org.sung.sqltranslator.translator.parser;

import java.util.List;

import org.sung.sqltranslator.translator.vo.Query;
import org.sung.sqltranslator.translator.vo.QueryText;


public class QueryParsingUtil {

	public static void parse(Query query) {
		List<QueryText> subQuerys = query.getSubsqls();
		QueryParser paser = null;
		for (QueryText qt : subQuerys) {
			paser = qt.getParser();
			if (paser == null) {
				// QueryParser cann't be null at this step.
				throw new RuntimeException("SQL  ID : " + query.getId() + " => SUBQUERY(" + qt.getText() + ") has no QueryParser!!");
			}

			paser.parse(qt);

			query.putAllTableNameMap(qt.getTableNmMap());
		}
	}

}