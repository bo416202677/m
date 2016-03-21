package cn.m.dubbo.test.provider.service.user.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.m.dubbo.test.api.exception.user.UserQueryException;
import cn.m.dubbo.test.api.service.user.IUserService;
import cn.m.dubbo.test.base.BaseTest;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserServiceImplTest extends BaseTest{
	
	@Autowired
	private IUserService userService;

	@Test
	public void testGetUserByAccount() throws UserQueryException {
		System.err.println(userService.getUserByAccount("mkbqzs1t3ibsmzo3"));
	}

}
