package com.yuyang.messi.ui.base;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.yuyang.lib_base.ui.base.BaseViewModel;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.net.retrofit.data.ResponseData;

public class AppBaseViewModel extends BaseViewModel {

    private MutableLiveData<Boolean> reLogin = new MutableLiveData<>();

    private MutableLiveData<Boolean> mUpdate;

    protected <T> boolean checkResponseData(ResponseData<T> responseData, boolean showErrorMsg) {
        synchronized (AppBaseViewModel.class) {
            if (responseData != null) {
                if ("E".equals(responseData.getReturnCode()) || "F".equals(responseData.getReturnCode())) {
                    if ("N".equals(responseData.getLoginStatus())) {
                        reLogin();
                        return false;
                    }
                    if (showErrorMsg) {
                        String returnMsg = responseData.getReturnMsg();
                        CharSequence errorMsg = TextUtils.isEmpty(returnMsg) ? "额，系统开小差了，请稍后再试！" : returnMsg;
                        ToastUtil.showToast(errorMsg);
                    }
                    return false;
                }
            } else {
                if (showErrorMsg) {
                    ToastUtil.showToast("额，系统开小差了，请稍后再试！");
                }
                return false;
            }
            return true;
        }
    }

    private void reLogin() {
        getReLogin().setValue(true);
    }

    public MutableLiveData<Boolean> getReLogin() {
        return reLogin;
    }

    public MutableLiveData<Boolean> getUpdate() {
        if (mUpdate == null) {
            mUpdate = new MutableLiveData<>();
        }
        return mUpdate;
    }
}
