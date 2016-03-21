package cn.m.util.utils;

import org.apache.commons.lang.StringUtils;

public abstract class ParamCheckUtil extends StringUtils {

	/**
	 * 校验vals不为空
	 * 
	 * @param vals
	 * @throws IllegalArgumentException
	 */
	public static void checkStringNotEmpty(String... vals) {
		if (vals == null)
			throw new IllegalArgumentException("vals is null!");
		for (String val : vals) {
			if (isEmpty(val))
				throw new IllegalArgumentException("val is null or Empty!");
		}
	}

	/**
	 * 校验对象不为null never use it to check String param
	 * 
	 * @param objects
	 * @throws IllegalArgumentException
	 */
	public static void checkObjectNotNull(Object... objects) {
		if (objects == null)
			throw new IllegalArgumentException("objects is null!");
		for (Object obj : objects) {
			if (obj == null)
				throw new IllegalArgumentException("obj is null!");
		}
	}

}
