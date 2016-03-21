package cn.m.web.controller.user;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.m.domain.user.UserQueryRequestParams;
import cn.m.dubbo.test.api.exception.user.UserQueryException;
import cn.m.dubbo.test.api.model.user.User;
import cn.m.dubbo.test.api.service.user.IUserService;
import cn.m.util.enums.ErrorEnum;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private IUserService userService;

	@RequestMapping("/getUserByAccount.do")
	@ResponseBody
	public Object getUserByAccount(UserQueryRequestParams params) throws UserQueryException {
		Map<String, Object> result = null;
		User user = userService.getUserByAccount(params.getAccount());
		result = ErrorEnum.getSuccessMap();
		result.put("user", user);
		return result;
	}

}
