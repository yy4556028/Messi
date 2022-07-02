package com.yuyang.messi.net.socket;

import com.yuyang.messi.net.NetworkManager;

import java.io.IOException;

/**
 * 创建者: yuyang
 * 创建日期: 2015-07-04
 * 创建时间: 23:34
 * SocketInputThread: 客户端读消息线程
 *
 * @author yuyang
 * @version 1.0
 */

public class SocketInputThread extends Thread {

    private boolean isStart = true;

    private String receivedString;

    public SocketInputThread() {

    }

    public void setStart(boolean isStart) {
        this.isStart = isStart;
        interrupt();
    }

    @Override
    public void run() {

        while (isStart) {

            // 手机能联网，读socket数据
            if (NetworkManager.isNetworkAvailable()) {

                if (!TCPClient.instance().isConnect() || TCPClient.instance().getSocket().isInputShutdown()) {
//                    CLog.e(tag, "TCPClient connect server is fail read thread sleep second" + Const.SOCKET_SLEEP_SECOND);

                    try {
                        sleep(Const.SOCKET_SLEEP_SECOND * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (TCPClient.instance().reader != null) {
                    readSocket();
                }

                // 如果连接服务器失败,服务器连接失败，sleep固定的时间，能联网，就不需要sleep

//                CLog.e("socket", "TCPClient.instance().isConnect() " + TCPClient.instance().isConnect());


            }
        }
    }

    public void readSocket() {

        try {

            receivedString = TCPClient.instance().reader.readLine();

            if (receivedString != null && receivedString.length() > 0) {

                SocketResponseListener.OnResponseListener listener = SocketResponseListener.getOnResponseListener();
                if (listener != null) {
                    listener.response(receivedString);
                }

//                Intent i = new Intent(Const.BC);
//                i.putExtra("response", receivedString);
//                LocalBroadcastManager.getAppContext(YamapApp.getAppContext()).sendBroadcast(i);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
