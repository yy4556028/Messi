package com.yuyang.messi.net.okhttp.builder;


import com.yuyang.messi.net.okhttp.OkHttpUtil;
import com.yuyang.messi.net.okhttp.request.OtherRequest;
import com.yuyang.messi.net.okhttp.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtil.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
