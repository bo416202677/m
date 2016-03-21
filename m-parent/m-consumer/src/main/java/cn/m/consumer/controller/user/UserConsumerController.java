package cn.m.consumer.controller.user;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.m.dubbo.test.api.exception.user.UserQueryException;
import cn.m.dubbo.test.api.model.user.User;
import cn.m.dubbo.test.api.service.user.IUserService;
import cn.m.util.enums.ErrorEnum;

@Controller
@RequestMapping("/consumer/user/")
public class UserConsumerController {

	@Autowired
	private IUserService userService;

	@RequestMapping("/getUserByAccount.do")
	@ResponseBody
	public Object getUserByAccount(String account) throws UserQueryException {
		Map<String, Object> result = null;
		User user = userService.getUserByAccount(account);
		result = ErrorEnum.getSuccessMap();
		result.put("user", user);
		return result;
	}

}
