package cn.m.util.base.test;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = { "classpath:spring.xml",
		"classpath:redis-config.xml" })
public class BaseTest {

}
