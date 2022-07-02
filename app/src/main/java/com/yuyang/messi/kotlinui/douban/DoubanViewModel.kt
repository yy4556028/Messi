package com.yuyang.messi.kotlinui.douban

import androidx.lifecycle.MutableLiveData
import com.yuyang.lib_base.config.Config
import com.yuyang.lib_base.net.common.RxUtils
import com.yuyang.lib_base.utils.ToastUtil
import com.yuyang.messi.bean.douban.DoubanBookBean
import com.yuyang.messi.bean.douban.DoubanMovieBean
import com.yuyang.messi.bean.douban.DoubanMusicBean
import com.yuyang.messi.bean.douban.DoubanResponse
import com.yuyang.messi.net.retrofit.CommonRequest
import com.yuyang.messi.ui.base.AppBaseViewModel
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class DoubanViewModel : AppBaseViewModel() {

    private val mHashMap = HashMap<String, MyStatusModel>()
    val bookList = MutableLiveData<MutableList<DoubanBookBean>>()
    val movieList = MutableLiveData<MutableList<DoubanMovieBean>>()
    val musicList = MutableLiveData<MutableList<DoubanMusicBean>>()

    fun loadBook(refresh: Boolean) {
        val currentCount: Int = if (refresh) {
            0
        } else {
            if (bookList.value == null) 0 else bookList.value!!.size
        }
        CommonRequest.getInstance().douban(
            "book",
            getStatusModel(DoubanBookFragment::class.java.simpleName)?.keyword,
            currentCount,
            Config.LIMIT
        )
            .compose(RxUtils.io2main())
            .doFinally { getStatusModel(DoubanBookFragment::class.java.simpleName)?.finishRefresh() }
            .subscribe(object : Observer<DoubanResponse> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(responseData: DoubanResponse) {
                    try {
                        if (refresh) {
                            bookList.setValue(responseData.books)
                        } else {
                            val beautyBeanList = bookList.value
                            if (beautyBeanList != null) {
                                beautyBeanList.addAll(responseData.books)
                                bookList.value = beautyBeanList!!
                            }
                        }
                    } catch (e: Exception) {
                        onError(e)
                    }
                    getStatusModel(DoubanBookFragment::class.java.simpleName)
                        ?.finishLoadMore(responseData.start + responseData.count < responseData.total)
                }

                override fun onError(e: Throwable) {
                    ToastUtil.showToast(e.message)
                }

                override fun onComplete() {}
            })
    }

    fun loadMovieHot(refresh: Boolean) {
        val currentCount: Int = if (refresh) {
            0
        } else {
            if (movieList.value == null) 0 else movieList.value!!.size
        }
        CommonRequest.getInstance().doubanMovieHot(
            getStatusModel(DoubanBookFragment::class.java.simpleName)?.keyword,
            currentCount,
            Config.LIMIT
        )
            .compose(RxUtils.io2main())
            .doFinally { getStatusModel(DoubanMovieFragment::class.java.simpleName)?.finishRefresh() }
            .subscribe(object : Observer<DoubanResponse> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(responseData: DoubanResponse) {
                    if (refresh) {
                        movieList.setValue(responseData.subjects)
                    } else {
                        val beautyBeanList = movieList.value
                        if (beautyBeanList != null) {
                            beautyBeanList.addAll(responseData.subjects)
                            movieList.value = beautyBeanList!!
                        }
                    }
                    getStatusModel(DoubanMovieFragment::class.java.simpleName)
                        ?.finishLoadMore(responseData.start + responseData.count < responseData.total)
                }

                override fun onError(e: Throwable) {
                    ToastUtil.showToast(e.message)
                }

                override fun onComplete() {}
            })
    }

    fun loadMusic(refresh: Boolean) {
        val currentCount: Int = if (refresh) {
            0
        } else {
            if (musicList.value == null) 0 else musicList.value!!.size
        }
        CommonRequest.getInstance().douban(
            "music",
            getStatusModel(DoubanMusicFragment::class.java.simpleName)!!.keyword,
            currentCount,
            Config.LIMIT
        )
            .compose(RxUtils.io2main())
            .doFinally { getStatusModel(DoubanMusicFragment::class.java.simpleName)!!.finishRefresh() }
            .subscribe(object : Observer<DoubanResponse> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(responseData: DoubanResponse) {
                    try {
                        if (refresh) {
                            musicList.setValue(responseData.musics)
                        } else {
                            val musicBeanList = musicList.value
                            if (musicBeanList != null) {
                                musicBeanList.addAll(responseData.musics)
                                musicList.value = musicBeanList!!
                            }
                        }
                        getStatusModel(DoubanMusicFragment::class.java.simpleName)
                            ?.finishLoadMore(responseData.start + responseData.count < responseData.total)
                    } catch (e: Exception) {
                        onError(e)
                    }
                }

                override fun onError(e: Throwable) {
                    if (!refresh) {
                        getStatusModel(DoubanMusicFragment::class.java.simpleName)!!.finishLoadMore(
                            true
                        )
                    }
                    ToastUtil.showToast(e.message)
                }

                override fun onComplete() {}
            })
    }

    fun initMyStatusModel(title: String) {
        mHashMap[title] = MyStatusModel()
    }

    fun getStatusModel(title: String?): MyStatusModel? {
        return mHashMap[title]
    }

    class MyStatusModel {
        val refresh = MutableLiveData<Boolean>() //刷新状态
        val hasMoreData = MutableLiveData<Boolean>() //是否还有更多数据
        var keyword: String? = null

        /**
         * 更改刷新状态（主线程调用）
         */
        fun finishRefresh() {
            refresh.value = false
        }

        fun finishLoadMore(hasMore: Boolean) {
            hasMoreData.value = hasMore
        }
    }
}