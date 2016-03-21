package cn.m.util.enums;

public enum DatePatternEnum {

	YYYY_MM_DD_HH_MM_DD("yyyy-MM-dd HH:mm:ss"),
	YYYY_MM_DD_HH_MM_DD_SSS("yyyy-MM-dd HH:mm:ss.SSS"),
	YYYY_MM_DD("yyyy-MM-dd"),
	YYYYMMDD("yyyyMMdd");

	private String pattern;

	private DatePatternEnum(String pattern) {
		this.pattern = pattern;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

}
