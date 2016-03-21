package cn.m.util.utils;

public abstract class CommonUtil {

	/**
	 * 获取方法执行时间戳 if cusTimeStamp is null or zero, return serverTimestamp
	 * otherwise return cusTimeStamp
	 * 
	 * @param cusTimeStamp
	 *            客户端接口跟踪时间戳
	 * @param serverTimestamp
	 *            服务端方法跟踪时间戳
	 * @return
	 */
	public static long getMethodTrackingTimeStamp(String cusTimeStamp,
			long serverTimestamp) {
		if (StringUtil.isBlank(cusTimeStamp)) {
			return serverTimestamp;
		}
		try {
			return Long.getLong(cusTimeStamp);
		} catch (Exception e) {
			return serverTimestamp;
		}
	}
}
