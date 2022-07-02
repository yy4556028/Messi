package com.yuyang.lib_base.ui.viewmodel;

public class LiveProcess {

    private String message;

    private boolean show;

    private boolean isCancelable;

    private int type;

    /**
     * @param show 是否显示
     */
    public LiveProcess(boolean show) {
        this(show, null);
    }

    /**
     * @param show 是否显示
     * @param message 显示内容
     */
    public LiveProcess(boolean show, String message) {
        this(show, message, false);
    }

    /**
     * @param show      是否显示
     * @param message      显示内容
     * @param isCancelable 是否可取消
     */
    public LiveProcess(boolean show, String message, boolean isCancelable) {
        this.show = show;
        this.message = message;
        this.isCancelable = isCancelable;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public boolean isCancelable() {
        return isCancelable;
    }

    public void setCancelable(boolean cancelable) {
        isCancelable = cancelable;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
