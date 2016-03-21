package cn.m.web.controller.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.m.domain.user.UserQueryRequestParams;

@Controller
@RequestMapping("/param/")
public class TestParamController {
	
	private static final Logger LOGGER = Logger.getLogger(TestParamController.class);
	
	@ResponseBody
	@RequestMapping("/passString.do")
	public Object passString(String id){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("id", id);
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/passInteger.do")
	public Object passInteger(@RequestParam("id") Integer id){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("id", id);
		return result;
	}

	@ResponseBody
	@RequestMapping("/passObject.do")
	public Object passObject(UserQueryRequestParams user){
		LOGGER.info("passObject params:" + user);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("user", user);
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/passArray.do")
	public Object passArray(@RequestParam("arr[]") byte[] arr){
		LOGGER.info("passObject params:" + arr);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("arr", arr);
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/passCollection.do")
	public Object passCollection(ArrayList<String> col){
		LOGGER.info("passObject params:" + col);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("arr", col);
		return result;
	}
}
