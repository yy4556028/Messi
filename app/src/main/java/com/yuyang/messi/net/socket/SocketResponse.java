package com.yuyang.messi.net.socket;

/**
 * 创建者: yuyang
 * 创建日期: 2015-07-04
 * 创建时间: 23:34
 * SocketResponse: 返回数据请求的结果值
 *
 * @author yuyang
 * @version 1.0
 */
public abstract class SocketResponse<T> {
    /**
     * 返回数据请求的结果值
     *
     * @param response
     */
    public abstract void onResponse(T response);
}
