package com.yuyang.lib_base.ui.base;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yuyang.lib_base.ui.viewmodel.LiveProcess;

public abstract class BaseViewModel extends ViewModel {

    private MutableLiveData<LiveProcess> mLoading;//加载状态：

    private MutableLiveData<Boolean> mRefresh;//刷新状态
    private MutableLiveData<Boolean> mHasMoreData;//是否还有更多数据

    public MutableLiveData<LiveProcess> getLoading() {

        if (mLoading == null) {
            mLoading = new MutableLiveData<>();
        }
        return mLoading;
    }


    public MutableLiveData<Boolean> getRefresh() {

        if (mRefresh == null) {
            mRefresh = new MutableLiveData<>();
        }
        return mRefresh;
    }

    public MutableLiveData<Boolean> getHasMoreData() {
        if (mHasMoreData == null) {
            mHasMoreData = new MutableLiveData<>();
        }
        return mHasMoreData;
    }

    /**
     * 更改刷新状态（主线程调用）
     */
    public void finishRefresh() {
        getRefresh().setValue(false);
    }

    /**
     * 是否在刷新
     */
    public Boolean isRefresh() {
        return getRefresh().getValue() != null && getRefresh().getValue();
    }

    public void finishLoadMore(boolean hasMoreData) {
        getHasMoreData().setValue(hasMoreData);
    }

    public void showLoading() {
        showLoading(null);
    }

    public void showLoading(String msg) {
        showLoading(msg, false);
    }

    public void showLoading(String msg, boolean cancelable) {
        getLoading().postValue(new LiveProcess(true, msg, cancelable));
    }

    public void hideLoading() {
        getLoading().postValue(new LiveProcess(false));
    }

    public boolean isLoading() {
        final LiveProcess value = getLoading().getValue();
        return value != null && value.isShow();
    }

}
