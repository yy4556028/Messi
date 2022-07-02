package com.yuyang.messi.ui.category;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.listener.MultiPointTouchListener;
import com.yuyang.messi.ui.base.AppBaseActivity;

/**
 * 创建者: yuyang
 * 创建日期: 2015-06-28
 * 创建时间: 16:14
 * TouchActivity: touch demo
 *
 * @author yuyang
 * @version 1.0
 */
public class TouchActivity extends AppBaseActivity {

    private MultiPointTouchListener multiPointTouchListener;

    private ImageView imageView;

    private TextView translateBtn;

    private TextView scaleBtn;

    private TextView rotateBtn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_touch;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("Touch");

        multiPointTouchListener = new MultiPointTouchListener();

        imageView = findViewById(R.id.activity_touch_imageView);
        imageView.setImageResource(R.mipmap.ic_launcher);

        translateBtn = findViewById(R.id.activity_touch_textView_translate);
        translateBtn.setText("可移动");

        scaleBtn = findViewById(R.id.activity_touch_textView_scale);
        scaleBtn.setText("可缩放");

        rotateBtn = findViewById(R.id.activity_touch_textView_rotate);
        rotateBtn.setText("可旋转");

        initEvents();
    }

    public void initEvents() {

        multiPointTouchListener.setOnImageChangeListener(new MultiPointTouchListener.ImageChangeListener() {
            @Override
            public void onImageChange(float transX, float transY, float scale, float rotate) {
                if (multiPointTouchListener.canTranslate) {
                    translateBtn.setText("可移动\nX = " + transX + "\nY = " + transY);
                }

                if (multiPointTouchListener.canScale) {
                    scaleBtn.setText("可缩放\nscale=" + scale);
                }

                if (multiPointTouchListener.canRotate) {
                    rotateBtn.setText("可旋转\nrotate=" + rotate);
                }
            }
        });

        imageView.setOnTouchListener(multiPointTouchListener);
        translateBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (multiPointTouchListener.canTranslate) {
                    translateBtn.setText("不可移动");
                    multiPointTouchListener.canTranslate = false;
                } else {
                    translateBtn.setText("可移动");
                    multiPointTouchListener.canTranslate = true;
                }
            }
        });

        scaleBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (multiPointTouchListener.canScale) {
                    scaleBtn.setText("不可缩放");
                    multiPointTouchListener.canScale = false;
                } else {
                    scaleBtn.setText("可缩放");
                    multiPointTouchListener.canScale = true;
                }
            }
        });

        rotateBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (multiPointTouchListener.canRotate) {
                    rotateBtn.setText("不可旋转");
                    multiPointTouchListener.canRotate = false;
                } else {
                    rotateBtn.setText("可旋转");
                    multiPointTouchListener.canRotate = true;
                }
            }
        });
    }
}
