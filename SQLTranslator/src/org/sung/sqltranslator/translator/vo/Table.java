package org.sung.sqltranslator.translator.vo;

public class Table {
	private String name;

	private String alias;

	private String tobeName;

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

	public boolean isSubquery() {
		return name.startsWith("@");
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

}