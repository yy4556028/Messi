package com.yuyang.messi.ui.category.chart;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.R;
import com.yuyang.messi.adapter.SensorRecyclerViewAdapter;

import java.util.Arrays;

public class ChartActivity extends AppBaseActivity {

    private RecyclerView recyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chart;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("Chart");

        recyclerView = findViewById(R.id.activity_chart_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String[] titles = new String[]{
                "EChart",
                "线状图",
        };

        Class[] classes = new Class[]{
                EChartActivity.class,
                LineChartActivity.class,
        };

        recyclerView.setAdapter(new SensorRecyclerViewAdapter(getActivity(), Arrays.asList(titles), Arrays.asList(classes)));
    }

}
