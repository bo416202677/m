package cn.m.web.controller.mq;

import java.util.Date;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.m.util.enums.DatePatternEnum;
import cn.m.util.enums.ErrorEnum;
import cn.m.util.utils.DateUtil;

@Controller
@RequestMapping("/mq/sender")
public class SenderController {

	@Autowired
	private JmsTemplate jmsTemplate;

	@RequestMapping("/send.do")
	@ResponseBody
	public Object send(final String mess) {
		Map<String, Object> result = null;
		jmsTemplate.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				MapMessage message = session.createMapMessage();
				message.setString("message", "current system time: "
						+DateUtil.format(new Date(), DatePatternEnum.YYYY_MM_DD_HH_MM_DD_SSS.getPattern()) + ", mess:" + mess);

				return message;
			}
		});
		result = ErrorEnum.getSuccessMap();
		result.put("mess", mess);
		result.put("time", DateUtil.format(new Date(), DatePatternEnum.YYYY_MM_DD_HH_MM_DD_SSS.getPattern()));
		return result;
	}

}
