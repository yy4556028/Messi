package com.yuyang.messi.jetpack.paging;

import androidx.paging.DataSource;

import com.yuyang.messi.kotlinui.beauty.BeautyBean;

public class DataSourceFactory extends DataSource.Factory<Integer, BeautyBean> {

    @Override
    public DataSource<Integer, BeautyBean> create() {
        return new PageKeyDataSource();
    }
}
