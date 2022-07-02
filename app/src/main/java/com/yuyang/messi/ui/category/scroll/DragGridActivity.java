package com.yuyang.messi.ui.category.scroll;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yuyang.lib_base.myglide.GlideApp;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.adapter.DragGridViewAdapter;
import com.yuyang.messi.adapter.DragGridViewPagerAdapter;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.view.DragGridView;
import com.yuyang.messi.view.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建者: yuyang
 * 创建日期: 2015-07-17
 * 创建时间: 16:14
 * DrawActivity: drag demo
 *
 * @author yuyang
 * @version 1.0
 */
public class DragGridActivity extends AppBaseActivity {

    private int GRID_HEIGHT_PER_ROW;

    private int ROW_NUM = 1;

    private int COLUMNS_NUM = 4;

    private ImageView showImage;

    private Button foldBtn;

    private LinearLayout bottomLyt;

    private NoScrollViewPager viewPager;

    private ImageView turnLeft, turnRight;

    private DragGridViewPagerAdapter viewPagerAdapter;

    private List<DragGridView> viewPagerList;

    private List<String> gridDataList;

    private DragGridView.OnDragListener dragListener;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_drag_grid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvents();
        makeData();
        setGridView();
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("DragGrid");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        GRID_HEIGHT_PER_ROW = dm.heightPixels / 6;

        showImage = findViewById(R.id.activity_drag_imageView);
        foldBtn = findViewById(R.id.activity_drag_fold);
        foldBtn.setText("展开");

        bottomLyt = findViewById(R.id.activity_drag_bottom);
        viewPager = findViewById(R.id.activity_drag_viewPager);
        turnLeft = findViewById(R.id.activity_drag_turn_left);
        turnRight = findViewById(R.id.activity_drag_turn_right);

    }

    /**
     * 造图片url数据
     */
    private void makeData() {
        gridDataList = new ArrayList<>();
        gridDataList.add("http://cms-bucket.nosdn.127.net/ddff45c6041c41e48ddc78dd64375d0820170311100045.jpeg?imageView&thumbnail=550x0");
        gridDataList.add("http://tv.cnr.cn/jbty/201204/W020120405345155502952.jpg");
        gridDataList.add("http://img1.dongqiudi.com/fastdfs/M00/04/43/oYYBAFfB9quAFORnAARqi9mYMpM828.gif");
        gridDataList.add("http://cms-bucket.nosdn.127.net/ddff45c6041c41e48ddc78dd64375d0820170311100045.jpeg?imageView&thumbnail=550x0");
        gridDataList.add("http://tv.cnr.cn/jbty/201204/W020120405345155502952.jpg");
        gridDataList.add("http://img1.dongqiudi.com/fastdfs/M00/04/43/oYYBAFfB9quAFORnAARqi9mYMpM828.gif");
        gridDataList.add("http://cms-bucket.nosdn.127.net/ddff45c6041c41e48ddc78dd64375d0820170311100045.jpeg?imageView&thumbnail=550x0");
        gridDataList.add("http://tv.cnr.cn/jbty/201204/W020120405345155502952.jpg");
        gridDataList.add("http://img1.dongqiudi.com/fastdfs/M00/04/43/oYYBAFfB9quAFORnAARqi9mYMpM828.gif");
    }

    private void initEvents() {

        dragListener = new DragGridView.OnDragListener() {
            @Override
            public void onDrag(int position) {
                int index = viewPager.getCurrentItem() * ROW_NUM * COLUMNS_NUM + position;
                GlideApp.with(getActivity())
                        .load(gridDataList.get(index))
                        .into(showImage);
            }
        };

        foldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 如果当前是1行显示
                if (ROW_NUM == 1) {
                    ROW_NUM = 2;
                    foldBtn.setText("收起");
                    setGridView();
                } else {
                    ROW_NUM = 1;
                    foldBtn.setText("展开");
                    setGridView();
                }
            }
        });

        turnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager != null && viewPager.getCurrentItem() > 0)
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            }
        });

        turnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager != null && viewPager.getCurrentItem() < viewPager.getChildCount())
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });
    }

    private void setGridView() {

        if (viewPagerList != null) {
            viewPagerList.clear();
        } else {
            viewPagerList = new ArrayList<>();
        }


        int gridHeight = GRID_HEIGHT_PER_ROW * ROW_NUM;
        int pageCount = (int) Math.ceil((float) gridDataList.size() / (ROW_NUM * COLUMNS_NUM));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, gridHeight);

        bottomLyt.setLayoutParams(params);

        DragGridView gridView;
        DragGridViewAdapter adapter;

        for (int i = 0; i < pageCount; i++) {
            gridView = new DragGridView(this);
            adapter = new DragGridViewAdapter(this, gridDataList.subList(ROW_NUM * COLUMNS_NUM * i, Math.min(gridDataList.size(), ROW_NUM * COLUMNS_NUM * (i + 1))), GRID_HEIGHT_PER_ROW);

            gridView.setLayoutParams(params);
            gridView.setNumColumns(COLUMNS_NUM);
            gridView.setHorizontalSpacing(8);
            gridView.setVerticalSpacing(8);
            gridView.setListener(dragListener);
            gridView.setAdapter(adapter);

            viewPagerList.add(gridView);
        }
        viewPagerAdapter = new DragGridViewPagerAdapter(viewPagerList);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(viewPagerAdapter);
    }

}
