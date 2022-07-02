package com.yuyang.messi.ui.category.scroll;

import android.os.Bundle;

import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;


public class ScrollViewActivity extends AppBaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_scrollview;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
//        initEvent();
    }

    private void initView() {
//        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
//        headerLayout.showLeftBackButton();
//        headerLayout.showTitle("ScrollView");

    }

}
