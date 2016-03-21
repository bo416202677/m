package cn.m.test.http;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpTools {

	public String excute(String url,List<NameValuePair> formparams)throws Exception{
		CloseableHttpClient httpClient =  HttpClients.createDefault();
		try{
			UrlEncodedFormEntity reqEntity = new UrlEncodedFormEntity(formparams, "UTF-8"); 
			HttpPost post = new HttpPost(url);
			post.setEntity(reqEntity);
			
			HttpResponse httpResponse = httpClient.execute(post);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if(statusCode == 200){
				HttpEntity entity = httpResponse.getEntity();
				if(entity != null){
					return EntityUtils.toString(entity,"UTF-8");
				}
			}else{
				System.out.println(httpResponse.toString());
				throw new Exception("statusCode = " + statusCode);
			}
		}finally{
			if(httpClient != null){
				httpClient.close();
			}
		}
		return null;
	}
	
}
