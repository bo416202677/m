package cn.m.dubbo.test.api.service.user;

import cn.m.dubbo.test.api.exception.user.UserQueryException;
import cn.m.dubbo.test.api.model.user.User;

public interface IUserService {

	User getUserByAccount(String account) throws UserQueryException;
}
