package cn.m.http.test.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import cn.m.test.http.HttpTools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


public class BaseRequest {
//	private static String baseUrl ="http://localhost:8080/yellowcircles/";
	private static String baseUrl ="http://120.26.116.103:8080/";
//	private static String baseUrl ="http://121.43.147.67:8080/";
//	private static String baseUrl ="http://120.26.135.6:8097/";
//	private static String baseUrl ="http://o-w.fengnian.cn/";
	
	private Map<String, String > params =new HashMap<String, String >();
	public BaseRequest() {
		params.put("debug","true");
	}
	
	public String excuse(String url){
		HttpTools tools = new HttpTools();
		String str=null;
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		//非公共请求参数
		for(Map.Entry<String, String> entry:params.entrySet()){
			formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		try {
			str = tools.excute(baseUrl+url,formparams);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return str;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	
	public static Map<String, String> getResult(Map<String, Map<String, String>> arrary){
		Map<String, String> resultMap = new HashMap<String, String>();
		for(Map.Entry<String, Map<String, String>> e:arrary.entrySet()){
			BaseRequest request = new BaseRequest();
			Map<String, String> map = e.getValue();
			for(Map.Entry<String, String> param:map.entrySet()){
				request.getParams().put(param.getKey(),param.getValue());
			}
			String result = request.excuse(e.getKey());
			if(!StringUtils.isNotBlank(result)){
				resultMap.put(e.getKey(), "请求返回内容为空");
			}else{
				JSONObject obj =  JSON.parseObject(result);
				resultMap.put(e.getKey(), result);
				if("success".equals(obj.getString("result"))){
					//成功返回
					System.out.println(e.getKey()+":"+result);
				}else{
					System.err.println(e.getKey()+":"+result);
				}
			}
			
		}
		return resultMap;
	}
	
}
