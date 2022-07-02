package com.yuyang.messi.ui.category;

import android.os.Bundle;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

public class SpanDemoActivity extends AppBaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_span_demo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvents();
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("Span Demo");


    }

    private void initEvents() {


    }


}

