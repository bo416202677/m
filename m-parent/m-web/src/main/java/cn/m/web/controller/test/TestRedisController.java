package cn.m.web.controller.test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.m.util.cache.ICacheManger;

@Controller
@RequestMapping("/redis/")
public class TestRedisController {
	
	@Autowired
	private ICacheManger<Object> redisCache;
	
	@RequestMapping("get.do")
	@ResponseBody
	public Object get(@RequestParam String key){
		Map<String, Object> result = new HashMap<String, Object>();
		Object val = redisCache.get(key);
		result.put(key, val);
		return result;
	}

}
