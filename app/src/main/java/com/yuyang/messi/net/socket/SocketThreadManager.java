package com.yuyang.messi.net.socket;

import android.os.Handler;

/**
 * 创建者: yuyang
 * 创建日期: 2015-07-04
 * 创建时间: 23:34
 * SocketThreadManager: socket 线程管理类
 *
 * @author yuyang
 * @version 1.0
 */

public class SocketThreadManager {

    private static SocketThreadManager s_SocketManager = null;

    private SocketInputThread mInputThread = null;

    private SocketOutputThread mOutThread = null;

    private SocketHeartThread mHeartThread = null;


    // 获取单例
    public static SocketThreadManager sharedInstance() {
        if (s_SocketManager == null) {
            s_SocketManager = new SocketThreadManager();
        }
        return s_SocketManager;
    }

    // 单例，不允许在外部构建对象
    private SocketThreadManager() {
    }

    // 连接server
    public void connect() {

        if (mHeartThread != null || mInputThread != null || mOutThread != null)
            stopThreads();

        startThreads();
    }

    /**
     * 启动线程
     */
    private void startThreads() {

        mHeartThread = new SocketHeartThread();
        mHeartThread.start();

        mInputThread = new SocketInputThread();
        mInputThread.start();
        mInputThread.setStart(true);

        mOutThread = new SocketOutputThread();
        mOutThread.start();
        mOutThread.setStart(true);
    }

    public static void releaseInstance() {
        if (s_SocketManager != null) {
            s_SocketManager.stopThreads();
            s_SocketManager = null;
        }
        TCPClient.instance().closeTCPSocket();
    }

    /**
     * stop线程
     */
    private void stopThreads() {

        if (mHeartThread != null) {
            mHeartThread.stopThread();
            mHeartThread = null;
        }

        if (mInputThread != null) {
            mInputThread.setStart(false);
            mInputThread = null;
        }

        if (mOutThread != null) {
            mOutThread.setStart(false);
            mOutThread = null;
        }
    }

    public void sendMsg(String message, Handler handler) {
        if (mOutThread == null)
            return;

        MsgEntity entity = new MsgEntity(message, handler);
        mOutThread.addMsgToSendList(entity);
    }

}
