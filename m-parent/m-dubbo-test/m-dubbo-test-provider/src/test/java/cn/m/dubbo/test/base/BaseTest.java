package cn.m.dubbo.test.base;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = { "classpath:spring.xml",
		"classpath:spring-mybatis.xml" })
public class BaseTest {

}
