package org.sung.sqltranslator.translator.vo;

public class MetaColumn implements Cloneable {
	private String name;

	private String tobeName;

	private String desc;

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

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("name : ").append(name).append(", tobe : ").append(tobeName).append(", desc : ").append(desc).append("\r\n");

		return sb.toString();
	}

	@Override
	public MetaColumn clone() {
		MetaColumn mc = new MetaColumn();
		mc.setDesc(desc);
		mc.setName(name);
		mc.setTobeName(tobeName);
		return mc;
	}
}
