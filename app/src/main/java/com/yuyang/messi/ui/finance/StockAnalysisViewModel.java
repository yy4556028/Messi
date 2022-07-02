package com.yuyang.messi.ui.finance;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.yuyang.lib_base.net.common.RxUtils;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.net.retrofit.CommonRequest;
import com.yuyang.messi.ui.base.AppBaseViewModel;
import com.yuyang.messi.ui.finance.bean.FinanceBaseBean;
import com.yuyang.messi.ui.finance.bean.FinanceData;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class StockAnalysisViewModel extends AppBaseViewModel {

    private MutableLiveData<FinanceData> financeData = new MutableLiveData<>();

    private String gid;

    public void setGid(String gid) {
        this.gid = gid;
    }

    public void loadStockInfo() {

        CommonRequest.getInstance().loadStockInfo(gid, null)
                .compose(RxUtils.io2main())
                .subscribe(new Observer<FinanceBaseBean<FinanceData>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(FinanceBaseBean<FinanceData> responseData) {
                        if (TextUtils.equals(responseData.getResultcode(), "200")) {
                            getFinanceData().setValue(responseData.getResult());
                        } else {
                            ToastUtil.showToast(responseData.getReason());
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

    public MutableLiveData<FinanceData> getFinanceData() {
        return financeData;
    }
}
