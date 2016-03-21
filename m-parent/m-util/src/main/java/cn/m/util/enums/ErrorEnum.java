package cn.m.util.enums;

import java.util.HashMap;
import java.util.Map;

public enum ErrorEnum {

	SUCCESS(0, "成功"),
	ERR_PARAMETER_ILLEGAL(1, "参数不合法"),
	ERR_SYS_FAIL(2, "系统异常"),
	ERR_NO_POWER(3, "无权限执行此操作"),
	ERR_TOKEN_FAIL(4, "token 失效");

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMess() {
		return mess;
	}

	public void setMess(String mess) {
		this.mess = mess;
	}

	private int code;

	private String mess;

	private ErrorEnum(int code, String mess) {
		this.code = code;
		this.mess = mess;
	}

	public static Map<String, Object> getSuccessMap() {
		return getErrorMap(ErrorEnum.SUCCESS);
	}

	public static Map<String, Object> getErrorMap(ErrorEnum error) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", error.getCode());
		map.put("mess", error.getMess());
		return map;
	}
}
