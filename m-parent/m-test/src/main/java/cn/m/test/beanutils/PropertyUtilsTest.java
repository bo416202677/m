package cn.m.test.beanutils;

import org.springframework.beans.BeanUtils;

public class PropertyUtilsTest {

	public static void main(String[] args) {
		ClassA a = new ClassA(12L, "aaaa", 1, 28);
		ClassC c = new ClassC();
		c.setAdd("addhahahah");
		a.setC(c);
		ClassB b = new ClassB();
		BeanUtils.copyProperties(a, b);
		System.err.println(a.toString());
		System.err.println(b.toString());
		
	}

}
