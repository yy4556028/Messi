package com.yuyang.messi.view.Progress;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

public class CircleProgress extends View {

    private static final int DEFAULT_MIN_WIDTH = 400;

    private int width;
    private int height;

    private int progress = 0;

    private int totalColor = Color.parseColor("#ECECEC");
    private int[] progressColor;

    private Paint totalPaint = new Paint();
    private Paint progressPaint = new Paint();

    private RectF rectF;

    private float rate = 0.15f;

    private ValueAnimator valueAnimator;

    public CircleProgress(Context context) {
        this(context, null);
    }

    public CircleProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void resetParams() {
        width = getWidth();
        height = getHeight();

        float arcWidth = Math.min(width, height) / 2f * rate;//圆弧宽度
        float centerX = width / 2f;//圆心 x 坐标
        float centerY = height / 2f;//圆心 y 坐标
        float radius = Math.min(width, height) / 2f - arcWidth / 2;//半径

        rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);//用于定义圆弧的形状和大小的 Rect
    }

    private void initPaint() {

        totalPaint.reset();
        totalPaint.setAntiAlias(true);
        totalPaint.setStrokeWidth(Math.min(width, height) / 2f * rate);
        totalPaint.setStyle(Paint.Style.STROKE);
        totalPaint.setColor(totalColor);

        if (progressColor == null || progressColor.length == 0)
            return;
        progressPaint.reset();
        progressPaint.setAntiAlias(true);
        progressPaint.setStrokeWidth(Math.min(width, height) / 2f * rate);
        progressPaint.setStyle(Paint.Style.STROKE);
        if (progressColor.length > 1) {
            progressPaint.setShader(new SweepGradient(width / 2f, height / 2f, progressColor, null));
        } else {
            progressPaint.setColor(progressColor[0]);
        }
    }

    public void setTotalColor(int totalColor) {
        this.totalColor = totalColor;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public void setValue(int value, int[] valueColor, int duration, Animator.AnimatorListener listener) {

        this.progressColor = valueColor;

        resetParams();
        initPaint();

        valueAnimator = ValueAnimator.ofInt(progress, value);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                return input;
//                return 1 - (1-input)*(1-input)*(1-input);
            }
        });

        if (listener != null) {
            valueAnimator.addListener(listener);
        }
        valueAnimator.start();
    }

    public void anim(int duration) {
        if (valueAnimator != null) return;
        int value = progress;
        progress = 0;
        setValue(value, progressColor, duration, null);
    }

    public void stop() {
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            valueAnimator.cancel();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
    }

    private int measure(int measureSpec) {
        int result = DEFAULT_MIN_WIDTH;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.EXACTLY: {
                result = specSize;
                break;
            }
            case MeasureSpec.AT_MOST: {
                result = Math.min(result, specSize);
                break;
            }
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        resetParams();
        initPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (rectF == null) {
            return;
        }
        canvas.drawArc(rectF, 0, 360, false, totalPaint);

        canvas.rotate(-90, width / 2f, height / 2f);
        canvas.drawArc(rectF, 0, 3.6f * progress, false, progressPaint);// progress / 100 * 360
        canvas.rotate(90, width / 2f, height / 2f);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (valueAnimator != null) {
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    progress = (int) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (valueAnimator != null) {
            valueAnimator.removeAllUpdateListeners();
        }
    }
}
