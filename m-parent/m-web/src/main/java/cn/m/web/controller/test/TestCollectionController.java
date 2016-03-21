package cn.m.web.controller.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/collection")
public class TestCollectionController {
	
	@RequestMapping("/shuffle.do")
	@ResponseBody
	public Object shuffle(){
		Map<String, Object> result = new HashMap<String, Object>(5, 1.2F);
		List<Integer> list = new LinkedList<Integer>();
		for(int i = 0; i < 10; i++){
			list.add(i);
		}
		Collections.shuffle(list);
		result.put("result", list);
		return result;
	}

}
