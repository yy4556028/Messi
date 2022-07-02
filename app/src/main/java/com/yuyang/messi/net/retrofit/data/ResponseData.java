package com.yuyang.messi.net.retrofit.data;

import com.google.gson.annotations.SerializedName;

public class ResponseData<T> {

    /**
     * 返回Code
     */
    @SerializedName(value = "returnCode", alternate = {"errorCode", "code"})
    private String returnCode;

    /**
     * 返回消息
     */
    @SerializedName(value = "returnMsg", alternate = {"errorMsg", "msg"})
    private String returnMsg;

    private String loginStatus;

    /**
     * 数据
     */
    @SerializedName(value = "data", alternate = {"list", "ztmdno", "remarkList", "orderList", "dutyType", "noticeList"})
    private T data;

    public String getReturnCode() {

        return returnCode;
    }

    public void setReturnCode(final String returnCode) {

        this.returnCode = returnCode;
    }

    public String getReturnMsg() {

        return returnMsg;
    }

    public void setReturnMsg(final String returnMsg) {

        this.returnMsg = returnMsg;
    }

    public T getData() {

        return data;
    }

    public void setData(T data) {

        this.data = data;
    }

    public String getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus;
    }
}
