package cn.m.util.utils;

import org.springframework.util.StringUtils;

public abstract class Assert extends org.springframework.util.Assert{

	/**
	 * Assert that an String array has text; that is, it must not be
	 * {@code null} and must have at least one element which has
	 * @param texts
	 */
	public static void hasText(String... texts) {
		hasText("[Assertion failed] - this String argument must have text; it must not be null, empty, or blank", texts);
	}
	
	/**
	 * Assert that an array has elements; that is, it must not be
	 * {@code null} and must have at least one element.
	 * <pre class="code">Assert.notEmpty(array, "The array must have elements");</pre>
	 * @param array the array to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the object array is {@code null} or has no elements
	 */
	public static void hasText(String message, String... texts) {
		notEmpty(texts);
		for(String text : texts){
			if (!StringUtils.hasText(text)) {
				throw new IllegalArgumentException(message);
			}
		}
	}
}
