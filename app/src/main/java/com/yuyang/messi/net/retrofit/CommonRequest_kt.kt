package com.yuyang.messi.net.retrofit

import android.os.Build
import com.yuyang.lib_base.net.common.RetrofitUtil
import com.yuyang.messi.BuildConfig
import com.yuyang.messi.bean.douban.DoubanMovieBean
import com.yuyang.messi.bean.douban.DoubanResponse
import com.yuyang.messi.kotlinui.beauty.BeautyBean
import com.yuyang.messi.net.retrofit.data.ResponseData
import com.yuyang.messi.ui.finance.FinanceHomeActivity
import com.yuyang.messi.ui.finance.bean.FinanceBaseBean
import com.yuyang.messi.ui.finance.bean.FinanceData
import io.reactivex.Observable
import java.util.*

class CommonRequest_kt {

    suspend fun baiduImage(
        word: String?,
        pageNum: Int,
        rowsNum: Int
    ): Observable<ResponseData<List<BeautyBean>>> {
        return mCommonService!!.baiduImage(
            "resultjson_com",
            "rj",
            "utf-8",
            "utf-8",
            pageNum,
            rowsNum,
            word
        )
    }

    suspend fun douban(
        type: String?,
        queryWord: String?,
        start: Int,
        count: Int
    ): Observable<DoubanResponse> {
        return mCommonService!!.douban(
            type,
            queryWord,
            start,
            count,
            "0df993c66c0c636e29ecbb5344252a4a"
        )
    }

    suspend fun doubanMovieHot(city: String?, start: Int, count: Int): Observable<DoubanResponse> {
        return mCommonService!!.doubanMovieHot(
            city,
            start,
            count,
            "0b2bdeda43b5688921839c8ecb20399b"
        )
    }

    suspend fun doubanMovie(movieId: String?): Observable<DoubanMovieBean> {
        return mCommonService!!.doubanMovie(movieId, "0b2bdeda43b5688921839c8ecb20399b")
    }

    suspend fun loadStockInfo(gid: String?, type: String?): Observable<FinanceBaseBean<FinanceData>> {
        return mCommonService!!.loadStockInfo(gid, type, FinanceHomeActivity.APP_KEY)
    }

    companion object {
        @Volatile
        private var INSTANCE: CommonRequest_kt? = null
        private var mCommonService: CommonService? = null
        val instance: CommonRequest_kt?
            get() {
                if (INSTANCE == null) {
                    synchronized(CommonRequest_kt::class.java) {
                        if (INSTANCE == null) {
                            INSTANCE = CommonRequest_kt()
                            val params: MutableMap<String, String> = HashMap()
                            params["device"] = "Android" + Build.VERSION.RELEASE
                            params["version"] = BuildConfig.VERSION_NAME
                            mCommonService =
                                RetrofitUtil.getInstance(BuildConfig.HTTPS_HOST, params).create(
                                    CommonService::class.java
                                )
                        }
                    }
                }
                return INSTANCE
            }
    }
}