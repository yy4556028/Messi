package com.yuyang.messi.view.Progress;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.yuyang.messi.R;

/**
 * 仿iphone带进度的进度条，线程安全的View，可直接在线程中更新进度
 *
 * @author xiaanming
 */
public class RoundProgressBar extends View {

    private Paint paint;

    // 中间圆的颜色
    private int centreColor;

    // 中间环的颜色
    private int midRoundColor;

    // 外面环的颜色
    private int outRoundColor;

    // 圆环进度的颜色
    private int roundProgressColor;

    // 中间环的宽度
    private float midRoundWidth;

    // 外面环的宽度
    private float outRoundWidth;

    // 最大进度
    private int max;

    // 当前进度
    private float progress;

    // 之前的进度
    private float preProgress = 0;

    // 进度的风格，实心或者空心
    private int style;

    public static final int STROKE = 0;
    public static final int FILL = 1;

    // 是否自动增长 progress
    private boolean isAutoGrow = false;

    // 限制de总时间
    private int totalTime = 15000;

    // 本次的起始时间
    private long startTime;

    private AnimatorSet zoomInAnim, zoomOutAnim;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        paint = new Paint();

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RoundProgressBar);

        //获取自定义属性和默认值
        centreColor = mTypedArray.getColor(R.styleable.RoundProgressBar_centreColor, Color.YELLOW);
        midRoundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_midRoundColor, Color.BLACK);
        outRoundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_outRoundColor, Color.WHITE);
        roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.GREEN);

        midRoundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_midRoundWidth, 5);
        outRoundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_outRoundWidth, 5);
        max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
        style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);

        mTypedArray.recycle();
    }

    public void resetProgress() {
        isAutoGrow = false;
        startTime = 0;
        progress = 0;
        preProgress = 0;
    }

    public void startAutoGrow(boolean startAutoGrow) {
        isAutoGrow = startAutoGrow;

        if (isAutoGrow) {
            initZoomInAnim();
            zoomInAnim.start();
            startTime = System.currentTimeMillis();
            preProgress = progress;
            invalidate();
        } else {
            initZoomOutAnim();
            zoomOutAnim.start();
            startTime = 0;
            preProgress = 0;
        }
    }

    private void initZoomInAnim() {
        ObjectAnimator animZoomInX = ObjectAnimator.ofFloat(this, "scaleX", getScaleX(), 1.3f);
        ObjectAnimator animZoomInY = ObjectAnimator.ofFloat(this, "scaleY", getScaleY(), 1.3f);
        zoomInAnim = new AnimatorSet();
        zoomInAnim.play(animZoomInX).with(animZoomInY);
        zoomInAnim.setDuration(200);
    }

    private void initZoomOutAnim() {
        ObjectAnimator animZoomOutX = ObjectAnimator.ofFloat(this, "scaleX", getScaleX(), 1);
        ObjectAnimator animZoomOutY = ObjectAnimator.ofFloat(this, "scaleY", getScaleY(), 1);
        zoomOutAnim = new AnimatorSet();
        zoomOutAnim.play(animZoomOutX).with(animZoomOutY);
        zoomOutAnim.setDuration(200);
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centre = getWidth() / 2; //获取圆心的x坐标
        paint.setAntiAlias(true);  //消除锯齿

        int outRadius = (int) (centre - outRoundWidth / 2); //圆环的半径
        paint.setColor(outRoundColor); //设置圆环的颜色
        paint.setStrokeWidth(outRoundWidth); //设置圆环的宽度
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(centre, centre, outRadius, paint); //画出圆环

        int midRadius = (int) (outRadius - outRoundWidth / 2 - midRoundWidth / 2); //mid圆环的半径
        paint.setColor(midRoundColor);
        paint.setStrokeWidth(midRoundWidth);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(centre, centre, midRadius, paint);

        int centreRadius = (int) (midRadius - midRoundWidth / 2);
        paint.setColor(centreColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centre, centre, centreRadius, paint);

        /**
         * 画圆弧 ，画圆环的进度
         */
        //设置进度是实心还是空心
        paint.setStrokeWidth(outRoundWidth); //设置圆环的宽度
        paint.setColor(roundProgressColor);  //设置进度的颜色

        RectF oval = new RectF(centre - outRadius, centre - outRadius, centre
                + outRadius, centre + outRadius);  //用于定义的圆弧的形状和大小的界限

        if (isAutoGrow)
            progress = ((System.currentTimeMillis() - startTime) * (float) max / totalTime + preProgress);

        if (progress > max) {
            progress = max;
            isAutoGrow = false;
            preProgress = 0;
            initZoomOutAnim();
            zoomOutAnim.start();
            if (listener != null) {
                listener.onFull();
            }

        }

        switch (style) {
            case STROKE: {
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval, 0, 360 * progress / max, false, paint);  //根据进度画圆弧
                break;
            }
            case FILL: {
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                if (progress != 0)
                    canvas.drawArc(oval, 0, 360 * progress / max, true, paint);  //根据进度画圆弧
                break;
            }
        }

        if (isAutoGrow)
            postInvalidateDelayed(10);
    }

    public synchronized int getMax() {
        return max;
    }

    /**
     * 设置进度的最大值
     *
     * @param max
     */
    public synchronized void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    /**
     * 获取进度.需要同步
     *
     * @return
     */
    public synchronized float getProgress() {
        return progress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress
     */
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }
    }

    private OnFullListener listener;

    public interface OnFullListener {
        void onFull();
    }

    public void setOnFullListener(OnFullListener listener) {
        this.listener = listener;
    }

}
