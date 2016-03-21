package cn.m.util.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.m.util.enums.DatePatternEnum;

public abstract class DateUtil {

	private static final Map<String, SimpleDateFormat> DATE_FORMAT_MAP = new HashMap<String, SimpleDateFormat>();

	/**
	 * Gets the time difference between the specified time and the current time.
	 * 
	 * @param start
	 * @return
	 */
	public static long getDiffTimeToCurForSpecifiedTime(long start) {
		return System.currentTimeMillis() - start;
	}

	/**
	 * 将日期转换为默认格式的时间字符串(yyyy-MM-dd HH:mm:ss)
	 * 
	 * @param date
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String format(Date date) {
		Assert.notNull(date);
		return format(date, DatePatternEnum.YYYY_MM_DD_HH_MM_DD.getPattern());
	}

	/**
	 * 将日期转换为制定格式的时间字符串
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String format(Date date, String pattern) {
		Assert.notNull(date);
		Assert.hasText(pattern);
		return getSimpleDateFormat(pattern).format(date);
	}

	/**
	 * 将日期字符串按默认的日期格式(yyyy-MM-dd HH:mm:ss)转换为Date对象
	 * 
	 * @param dateStr
	 * @return
	 * @throws IllegalArgumentException
	 * @throws ParseException
	 */
	public static Date parse(String dateStr) throws ParseException {
		Assert.hasText(dateStr);
		return parse(dateStr, DatePatternEnum.YYYY_MM_DD_HH_MM_DD.getPattern());
	}

	/**
	 * 将指定格式的字符串日期转为Date对象
	 * 
	 * @param dateStr
	 * @param pattern
	 * @return
	 * @throws ParseException
	 * @throws IllegalArgumentException
	 */
	public static Date parse(String dateStr, String pattern) throws ParseException {
		Assert.hasText(dateStr);
		return getSimpleDateFormat(pattern).parse(dateStr);
	}

	/**
	 * 获取指定格式的时间转换类
	 * 
	 * @param pattern
	 * @return
	 */
	private static SimpleDateFormat getSimpleDateFormat(String pattern) {
		if (DATE_FORMAT_MAP.containsKey(pattern)) {
			return DATE_FORMAT_MAP.get(pattern);
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		DATE_FORMAT_MAP.put(pattern, sdf);
		return sdf;
	}

}
