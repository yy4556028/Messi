package com.yuyang.messi.net.socketchannel;

import android.text.TextUtils;

import com.yuyang.messi.net.NetworkManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;

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

    private boolean isStart = false;

    public SocketInputThread() {
    }

    public void setStart(boolean isStart) {
        this.isStart = isStart;
    }

    @Override
    public void run() {
        while (isStart) {
            // 手机能联网，读socket数据
            if (NetworkManager.isNetworkAvailable()) {

                if (!TCPClient.instance().isConnect()) {
//                    CLog.e(tag, "TCPClient connect server is fail read thread sleep second" + Const.SOCKET_SLEEP_SECOND);

                    try {
                        sleep(Const.SOCKET_SLEEP_SECOND * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                readSocket();

                // 如果连接服务器失败,服务器连接失败，sleep固定的时间，能联网，就不需要sleep

//                CLog.e("socket", "TCPClient.instance().isConnect() " + TCPClient.instance().isConnect());


            }
        }
    }

    public void readSocket() {
        Selector selector = TCPClient.instance().getSelector();
        if (selector == null) {
            return;
        }
        try {
            // 如果没有数据过来，一直柱塞
            while (selector.select() > 0) {
                for (SelectionKey sk : selector.selectedKeys()) {
                    // 如果该SelectionKey对应的Channel中有可读的数据
                    if (sk.isReadable()) {
                        // 使用NIO读取Channel中的数据
                        SocketChannel sc = (SocketChannel) sk.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        try {
                            sc.read(buffer);
                        } catch (IOException e) {
                            e.printStackTrace();
                            // continue;
                        }
                        buffer.flip();
                        String receivedString = "";
                        // 打印收到的数据
                        try {
                            receivedString = Charset.forName("UTF-8")
                                    .newDecoder().decode(buffer).toString();

//                            CLog.e(tag, receivedString);

                            if (!TextUtils.isEmpty(receivedString)) {

                                SocketResponseListener.OnResponseListener listener = SocketResponseListener.getOnResponseListener();
                                if (listener != null) {
                                    listener.response(receivedString);
                                }

                                // 源代码是通过广播通知 activity 的
//                                Intent i = new Intent(Const.BC);
//                                i.putExtra("response", receivedString);
//                                LocalBroadcastManager.getAppContext(YamapApp.getAppContext()).sendBroadcast(i);
                            }

                        } catch (CharacterCodingException e) {
                            e.printStackTrace();
                        }
                        buffer.clear();
                        buffer = null;

                        try {
                            // 为下一次读取作准备
                            sk.interestOps(SelectionKey.OP_READ);
                            // 删除正在处理的SelectionKey
                            selector.selectedKeys().remove(sk);

                        } catch (CancelledKeyException e) {
                            e.printStackTrace();
                        }


                    }
                }
            }
            // selector.close();
            // TCPClient.instance().repareRead();

        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (ClosedSelectorException e2) {
        }
    }

}
