package cn.m.dubbo.test.provider.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.m.dubbo.test.api.exception.user.UserQueryException;
import cn.m.dubbo.test.api.model.user.User;
import cn.m.dubbo.test.api.service.user.IUserService;
import cn.m.dubbo.test.provider.dao.user.IUserDao;

@Service("userService")
public class UserServiceImpl implements IUserService{
	
	@Autowired
	private IUserDao userDao;

	@Override
	public User getUserByAccount(String account) throws UserQueryException {
		return userDao.getUserByAccount(account);
	}

}
