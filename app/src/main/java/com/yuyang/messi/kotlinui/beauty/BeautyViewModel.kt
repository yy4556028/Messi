package com.yuyang.messi.kotlinui.beauty

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yuyang.lib_base.config.Config
import com.yuyang.lib_base.net.common.RxUtils
import com.yuyang.lib_base.utils.ToastUtil
import com.yuyang.messi.net.okhttp.OkHttpUtil
import com.yuyang.messi.net.okhttp.callback.StringCallback
import com.yuyang.messi.net.retrofit.CommonRequest
import com.yuyang.messi.net.retrofit.data.ResponseData
import com.yuyang.messi.ui.base.AppBaseViewModel
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.launch
import okhttp3.Call

class BeautyViewModel : AppBaseViewModel() {
    init {
        viewModelScope.launch {
            // 当ViewModel被清除，这个范围内启动的协程就会自动取消
        }
    }

    val beanList = MutableLiveData<MutableList<BeautyBean>?>()
    val bean1List = MutableLiveData<MutableList<Beauty1Bean>?>()
    private var keyword = "美女"
    fun setKeyword(keyword: String) {
        this.keyword = keyword
    }

    fun loadImageBaidu(refresh: Boolean) {
        val currentCount: Int = if (refresh) {
            0
        } else {
            if (beanList.value == null) 0 else beanList.value!!.size
        }
        CommonRequest.getInstance().baiduImage(keyword, currentCount, Config.LIMIT)
            .compose(RxUtils.io2main())
            .doFinally { if (refresh) finishRefresh() }
            .subscribe(object : Observer<ResponseData<List<BeautyBean>?>> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(responseData: ResponseData<List<BeautyBean>?>) {
                    if (checkResponseData(responseData, true)) {
                        if (refresh) {
                            beanList.value = responseData.data?.toMutableList()
                        } else {
                            val beautyBeanList = beanList.value?.toMutableList()
                            if (beautyBeanList != null) {
                                responseData.data?.toMutableList()
                                    ?.let { beautyBeanList.addAll(it) }
                                beanList.value = beautyBeanList
                            }
                        }
                        finishLoadMore(responseData.data!!.size >= Config.LIMIT)
                    } else {
                        if (refresh) {
                            beanList.value = null
                            finishLoadMore(false)
                        } else {
                            finishLoadMore(true)
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    if (!refresh) finishLoadMore(true)
                    ToastUtil.showToast(e.message)
                }

                override fun onComplete() {
                }
            })
    }

    fun loadImageOther(refresh: Boolean) {
        OkHttpUtil
            .get()
            .url(" https://www.mxnzp.com/api/image/girl/list/random")
            .addParams("app_id", "xbhtzmhwyqssdqkk")
            .addParams("app_secret", "UTRPRzhuRkxtSEVpdVJLVHpLVVNQZz09")
            .build()
            .execute(object : StringCallback() {
                override fun onError(call: Call, e: Exception, id: Int) {
                    if (refresh) finishRefresh()
                    if (!refresh) finishLoadMore(true)
                    ToastUtil.showToast(e.message)
                }

                override fun onResponse(response: String, id: Int) {
                    if (refresh) finishRefresh()
                    try {
                        val responseData: ResponseData<List<Beauty1Bean>> = Gson().fromJson(
                            response,
                            object : TypeToken<ResponseData<List<Beauty1Bean>>>() {}.type
                        )
                        if (checkResponseData(responseData, true)) {
                            if (refresh) {
                                bean1List.value = responseData.data?.toMutableList()
                            } else {
                                val beautyBeanList = bean1List.value?.toMutableList()
                                if (beautyBeanList != null) {
                                    responseData.data?.toMutableList()
                                        ?.let { beautyBeanList.addAll(it) }
                                    bean1List.value = beautyBeanList
                                }
                            }
                            finishLoadMore(true)
                        } else {
                            if (refresh) {
                                bean1List.value = null
                                finishLoadMore(true)
                            } else {
                                finishLoadMore(true)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }
}