package com.yuyang.messi.ui.category;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.gesture_detector.RotateGestureDetector;
import com.yuyang.messi.ui.base.AppBaseActivity;

public class TouchNewActivity extends AppBaseActivity {

    private ImageView imageView;

    private TextView translateBtn;

    private TextView scaleBtn;

    private TextView rotateBtn;

    public boolean canTranslate = true;
    public boolean canScale = true;
    public boolean canRotate = true;

    private final Matrix matrix = new Matrix();
    private float baseScale = 1;
    private float preScale = 1;
    private float baseRotate = 1;
    private float preRotate = 1;

    private PointF centerPoint;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private RotateGestureDetector rotateGestureDetector;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_touch;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("TouchNew");

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
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(@NonNull MotionEvent e) {
                return super.onDown(e);
            }

            @Override
            public boolean onScroll(@Nullable MotionEvent downEvent, @NonNull MotionEvent currentEvent, float distanceX, float distanceY) {
                if (downEvent != null) {
                    matrix.postTranslate(-distanceX, -distanceY);
                    imageView.setImageMatrix(matrix);
                }
                return super.onScroll(downEvent, currentEvent, distanceX, distanceY);
            }
        });
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(@NonNull ScaleGestureDetector detector) {
                float scale = baseScale * detector.getScaleFactor();
                matrix.postScale(
                        scale / preScale,
                        scale / preScale,
                        detector.getFocusX(),
                        detector.getFocusY());
                imageView.setImageMatrix(matrix);
                preScale = scale;
                return super.onScale(detector);
            }

            @Override
            public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
                baseScale = baseScale * detector.getScaleFactor();
                super.onScaleEnd(detector);
            }
        });
        rotateGestureDetector = new RotateGestureDetector(this, new RotateGestureDetector.SimpleOnRotateGestureListener() {
            @Override
            public boolean onRotate(RotateGestureDetector detector) {
                float rotate = baseRotate - detector.getRotationDegreesDelta();
                matrix.postRotate(rotate - preRotate, centerPoint.x, centerPoint.y);
                imageView.setImageMatrix(matrix);
                preRotate = rotate;
                return super.onRotate(detector);
            }

            @Override
            public void onRotateEnd(RotateGestureDetector detector) {
                baseRotate = baseRotate - detector.getRotationDegreesDelta();
                super.onRotateEnd(detector);
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                centerPoint = getCenterPoint(event);
                if (canTranslate) {
                    gestureDetector.onTouchEvent(event);
                }
                if (canScale) {
                    scaleGestureDetector.onTouchEvent(event);
                }
                if (canRotate) {
                    rotateGestureDetector.onTouchEvent(event);
                }
                return true;
            }
        });

        translateBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canTranslate) {
                    translateBtn.setText("不可移动");
                    canTranslate = false;
                } else {
                    translateBtn.setText("可移动");
                    canTranslate = true;
                }
            }
        });

        scaleBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canScale) {
                    scaleBtn.setText("不可缩放");
                    canScale = false;
                } else {
                    scaleBtn.setText("可缩放");
                    canScale = true;
                }
            }
        });

        rotateBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canRotate) {
                    rotateBtn.setText("不可旋转");
                    canRotate = false;
                } else {
                    rotateBtn.setText("可旋转");
                    canRotate = true;
                }
            }
        });
    }

    private PointF getCenterPoint(MotionEvent event) {
        int pointerCount = event.getPointerCount(); // 获取触摸点的数量
        float sumX = 0, sumY = 0;

        // 遍历所有触摸点并计算X和Y坐标的总和
        for (int i = 0; i < pointerCount; i++) {
            sumX += event.getX(i); // 累加X坐标
            sumY += event.getY(i); // 累加Y坐标
        }

        // 计算中心点坐标
        float centerX = sumX / pointerCount;
        float centerY = sumY / pointerCount;
        return new PointF(centerX, centerY);
    }

}
