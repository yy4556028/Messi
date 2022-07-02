package com.yuyang.messi.bean;

public class UserBean {

	public static final String TAG = UserBean.class.getSimpleName();

	private String accountType;

	// 用户名
	private String username;
	// 别名
	private String alias;
	// 头像 url
	private String avatar_url;
	// 电话
	private String phone_number;
	// 背景图片
	private String background_url;
	// token
	private String token;
	// 坐标
	private Double latitude;
	// 坐标
	private Double longitude;
	// 性别
	private Short gender;
	private String remote_id;
	private Boolean is_official;

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getAvatar_url() {
		return avatar_url;
	}

	public void setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	public String getBackground_url() {
		return background_url;
	}

	public void setBackground_url(String background_url) {
		this.background_url = background_url;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Short getGender() {
		return gender;
	}

	public void setGender(Short gender) {
		this.gender = gender;
	}

	public String getRemote_id() {
		return remote_id;
	}

	public void setRemote_id(String remote_id) {
		this.remote_id = remote_id;
	}

	public Boolean getIs_official() {
		return is_official;
	}

	public void setIs_official(Boolean is_official) {
		this.is_official = is_official;
	}

}
