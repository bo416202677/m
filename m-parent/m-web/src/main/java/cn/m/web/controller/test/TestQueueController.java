package cn.m.web.controller.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/queue")
public class TestQueueController {
	
	private static ArrayBlockingQueue<String> QUEUE = new ArrayBlockingQueue<String>(2);

	@RequestMapping("/put.do")
	@ResponseBody
	public Object put(@RequestParam String q) throws InterruptedException{
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("before queue members", QUEUE.toString());
		QUEUE.put(q);
		result.put("after queue members", QUEUE.toString());
		return result;
	}
	
	@RequestMapping("/take.do")
	@ResponseBody
	public Object take() throws InterruptedException {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("before queue members", QUEUE.toString());
		result.put("result", QUEUE.take());
		result.put("after queue members", QUEUE.toString());
		return result;
	}
}
