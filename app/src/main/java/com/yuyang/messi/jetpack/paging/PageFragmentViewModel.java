package com.yuyang.messi.jetpack.paging;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.yuyang.messi.kotlinui.beauty.BeautyBean;

public class PageFragmentViewModel extends ViewModel {

    public static final int LOAD_SIZE = 20;
    private LiveData<PagedList<BeautyBean>> pagedList;

    public LiveData<PagedList<BeautyBean>> getPagedList() {
        if (pagedList == null) {
            PagedList.Config pagingConfig = new PagedList.Config.Builder()
                    .setPageSize(LOAD_SIZE) // 分页加载的数量
                    .setInitialLoadSizeHint(LOAD_SIZE) // 预加载的数量, 与分页加载的数量成倍数关系
//                    .setEnablePlaceholders(false) // 当item为null是否使用PlaceHolder展示
//                    .setPrefetchDistance(5)
                    .build();

            pagedList = new LivePagedListBuilder<>(new DataSourceFactory(), pagingConfig)
                    .setInitialLoadKey(0)
//                    .setFetchExecutor(myExecutor)// 指定线程执行异步操作
                    .build();
//            pagedList = new RxPagedListBuilder(new DataSourceFactory(), pagingConfig);
        }
        return pagedList;
    }
}
