package cn.m.util.utils;

import java.util.Set;

import org.apache.log4j.Logger;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.ClientConfig;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;

public abstract class MessagePushUtil {

	private String masterSecret;

	private String appkey;

	private int maxRetryTimes;

	private boolean isApnsProduction;

	/**
	 *  推送类型,1:通知 2:自定义(默认通知)
	 */
	private String pushType = PUSH_TYPE_CUSTOMER;
	
	/**
	 * 通知消息推送类型
	 */
	public static final String PUSH_TYPE_NOTIFICATION = "1";
	
	/**
	 * 自定义消息推送类型
	 */
	public static final String PUSH_TYPE_CUSTOMER = "2";

	private static final Logger LOG = Logger.getLogger(MessagePushUtil.class);

	private JPushClient jpushClient;

	private String message;

	public MessagePushUtil(String masterSecret, String appkey, int maxRetryTimes,
			boolean isApnsProduction) {
		this.masterSecret = masterSecret;
		this.appkey = appkey;
		this.maxRetryTimes = maxRetryTimes;
		this.isApnsProduction = isApnsProduction;
	}

	/**
	 * 向所有人发送消息
	 * 
	 * @return 消息id
	 */
	public long sendPushAll() {
		init();
		PushPayload payload = buildPushObject_all_all_alert();
		long msgId = 0;
		try {
			PushResult result = jpushClient.sendPush(payload);
			msgId = result.msg_id;
		} catch (APIConnectionException e) {
			LOG.error("Connection error. Should retry later. ", e);
		} catch (APIRequestException e) {
			LOG.info("HTTP Status: " + e.getStatus());
			msgId = e.getMsgId();
		}
		return msgId;
	}

	/**
	 * 向指定别名的客户端发送消息
	 * 
	 * @param alias
	 *            所有别名信息集合，这里表示发送所有登陆账号
	 * @return 消息id
	 */
	public long sendPushAlias(Set<String> alias) {
		init();
		PushPayload payloadAlias = buildPushObject_ios_alias_alertWithTitle(alias);
		long msgId = 0;
		try {
			PushResult result = jpushClient.sendPush(payloadAlias);
			LOG.info("jiguang server return mes : " + result.toString());
			msgId = result.msg_id;
		} catch (APIConnectionException e) {
			LOG.error("Connection error. Should retry later. ", e);
		} catch (APIRequestException e) {
			LOG.info("HTTP Status: " + e.getStatus());
			LOG.info("Error Code: " + e.getErrorCode());
			LOG.info("Error Message: " + e.getErrorMessage());
			LOG.info("Msg ID: " + e.getMsgId());
			msgId = e.getMsgId();
		}
		return msgId;
	}

	/**
	 * 向指定组发送消息
	 * 
	 * @param tag
	 *            组名称
	 * @return 消息id
	 */
	public long sendPushTag(String tag) {
		init();
		PushPayload payloadtag = buildPushObject_ios_tag_alertWithTitle(tag);
		long msgId = 0;
		try {
			PushResult result = jpushClient.sendPush(payloadtag);
			msgId = result.msg_id;
			LOG.info("Got result - " + result);
		} catch (APIConnectionException e) {
			LOG.error("Connection error. Should retry later. ", e);

		} catch (APIRequestException e) {
			LOG.info("HTTP Status: " + e.getStatus());
			LOG.info("Error Code: " + e.getErrorCode());
			LOG.info("Error Message: " + e.getErrorMessage());
			LOG.info("Msg ID: " + e.getMsgId());
			msgId = e.getMsgId();
		}
		return msgId;
	}

	/**
	 * 下列封装了三种获得消息推送对象（PushPayload）的方法
	 * buildPushObject_android_alias_alertWithTitle、
	 * buildPushObject_android_tag_alertWithTitle、 buildPushObject_all_all_alert
	 */
	public PushPayload buildPushObject_ios_alias_alertWithTitle(
			Set<String> alias) {
		PushPayload.Builder builder = PushPayload.newBuilder().setPlatform(Platform.all()).setAudience(Audience.alias(alias))
				.setOptions(Options.newBuilder().setApnsProduction(isApnsProduction).build());
		if(PUSH_TYPE_CUSTOMER.equals(pushType)){
			builder.setMessage(Message.content(message));
		}else{
			builder.setNotification(Notification.newBuilder().setAlert(message).build());
		}
		return builder.build();
	}

	public PushPayload buildPushObject_ios_tag_alertWithTitle(String tag) {
		PushPayload.Builder builder = PushPayload.newBuilder().setPlatform(Platform.all()).setAudience(Audience.tag(tag))
				.setOptions(Options.newBuilder().setApnsProduction(isApnsProduction).build());
		if(PUSH_TYPE_CUSTOMER.equals(pushType)){
			builder.setMessage(Message.content(message));
		}else{
			builder.setNotification(Notification.newBuilder().setAlert(message).build());
		}
		return builder.build();
	}

	public PushPayload buildPushObject_all_all_alert() {
		return PushPayload.alertAll(message);
	}

	public String getMasterSecret() {
		return masterSecret;
	}

	public void setMasterSecret(String masterSecret) {
		this.masterSecret = masterSecret;
	}

	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	public int getMaxRetryTimes() {
		return maxRetryTimes;
	}

	public void setMaxRetryTimes(int maxRetryTimes) {
		this.maxRetryTimes = maxRetryTimes;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * 推送类型,1:通知 2:自定义(默认通知)
	 * @return
	 */
	public String getPushType() {
		return pushType;
	}

	/**
	 * 推送类型,1:通知 2:自定义(默认通知)
	 * @param pushType
	 */
	public void setPushType(String pushType) {
		this.pushType = pushType;
	}

	private void init() {
		if (jpushClient == null){
			ClientConfig conf = ClientConfig.getInstance();
			conf.setMaxRetryTimes(maxRetryTimes);;
			jpushClient = new JPushClient(masterSecret, appkey, null, conf);
		}
	}

}
