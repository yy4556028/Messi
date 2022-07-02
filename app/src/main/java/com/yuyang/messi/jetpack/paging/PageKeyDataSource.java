package com.yuyang.messi.jetpack.paging;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.yuyang.lib_base.net.common.RxUtils;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.kotlinui.beauty.BeautyBean;
import com.yuyang.messi.net.retrofit.CommonRequest;
import com.yuyang.messi.net.retrofit.data.ResponseData;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class PageKeyDataSource extends PageKeyedDataSource<Integer, BeautyBean> {

    // 初始化第一页数据
    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, BeautyBean> callback) {

        CommonRequest.getInstance().baiduImage("美女", 0, params.requestedLoadSize)
                .compose(RxUtils.io2main())
                .subscribe(new Observer<ResponseData<List<BeautyBean>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseData<List<BeautyBean>> responseData) {
                        if ("E".equals(responseData.getReturnCode()) || "F".equals(responseData.getReturnCode())) {
                            String returnMsg = responseData.getReturnMsg();
                            CharSequence errorMsg = TextUtils.isEmpty(returnMsg) ? "额，系统开小差了，请稍后再试！" : returnMsg;
                            ToastUtil.showToast(errorMsg);
                        } else {
                            callback.onResult(responseData.getData(), null, 1);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 加载前一页数据
    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, BeautyBean> callback) {
        CommonRequest.getInstance().baiduImage("美女", params.key * params.requestedLoadSize, params.requestedLoadSize)
                .compose(RxUtils.io2main())
                .subscribe(new Observer<ResponseData<List<BeautyBean>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseData<List<BeautyBean>> responseData) {
                        if ("E".equals(responseData.getReturnCode()) || "F".equals(responseData.getReturnCode())) {
                            String returnMsg = responseData.getReturnMsg();
                            CharSequence errorMsg = TextUtils.isEmpty(returnMsg) ? "额，系统开小差了，请稍后再试！" : returnMsg;
                            ToastUtil.showToast(errorMsg);
                            callback.onResult(responseData.getData(), params.key);
                        } else {
                            callback.onResult(responseData.getData(), params.key - 1);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 加载下一页数据
    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, BeautyBean> callback) {
        CommonRequest.getInstance().baiduImage("美女", params.key * params.requestedLoadSize, params.requestedLoadSize)
                .compose(RxUtils.io2main())
                .subscribe(new Observer<ResponseData<List<BeautyBean>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseData<List<BeautyBean>> responseData) {
                        if ("E".equals(responseData.getReturnCode()) || "F".equals(responseData.getReturnCode())) {
                            String returnMsg = responseData.getReturnMsg();
                            CharSequence errorMsg = TextUtils.isEmpty(returnMsg) ? "额，系统开小差了，请稍后再试！" : returnMsg;
                            ToastUtil.showToast(errorMsg);
                        } else {
                            callback.onResult(responseData.getData(), params.key + 1);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}

