package cn.m.util.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.m.util.base.test.BaseTest;

@RunWith(SpringJUnit4ClassRunner.class)
public class ICacheMangerTest extends BaseTest{
	
	@Autowired
	private ICacheManger<String> cache;

	@Test
	public void testSetStringT() {
		cache.set("testSetKey", "testSetKey");
	}

	@Test
	public void testGetString() {
		System.err.println(cache.get("testSetKey"));
	}

}
