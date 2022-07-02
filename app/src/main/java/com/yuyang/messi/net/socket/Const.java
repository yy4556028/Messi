package com.yuyang.messi.net.socket;

public class Const {

    public static String SP_KEY_SOCKET_SERVER = "socket_server";

    public static String SP_KEY_SOCKET_PORT = "socket_port";

    public static String SOCKET_SERVER = "192.168.1.57";

    public static int SOCKET_PORT = 60000;

    // 默认timeout 时间 60s
    public final static int SOCKET_TIMOUT = 5 * 1000;

    public final static int SOCKET_READ_TIMOUT = 15 * 1000;

    //如果没有连接无服务器。读线程的sleep时间
    public final static int SOCKET_SLEEP_SECOND = 3;

    //心跳包发送间隔时间
    public final static int SOCKET_HEART_SECOND = 6;

    public final static String BC = "BC";


}
