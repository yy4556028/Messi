package com.yuyang.messi.ui.category;

import android.content.res.Resources;
import android.os.Bundle;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.AdaptScreenUtils;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

public class AdaptScreenActivity extends AppBaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_adapt_screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("屏幕适配");
    }

    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        AdaptScreenUtils.adaptHeight(resources, 100);
        AdaptScreenUtils.adaptWidth(resources, 1080);
//        AdaptScreenUtils.closeAdapt(resources);
//        https://zhuanlan.zhihu.com/p/363727092
        return resources;
    }

}

