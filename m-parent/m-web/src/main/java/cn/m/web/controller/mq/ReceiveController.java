package cn.m.web.controller.mq;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.m.util.enums.ErrorEnum;

/**
 * 
 * @author hadoop
 *
 */
@Controller
@RequestMapping("/mq/receiver")
public class ReceiveController {
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@RequestMapping("/receive.do")
	@ResponseBody
	public Object receive(){
		Map<String, Object> result = null;
		@SuppressWarnings("unchecked")
		Map<String, Object> mess = (Map<String, Object>) jmsTemplate.receiveAndConvert();
		result = ErrorEnum.getSuccessMap();
		result.putAll(mess);
		return result;
	}

}
