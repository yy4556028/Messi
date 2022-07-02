package com.yuyang.messi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.yuyang.messi.R;

public class DashedLineView extends View {

    private boolean isTop;//true为从上开始

    private Paint paint;
    private Path path;

    public DashedLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DashedLineView);
        isTop = ta.getBoolean(R.styleable.DashedLineView_dlv_top_or_bottom, true);
        ta.recycle();
        init();
    }


    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));//颜色可以自己设置

        path = new Path();

        PathEffect effects;

        if (isTop) {
            effects = new DashPathEffect(new float[]{8, 8, 8, 8}, 1);//设置虚线的间隔和点的长度
        } else {
            effects = new DashPathEffect(new float[]{0, 8, 8, 0}, 1);//设置虚线的间隔和点的长度
        }

        paint.setPathEffect(effects);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        path.reset();
        if (isTop) {
            path.moveTo(0, 0);//起始坐标
            path.lineTo(0, getMeasuredHeight());//终点坐标
        } else {
            path.moveTo(0, getMeasuredHeight());//起始坐标
            path.lineTo(0, 0);//终点坐标
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }
}
