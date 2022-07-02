package com.yuyang.messi.net.retrofit;


import com.yuyang.lib_base.net.common.RetrofitUtil;
import com.yuyang.messi.BuildConfig;
import com.yuyang.messi.bean.douban.DoubanMovieBean;
import com.yuyang.messi.bean.douban.DoubanResponse;
import com.yuyang.messi.kotlinui.beauty.BeautyBean;
import com.yuyang.messi.net.retrofit.data.ResponseData;
import com.yuyang.messi.ui.finance.FinanceHomeActivity;
import com.yuyang.messi.ui.finance.bean.FinanceBaseBean;
import com.yuyang.messi.ui.finance.bean.FinanceData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

public class CommonRequest {

    private volatile static CommonRequest INSTANCE = null;

    private static CommonService mCommonService;

    public static CommonRequest getInstance() {

        if (INSTANCE == null) {
            synchronized (CommonRequest.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CommonRequest();
                    Map<String, String> params = new HashMap<>();
                    params.put("device", "Android" + android.os.Build.VERSION.RELEASE);
                    params.put("version", BuildConfig.VERSION_NAME);
                    mCommonService = RetrofitUtil.getInstance(BuildConfig.HTTPS_HOST, params).create(CommonService.class);
                }
            }
        }
        return INSTANCE;
    }

    public Observable<ResponseData<List<BeautyBean>>> baiduImage(String word, int pageNum, int rowsNum) {
        return mCommonService.baiduImage("resultjson_com", "rj", "utf-8", "utf-8", pageNum, rowsNum, word);
    }

    public Observable<DoubanResponse> douban(String type, String queryWord, int start, int count) {
        return mCommonService.douban(type, queryWord, start, count, "0df993c66c0c636e29ecbb5344252a4a");
    }

    public Observable<DoubanResponse> doubanMovieHot(String city, int start, int count) {
        return mCommonService.doubanMovieHot(city, start, count, "0b2bdeda43b5688921839c8ecb20399b");
    }

    public Observable<DoubanMovieBean> doubanMovie(String movieId) {
        return mCommonService.doubanMovie(movieId, "0b2bdeda43b5688921839c8ecb20399b");
    }

    public Observable<FinanceBaseBean<FinanceData>> loadStockInfo(String gid, String type) {
        return mCommonService.loadStockInfo(gid, type, FinanceHomeActivity.APP_KEY);
    }
}
