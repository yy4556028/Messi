package com.yuyang.messi.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.yuyang.messi.R;

/**
 * 创建者: yuyang
 * 创建日期: 2015-07-02
 * 创建时间: 16:14
 * DrawView: 涂鸦view
 *
 * @author yuyang
 * @version 1.0
 */
public class PathView extends View {

    private float[] pos;
    private float[] tan;

    private float mX, mY;
    private Path mPath;
    private Path segmentPath;
    private Paint mPaint;
    private Paint segmentPaint;
    private PathMeasure pathMeasure;
    private static final float TOUCH_TOLERANCE = 0; // 两点间距大于该距离才绘制

    private Bitmap bitmap;

    private Matrix bitmapMatrix;

    public PathView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();//创建画笔渲染对象
        mPaint.setAntiAlias(true);//设置抗锯齿，让绘画比较平滑
        mPaint.setDither(true);//设置递色
        mPaint.setStyle(Paint.Style.STROKE);//画笔的类型有三种（1.FILL 2.FILL_AND_STROKE 3.STROKE ）
        mPaint.setStrokeJoin(Paint.Join.ROUND);//默认类型是MITER（1.BEVEL 2.MITER 3.ROUND ）
        mPaint.setStrokeCap(Paint.Cap.ROUND);//默认类型是BUTT（1.BUTT 2.ROUND 3.SQUARE ）
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(5);
        mPaint.setPathEffect(new DashPathEffect(new float[]{25, 25, 25, 25}, 0));

        segmentPaint = new Paint();//创建画笔渲染对象
        segmentPaint.setAntiAlias(true);//设置抗锯齿，让绘画比较平滑
        segmentPaint.setDither(true);//设置递色
        segmentPaint.setStyle(Paint.Style.STROKE);//画笔的类型有三种（1.FILL 2.FILL_AND_STROKE 3.STROKE ）
        segmentPaint.setStrokeJoin(Paint.Join.ROUND);//默认类型是MITER（1.BEVEL 2.MITER 3.ROUND ）
        segmentPaint.setStrokeCap(Paint.Cap.ROUND);//默认类型是BUTT（1.BUTT 2.ROUND 3.SQUARE ）
        segmentPaint.setColor(Color.BLUE);
        segmentPaint.setStrokeWidth(5);
        Shader shader = new LinearGradient(0, 0, 1000, 1000, new int[]{Color.RED, Color.YELLOW, Color.BLUE}, null, Shader.TileMode.REPEAT);
        segmentPaint.setShader(shader);
        segmentPaint.setShadowLayer(15, 10, 10, Color.RED);

        mPath = new Path();
        segmentPath = new Path();
        pathMeasure = new PathMeasure();
        pos = new float[2];
        tan = new float[2];

        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        bitmapMatrix = new Matrix();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(segmentPath, segmentPaint);

        if (!segmentPath.isEmpty()) {
            canvas.drawBitmap(bitmap, bitmapMatrix, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://手指开始按压屏幕，这个动作包含了初始化位置
                onTouchDown(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE://手指按压屏幕时，位置的改变触发，这个方法在ACTION_DOWN和ACTION_UP之间。
                onTouchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP://手指离开屏幕，不再按压屏幕
                onTouchUp(x, y);
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    private void onTouchDown(float x, float y) {
        mPath.reset();//将上次的路径保存起来，并重置新的路径。
        segmentPath.reset();
        mPath.moveTo(x, y);//设置新的路径“轮廓”的开始
        mX = x;
        mY = y;
    }

    private void onTouchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void onTouchUp(float x, float y) {
        mPath.lineTo(mX, mY);//从最后一个指定的xy点绘制一条线，如果没有用moveTo方法，那么起始点表示（0，0）点。
        pathMeasure.setPath(mPath, false);
        start();
    }

    private void start() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, pathMeasure.getLength());
        valueAnimator.setDuration((long) pathMeasure.getLength());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                segmentPath.reset();
                pathMeasure.getPosTan(value, pos, tan);
                pathMeasure.getSegment(0, value, segmentPath, true);
                /**
                 * 如果在安卓4.4或者之前的版本，在默认开启硬件加速的情况下，更改 dst 的内容后可能绘制会出现问题，请关闭硬件加速或者给 dst 添加一个单个操作，例如: dst.rLineTo(0, 0)
                 */
                segmentPath.rLineTo(0, 0);

                float degree = (float) (Math.atan2(tan[1], tan[0]) * 180 / Math.PI);

                bitmapMatrix.reset();
                bitmapMatrix.postRotate(degree + 90, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
                bitmapMatrix.postTranslate(pos[0] - bitmap.getWidth() / 2, pos[1] - bitmap.getHeight() / 2);
                invalidate();
            }
        });
        valueAnimator.start();
    }

}