package com.yuyang.lib_base.config;

public class Config {

    public static final String FILEPROVIDER_AUTHORITY = "com.yuyang.messi.fileprovider";   // 与 manifest 中的 fileprovider 的 authority 保持一致
    public static final long CODE_WAIT_TIME = 60 * 1000;//验证码等待时间
    public static boolean PRINT_LOG = true; //是否打印log日志
    public static final int LIMIT = 20; //分页加载每页的数量
    public static final int REFRESH_DELAY = 200;//列表页面刚进入时的延迟加载时间
}
