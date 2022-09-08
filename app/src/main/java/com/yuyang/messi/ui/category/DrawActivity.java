package com.yuyang.messi.ui.category;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yuyang.lib_base.helper.SelectImageUtil;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.ui.header.HeaderRightBean;
import com.yuyang.lib_base.utils.BitmapUtil;
import com.yuyang.lib_base.utils.FileUtil;
import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.lib_base.utils.StorageUtil;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.adapter.DrawColorGridAdapter;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.lib_base.utils.DateUtil;
import com.yuyang.messi.view.DrawView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 创建者: yuyang
 * 创建日期: 2015-06-28
 * 创建时间: 16:14
 * DrawActivity: draw demo
 *
 * @author yuyang
 * @version 1.0
 */
public class DrawActivity extends AppBaseActivity {

    // 涂鸦 View
    private DrawView drawView;

    // 颜色 gridView
    private GridView gridView;

    // 颜色 gridView adapter
    private DrawColorGridAdapter adapter;

    // 画笔粗细
    private RelativeLayout paintLine0;
    private RelativeLayout paintLine1;
    private RelativeLayout paintLine2;
    private RelativeLayout paintLine3;

    private ImageView paintClearBtn; // 清屏

    // 颜色合集
    private final int[] paintColors = new int[]{R.color.red, R.color.yellow, R.color.blue, R.color.green, R.color.purple, R.color.black};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_draw;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initEvents();

        Drawable drawable = getResources().getDrawable(R.drawable.nav_background);

        drawView.setBitmap(BitmapUtil.drawableToBitmap0(drawable));

        drawView.setPaintColor(getResources().getColor(paintColors[0]));
        paintLine0.performClick();
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("涂鸦");
        List<HeaderRightBean> rightBeanList = new ArrayList<>();
        rightBeanList.add(new HeaderRightBean("保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBitmap();
            }
        }));
        headerLayout.setRight(rightBeanList);

        drawView = findViewById(R.id.activity_draw_view);

        gridView = findViewById(R.id.activity_draw_gridView);
        adapter = new DrawColorGridAdapter(this, paintColors);
        gridView.setAdapter(adapter);

        paintLine0 = findViewById(R.id.activity_draw_paint_line0);
        paintLine1 = findViewById(R.id.activity_draw_paint_line1);
        paintLine2 = findViewById(R.id.activity_draw_paint_line2);
        paintLine3 = findViewById(R.id.activity_draw_paint_line3);

        paintClearBtn = findViewById(R.id.activity_draw_clear);
    }

    public void initEvents() {

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.selectPosition = position;
                adapter.notifyDataSetChanged();
                drawView.setPaintColor(getResources().getColor(paintColors[position]));
            }
        });

        paintLine0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPaintLineSelect();
                paintLine0.setBackgroundResource(R.drawable.activity_draw_paint_select);
                drawView.setPaintLineWidth(PixelUtils.dp2px(1));
            }
        });

        paintLine1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPaintLineSelect();
                paintLine1.setBackgroundResource(R.drawable.activity_draw_paint_select);
                drawView.setPaintLineWidth(PixelUtils.dp2px(2));
            }
        });

        paintLine2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPaintLineSelect();
                paintLine2.setBackgroundResource(R.drawable.activity_draw_paint_select);
                drawView.setPaintLineWidth(PixelUtils.dp2px(3));
            }
        });

        paintLine3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPaintLineSelect();
                paintLine3.setBackgroundResource(R.drawable.activity_draw_paint_select);
                drawView.setPaintLineWidth(PixelUtils.dp2px(4));
            }
        });

        paintClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.clear();
            }
        });
    }

    private void clearPaintLineSelect() {
        paintLine0.setBackgroundResource(0);
        paintLine1.setBackgroundResource(0);
        paintLine2.setBackgroundResource(0);
        paintLine3.setBackgroundResource(0);
    }

    // 保存图片
    public void saveBitmap() {
        Uri uri = FileUtil.saveImageV29(drawView.getDrawBitmap(),
                StorageUtil.getPublicPath("/涂鸦/" + DateUtil.formatDataToString(new Date(), "yyyy-MM-dd_HH:mm:ss") + ".png"));

        if (uri == null) {
            ToastUtil.showToast("图片保存失败");
        } else {
            ToastUtil.showToast("图片已保存到" + SelectImageUtil.getPath(this, uri));
        }
    }
}
