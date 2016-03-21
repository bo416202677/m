package cn.m.util.constants;

public enum CacheConstantEnum {

	MAX_CACHE_SEC(30 * 86400, "30天"),
	MIN_CACHE_SEC(60, "一分钟"),
	DEFAULT_CACHE_SEC(86400, "一天"),
	FOREVER_CACHE_SEC(-1, "不设置缓存时间"),
	ONE_WEEK_CACHE_SEC(7 * 86400, "一周"),
	HAFL_MONTH_CACHE_SEC(15 * 86400, "半个月")
	;

	private int cacheTime;

	private String des;

	private CacheConstantEnum(int cacheTime, String des) {
		this.cacheTime = cacheTime;
		this.des = des;
	}

	public int getCacheTime() {
		return cacheTime;
	}

	public String getDes() {
		return des;
	}

}
