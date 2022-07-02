package com.yuyang.messi.ui.suning;

public class DouyaLoginBean {

    /**
     * 0：成功
     * -1：失败，显示登录页，即未提交登录凭据
     * 1：失败，原因再根据下面具体的errorCode
     */
    private int res_code;
    private String res_message;//SUCCESS    errorCode
    private String errorMessage;
    private String service;
    private boolean tgtTimeoutOrKickoff;
    private String st;

    public int getRes_code() {
        return res_code;
    }

    public void setRes_code(int res_code) {
        this.res_code = res_code;
    }

    public String getRes_message() {
        return res_message;
    }

    public void setRes_message(String res_message) {
        this.res_message = res_message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public boolean isTgtTimeoutOrKickoff() {
        return tgtTimeoutOrKickoff;
    }

    public void setTgtTimeoutOrKickoff(boolean tgtTimeoutOrKickoff) {
        this.tgtTimeoutOrKickoff = tgtTimeoutOrKickoff;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }
}
