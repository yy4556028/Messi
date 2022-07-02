package com.yuyang.messi.ui.category;

import android.os.Bundle;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.view.PathView;

public class PathActivity extends AppBaseActivity {

    private PathView pathView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_path;
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
        headerLayout.showTitle("Path");

        pathView = findViewById(R.id.activity_path_view);
    }

    public void initEvents() {

    }
}
