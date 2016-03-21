package cn.m.dubbo.test.api.model.user;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String headImage;

	private String nickname;

	private String personalitySignature;

	private String account;

	private String phoneNumber;

	private Date createTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getHeadImage() {
		return headImage;
	}

	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPersonalitySignature() {
		return personalitySignature;
	}

	public void setPersonalitySignature(String personalitySignature) {
		this.personalitySignature = personalitySignature;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", headImage=" + headImage + ", nickname="
				+ nickname + ", personalitySignature=" + personalitySignature
				+ ", account=" + account + ", phoneNumber=" + phoneNumber
				+ ", createTime=" + createTime + "]";
	}

}
