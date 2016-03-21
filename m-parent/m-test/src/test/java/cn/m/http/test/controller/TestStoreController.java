package cn.m.http.test.controller;

import java.util.HashMap;
import java.util.Map;

import cn.m.http.test.base.BaseRequest;

public class TestStoreController {
	
@SuppressWarnings("serial")
static final Map<String,Map<String, String>> arrary = new HashMap<String,Map<String, String>>(){{
		
		put("storeList.do",new HashMap<String, String>(){{
			put("account","1o0dp3jay66mnohf");
			put("sign","1");
			put("pageNo","1");
			put("pageSize","10");
		}});
		
	}} ; 
	public static void main(String[] args) {
		BaseRequest.getResult(arrary);
	}
}
