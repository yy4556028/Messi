package com.yuyang.messi.net.socket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.yuyang.lib_base.utils.ToastUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

/**
 * TCP 客户端
 */
public class TCPClient {

    private static final int SOCKET_CONNECT_SUCCESS = 0;

    private static final int SOCKET_CONNECT_FAIL = 1;

    private static final int SOCKET_CLOSE = 2;

    // 套接字
    private Socket socket;

    // 接受服务器消息的 reader
    public BufferedReader reader;

    // 向服务器发消息的输出流
    private OutputStream outputStream;

    private static TCPClient s_Tcp = null;

    public boolean isInitialized = false;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case SOCKET_CONNECT_SUCCESS:
                    try {
                        outputStream = socket.getOutputStream();

                        InputStream ios = socket.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(ios, "utf-8"));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ToastUtil.showToast("Socket连接成功");
                    break;

                case SOCKET_CONNECT_FAIL:
                    reader = null;
                    ToastUtil.showToast("Socket连接失败");
                    break;

                case SOCKET_CLOSE:
                    ToastUtil.showToast("Socket已关闭");
                    break;
            }
        }
    };

    public static synchronized TCPClient instance() {
        if (s_Tcp == null) {

            s_Tcp = new TCPClient();
        }
        return s_Tcp;
    }

    public Socket getSocket() {
        return socket;
    }

    /**
     * 构造函数
     *
     * @throws IOException
     */
    public TCPClient() {

        try {
            initialize();
            this.isInitialized = true;
        } catch (IOException e) {
            this.isInitialized = false;
            e.printStackTrace();
        } catch (Exception e) {
            this.isInitialized = false;
            e.printStackTrace();
        }

    }

    /**
     * 初始化
     *
     * @throws IOException
     */
    private void initialize() throws IOException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                Message message = handler.obtainMessage();

                try {

                    InetAddress serverAddr = InetAddress.getByName(Const.SOCKET_SERVER);

                    socket = new Socket();

                    SocketAddress socketAddress = new InetSocketAddress(serverAddr, Const.SOCKET_PORT);

                    socket.connect(socketAddress, Const.SOCKET_TIMOUT);
                    socket.setTcpNoDelay(true);
                    message.what = SOCKET_CONNECT_SUCCESS;
                    handler.sendMessage(message);

                } catch (SocketTimeoutException ste) {
                    message.what = SOCKET_CONNECT_FAIL;
                    handler.sendMessage(message);
                    ste.printStackTrace();
                } catch (Exception e) {
                    message.what = SOCKET_CLOSE;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
            }
        };

        new Thread(runnable).start();

    }

    /**
     * 发送字符串到服务器
     *
     * @param bytes
     * @throws IOException
     */
    public void sendMsg(byte[] bytes) throws IOException {

        outputStream.write(bytes);
        outputStream.flush();
    }

    /**
     * Socket连接是否是正常的
     *
     * @return
     */
    public boolean isConnect() {
        boolean isConnect = false;
        if (socket != null && this.isInitialized) {
            isConnect = socket.isConnected() && !socket.isClosed();
        }
        return isConnect;
    }

    /**
     * 关闭socket 重新连接
     *
     * @return
     */
    public boolean reConnect() {
        closeTCPSocket();

        try {
            initialize();
            isInitialized = true;
        } catch (IOException e) {
            isInitialized = false;
            e.printStackTrace();
        } catch (Exception e) {
            isInitialized = false;
            e.printStackTrace();
        }

        return isInitialized;
    }

    /**
     * 服务器是否关闭，通过发送一个socket信息
     *
     * @return
     */
    public boolean canConnectToServer() {
        try {
            if (isConnect()) {
                socket.sendUrgentData(0xff);
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 关闭socket
     */
    public void closeTCPSocket() {
        try {
            if (socket != null) {
                socket.close();
            }

            if (outputStream != null) {
                outputStream.close();
            }

            if (reader != null) {
                reader.close();
            }

        } catch (IOException e) {

        }

        socket = null;
        isInitialized = false;
    }

}
