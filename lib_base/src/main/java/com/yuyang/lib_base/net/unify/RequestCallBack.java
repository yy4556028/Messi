package com.yuyang.lib_base.net.unify;

public abstract class RequestCallBack<T> {

    public abstract void onSuccess(ResponseInfo<T> responseInfo);

    public abstract void onFailure(HttpException exception, String string);

    public void onStart() {
    }

    public void onCancelled() {
    }

    public void onLoading(long total, long current, boolean isUploading) {
    }

}
