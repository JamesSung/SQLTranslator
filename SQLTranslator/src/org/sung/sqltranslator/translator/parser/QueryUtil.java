package org.sung.sqltranslator.translator.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sung.sqltranslator.translator.vo.QueryText;

public class QueryUtil {
	private static Log log = LogFactory.getLog(QueryUtil.class);

	/**
	 * delimiters for column
	 */
	public static final String[] delims = { " ", ",", "\\/", "\\(", "\\)", "-", "\\+", "\\*", "\\!", "=", ">", "<" };

	// TODO need to add more keyword, not everything, but as many as possible.
	public static final String[] keywds = { "CASE", "WHEN", "ELSE", "THEN", "END", "AND", "IS", "NOT", "NULL", "ROWNUM", "CURRENT_DATE",
			"CURRENT_TIMESTAMP", "AS", "OR", "LEFT", "OUTER", "FETCH", "FIRST", "OPTIMIZE", "FOR", "ROWS", "ROW", "CURRENT DATE",
			"CURRENT TIMESTAMP", "WITH UR", "ONLY", "ORDER", "GROUP", "BY", "IN", "EXISTS" };

	public static final String[] funcs = { "MAX", "SUM", "MIN", "AVG", "COUNT", "TO_DATE", "TO_NUMBER", "TO_CHAR", "SUBSTR", "VALUE", "COALESCE",
			"CHAR", "DATE", "DIGITS", "DECIMAL", "HEX", "RIGHT", "LEFT", "DAYOFWEEK", "ROWNUMBER", "OVER", "INT", "INTEGER", "ADD_MONTHS", "NVL",
			"COALESCE", "YEAR", "LTRIM", "RTRIM", "TRIM", "CAST" };

	/**
	 * 
	 * Desc : remove constants(string,number), variables and keyword.
	 * @Method Name : removeExtras
	 * @param str
	 * @return
	 */
	public static String removeExtras(String str) {

		str = str.replaceAll("\\[", "");
		str = str.replaceAll("\\]", "");

		String tmp = getDelimitedString(str);

		String[] tokens = tmp.split("\\|");
		for (String token : tokens) {
			if (token == null)
				continue;
			
			if (token.startsWith("#")) {

				token = token.replaceAll("\\#", "");
				str = str.replaceAll("\\#" + token + "\\#", "");
			} else if (token.startsWith("$")) {
				token = token.replaceAll("\\$", "");
				str = str.replaceAll("\\$" + token + "\\$", "");
			} else if (token.startsWith("'")) {
				str = str.replaceAll(token, "");
			}

			// don't do this it could cause bug.
			// if (NumberUtils.isDigits(token)) {
			// str = str.replaceAll(token, "");
			// }

		}

		if (log.isDebugEnabled()) {
			log.debug("result: " + str);
		}

		return str;

	}

	public static String getDelimitedString(String str) {
		String tmp = str;

		if (log.isDebugEnabled()) {
			log.debug("input string : " + tmp);
		}

		for (String delim : delims) {
			if ("-".equals(delim)) {
				int size = howMany(tmp, "-");
				int idx = 0;
				for (int i = 0; i < size; i++) {
					idx = tmp.indexOf("-", idx + 1);

					if (size == 2 && ("'".equals(tmp.substring(idx - 5, idx - 4)) || "'".equals(tmp.substring(idx + 3, idx + 4))))
						continue;
					else
						tmp = tmp.substring(0, idx) + "|" + tmp.substring(idx + 1);
				}

				continue;
			}

			tmp = tmp.replaceAll(delim, "|");
		}

		if (log.isDebugEnabled()) {
			log.debug("delimiter applyed : " + tmp);
		}

		return tmp;
	}

	public static int howMany(String base, String find) {
		String[] arr = base.split(find);
		if ("END".equals(find)) {
			log.debug(" count : " + (arr.length - 1));
		}

		if (arr == null)
			return 0;
		return arr.length - 1;
	}


	public static String removeKeyWords(String str) {

		return removeKeyWords(str, keywds);
	}

	public static String removeKeyWords(String str, String[] keywds) {
		if (log.isDebugEnabled()) {
			log.debug("input : " + str);
		}
		for (String keywd : keywds) {
			str = removeKeyword(str, keywd);
			str = removeKeyword(str, keywd.toLowerCase());
		}
		if (log.isDebugEnabled()) {
			log.debug("result : " + str);
		}
		return str;
	}

	public static String removeFunctions(String str) {
		if (log.isDebugEnabled()) {
			log.debug("input : " + str);
		}
		for (String func : funcs) {
			str = removeFunction(str, func);
			str = removeFunction(str, func.toLowerCase());
		}
		if (log.isDebugEnabled()) {
			log.debug("result : " + str);
		}
		return str;
	}

	private static String removeFunction(String sql, String keywd) {
		int kCnt = howMany(sql, keywd);
		int kIdx = -1;
		int kLen = 0;
		int bIdx = -1;
		String brace = null;
		for (int i = 0; i < kCnt; i++) {
			kIdx = sql.indexOf(keywd);
			kLen = keywd.length();
			bIdx = sql.indexOf("(", kIdx + kLen);
			if (bIdx < 0)
				continue;

			brace = sql.substring(kIdx + kLen, bIdx + 1).trim();
			if (isDelimiter(sql.substring(kIdx - 1, kIdx)) && "(".equals(brace)) {
				sql = sql.substring(0, kIdx) + sql.substring(kIdx + kLen);
			} else {
				continue;
			}

		}

		return sql;
	}

	private static String removeKeyword(String sql, String keywd) {
		int kCnt = howMany(sql, keywd);
		int kIdx = -1;
		int kLen = 0;
		for (int i = 0; i < kCnt; i++) {
			kIdx = sql.indexOf(keywd);
			kLen = keywd.length();
			if (isDelimiter(sql.substring(kIdx - 1, kIdx)) && isDelimiter(sql.substring(kIdx + kLen, kIdx + kLen + 1))) {
					sql = sql.substring(0, kIdx) + sql.substring(kIdx + kLen);
			} else {
				continue;
			}

		}

		return sql;
	}

	private static boolean isDelimiter(String str) {
		for (String delim : delims) {
			if (delim.indexOf(str) >= 0)
				return true;
		}

		return false;
	}

	/**
	 * 
	 * Desc : replace commas in brace with a space
	 * @Method Name : removeCommaInBrace
	 * @param str
	 * @return
	 */
	public static String removeCommaInBrace(String str) {
		int openIdx = str.indexOf("(");
		int closeIdx = -1;

		if (log.isDebugEnabled()) {
			log.debug("input : " + str);
		}

		while (openIdx >= 0) {
			closeIdx = findEndIndexOfCloseBrace(str, openIdx);
			str = str.substring(0, openIdx) + str.substring(openIdx, closeIdx).replaceAll(",", " ") + str.substring(closeIdx);
			openIdx = str.indexOf("(", closeIdx);
		}

		if (log.isDebugEnabled()) {
			log.debug("result : " + str);
		}

		return str;
	}

	private static int findEndIndexOfCloseBrace(String sql, int baseIndex) {

		int endIdx = sql.indexOf(")", baseIndex);
		if (endIdx > 0) {
			// System.out.println("baseIndex : " + baseIndex);
			// System.out.println("endIdx : " + endIdx);
			String subText = sql.substring(sql.indexOf("(") + 1, endIdx);// exclude brace
			// System.out.println("subText : " + subText);

			int openCnt = howMany(subText, "\\(");
			int closeCnt = howMany(subText, "\\)");
			if (openCnt == closeCnt)
				return endIdx;
			else
				return findEndIndexOfCloseBrace(sql, endIdx + 1);
		}

		return endIdx;
	}

	public static String removeComment(String sql) {
		int sIdx = sql.indexOf("/*");
		int eIdx = sql.indexOf("*/");
		if (log.isDebugEnabled()) {
			log.debug("input : " + sql);
		}

		while (sIdx >= 0) {
			sql = sql.substring(0, sIdx) + sql.substring(eIdx + 2);
			sIdx = sql.indexOf("/*");
			eIdx = sql.indexOf("*/");
		}

		if (log.isDebugEnabled()) {
			log.debug("result : " + sql);
		}

		return sql;
	}

	public static List orderedList(List list, String[] orderAttrs) {
		if (list == null) {
			return new ArrayList();
		}
		ComparatorChain comparatorChain = new ComparatorChain();

		if ((orderAttrs == null) || (orderAttrs.length <= 0))
			return list;

		for (int i = 0; i < orderAttrs.length; ++i) {
			comparatorChain.addComparator(new BeanComparator(orderAttrs[i]));
		}

		Object[] orderedValues = list.toArray();
		Arrays.sort(orderedValues, comparatorChain);

		return Arrays.asList(orderedValues);
	}

	public static List reverseOrderedList(List list, String[] orderAttrs) {

		List orderedList = orderedList(list, orderAttrs);

		List rstList = new ArrayList();

		int size = orderedList.size();
		QueryText qt = null;
		for (int i = size - 1; i >= 0; i--) {
			rstList.add(orderedList.get(i));
		}

		return rstList;
	}

}
