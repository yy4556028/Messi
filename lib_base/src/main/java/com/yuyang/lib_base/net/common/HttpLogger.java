package com.yuyang.lib_base.net.common;

import com.yuyang.lib_base.utils.LogUtil;
import okhttp3.logging.HttpLoggingInterceptor;

public class HttpLogger implements HttpLoggingInterceptor.Logger {

    @Override
    public void log(String message) {
        //信息太长,分段打印
        //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，
        //  把4*1024的MAX字节打印长度改为2001字符数
        int max_str_length = 3001;
        //大于4000时
        while (message.length() > max_str_length) {
            LogUtil.d("HttpLogger", message.substring(0, max_str_length));
            message = message.substring(max_str_length);
        }
        //剩余部分
        LogUtil.d("HttpLogger", message);
    }
}
