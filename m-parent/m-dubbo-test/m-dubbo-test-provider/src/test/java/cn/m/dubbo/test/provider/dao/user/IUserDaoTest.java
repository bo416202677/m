package cn.m.dubbo.test.provider.dao.user;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.m.dubbo.test.base.BaseTest;

@RunWith(SpringJUnit4ClassRunner.class)
public class IUserDaoTest extends BaseTest{
	
	@Autowired
	private IUserDao userDao;

	@Test
	public void testGetUserByAccount() {
		System.err.println(userDao.getUserByAccount("mkbqzs1t3ibsmzo3"));
	}

}
