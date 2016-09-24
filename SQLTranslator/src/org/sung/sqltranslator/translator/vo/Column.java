package org.sung.sqltranslator.translator.vo;

public class Column implements Cloneable {
	
	/**
	 * A column name using in 'select', 'where' and 'join' clause.</br> A column name exclude table alias.
	 */
	private String name;

	private int nameLenth;

	private String tableAlias;

	/**
	 * A column alias using in select clause;
	 */
	private String alias;

	private String tobeName;

	private String desc;

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
	 * @return the nameLenth
	 */
	public int getNameLenth() {
		return nameLenth;
	}


	/**
	 * @return the tableAlias
	 */
	public String getTableAlias() {
		return tableAlias;
	}

	/**
	 * @param tableAlias the tableAlias to set
	 */
	public void setTableAlias(String tableAlias) {
		this.tableAlias = tableAlias;
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
		if (name != null)
			this.nameLenth = name.length();
	}

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

	public String getFullName() {
		if (this.tableAlias == null)
			return this.name;
		else
			return this.tableAlias + "." + this.name;
	}

	public String getFullNameForReplace() {
		if (this.tableAlias == null)
			return this.name;
		else
			return this.tableAlias + "\\." + this.name;
	}

	public String getFullTobeName() {
		if (this.tableAlias == null)
			return this.tobeName;
		else
			return this.tableAlias + "." + this.tobeName;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("name : ").append(name).append(", table alias : ").append(tableAlias).append(", alias : ").append(alias).append(", tobeName : ")
				.append(tobeName).append(", desc : ").append(desc).append("\r\n");

		return sb.toString();

	}

}