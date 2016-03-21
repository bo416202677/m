package cn.m.http.test.controller;

import java.util.HashMap;
import java.util.Map;

import cn.m.http.test.base.BaseRequest;

public class TestMessageController {

	@SuppressWarnings("serial")
	static final Map<String, Map<String, String>> arrary = new HashMap<String, Map<String, String>>() {
		{
			put("message/listInteractMessage.do",
					new HashMap<String, String>() {
						{
							put("account", "4p21ohewh7b80cil");
							put("messageId", "1000000");
						}
					});

		}
	};

	public static void main(String[] args) {
		BaseRequest.getResult(arrary);
	}
}
