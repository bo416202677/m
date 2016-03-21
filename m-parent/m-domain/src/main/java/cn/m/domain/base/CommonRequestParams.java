package cn.m.domain.base;

/**
 * 公共请求参数
 * 
 * @author hadoop
 *
 */
public class CommonRequestParams {

	/**
	 * 接口跟踪时间戳
	 */
	private Long mthTracTime = 0l;

	/**
	 * 用户id
	 */
	private String account;

	public Long getMthTracTime() {
		return mthTracTime;
	}

	public void setMthTracTime(Long mthTracTime) {
		this.mthTracTime = mthTracTime;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Override
	public String toString() {
		return "CommonRequestParams [mthTracTime=" + mthTracTime + ", account="
				+ account + "]";
	}

}
