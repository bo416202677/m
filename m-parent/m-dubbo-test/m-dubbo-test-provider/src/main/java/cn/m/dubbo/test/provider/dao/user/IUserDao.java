package cn.m.dubbo.test.provider.dao.user;

import cn.m.dubbo.test.api.model.user.User;

public interface IUserDao {

	/**
	 * 通过帐号查询用户信息
	 * @param account
	 * @return
	 */
	User getUserByAccount(String account);
}
