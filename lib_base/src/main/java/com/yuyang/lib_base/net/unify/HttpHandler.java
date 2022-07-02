package com.yuyang.lib_base.net.unify;


import okhttp3.Call;

public class HttpHandler<T> {
    private Call call;

    public HttpHandler(Call call) {
        this.call = call;
    }

    public void cancel() {
        call.cancel();
    }
}
