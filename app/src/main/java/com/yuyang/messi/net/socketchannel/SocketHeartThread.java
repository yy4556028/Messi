package com.yuyang.messi.net.socketchannel;

/**
 * 创建者: yuyang
 * 创建日期: 2015-07-04
 * 创建时间: 23:34
 * SocketHeartThread: 心跳线程
 *
 * @author yuyang
 * @version 1.0
 */
class SocketHeartThread extends Thread {

    boolean isStop = false;

    static SocketHeartThread s_instance;

    public static synchronized SocketHeartThread instance() {
        if (s_instance == null) {
            s_instance = new SocketHeartThread();
        }
        return s_instance;
    }

    public SocketHeartThread() {
        TCPClient.instance();
    }

    public void stopThread() {
        isStop = true;
    }

    /**
     * 连接socket到服务器, 并发送初始化的Socket信息
     *
     * @return
     */


    private boolean reConnect() {
        return TCPClient.instance().reConnect();
    }


    public void run() {

        isStop = false;

        while (!isStop) {
            // 发送一个心跳包看服务器是否正常
            boolean canConnectToServer = TCPClient.instance().canConnectToServer();

            if (canConnectToServer == false) {
                reConnect();
            }
            try {
                Thread.sleep(Const.SOCKET_HEART_SECOND * 1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
