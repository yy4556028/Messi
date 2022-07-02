package com.yuyang.messi.net.socketchannel;

/**
 * 创建者: yuyang
 * 创建日期: 2015-07-08
 * 创建时间: 9:34
 * SocketThreadManager: socket 注册监听返回数据listener
 *
 * @author yuyang
 * @version 1.0
 */
public class SocketResponseListener {

    private static OnResponseListener listener;

    /**
     * 接收 socket server端发送的消息接口
     */
    public static interface OnResponseListener {
        public void response(String response);
    }

    // 注册监听定位
    public static void setOnResponseListener(OnResponseListener onResponseListener) {
        listener = onResponseListener;
    }

    public static void removeResponseListener(OnResponseListener onResponseListener) {
        if (listener == onResponseListener)
            listener = null;
    }

    public static OnResponseListener getOnResponseListener() {
        return listener;
    }
}
