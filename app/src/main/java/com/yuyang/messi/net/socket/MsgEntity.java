package com.yuyang.messi.net.socket;

import android.os.Handler;

/**
 * 创建者: yuyang
 * 创建日期: 2015-07-04
 * 创建时间: 23:34
 * MsgEntity: 存储发送socket的类，包含要发送的BufTest，以及对应的返回结果的Handler
 *
 * @author yuyang
 * @version 1.0
 */

public class MsgEntity {
    //要发送的消息
    private String message;
    //错误处理的handler
    private Handler mHandler;

    public MsgEntity(String message, Handler handler) {
        this.message = message;
        mHandler = handler;
    }

    public String getMessage() {
        return this.message;
    }

    public Handler getHandler() {
        return mHandler;
    }

}
