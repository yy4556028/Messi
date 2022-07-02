package com.yuyang.lib_base.net.unify;

public class ResponseInfo<T> {
    public T result;
    public long max;

    public ResponseInfo(T result) {
        this.result = result;
    }
}
