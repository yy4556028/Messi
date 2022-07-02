package com.yuyang.messi.net.socket;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 创建者: yuyang
 * 创建日期: 2015-07-04
 * 创建时间: 23:34
 * SocketOutputThread: 客户端写消息线程
 *
 * @author yuyang
 * @version 1.0
 */

public class SocketOutputThread extends Thread {

    private boolean isStart = false;
    private LinkedBlockingQueue<MsgEntity> sendMsgQueue;

    public SocketOutputThread() {

        sendMsgQueue = new LinkedBlockingQueue<>();
    }

    public void setStart(boolean isStart) {
        this.isStart = isStart;

        synchronized (this) {
            notify();
        }
    }

    // 使用socket发送消息
    private boolean sendMsg(String message) {

        if (!isStart) {
            return false;
        }

        if (message == null) {
            return false;
        }

        try {
            TCPClient.instance().sendMsg(message.getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    // 使用socket发送消息
    public void addMsgToSendList(MsgEntity msg) {
        sendMsgQueue.add(msg);
    }

    @Override
    public void run() {
        while (isStart) {

            if (!sendMsgQueue.isEmpty()) {

                MsgEntity msg = sendMsgQueue.poll();

                Handler handler = msg.getHandler();

                try {
                    sendMsg(msg.getMessage());

                    // 成功消息，通过hander回传
                    if (handler != null) {
                        Message message = new Message();
                        message.obj = msg.getMessage();
                        message.what = 1;
                        handler.sendMessage(message);
                        //	handler.sendEmptyMessage(1);
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                    // 错误消息，通过hander回传
                    if (handler != null) {
                        Message message = new Message();
                        message.obj = msg.getMessage();
                        message.what = 0;
                        ;
                        handler.sendMessage(message);

                    }
                }
            }
        }
    }
}
