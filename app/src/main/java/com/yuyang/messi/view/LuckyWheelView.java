package com.yuyang.messi.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.yuyang.messi.R;

/**
 * http://blog.csdn.net/lmj623565791/article/details/41722441
 */

public class LuckyWheelView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder mHolder;
    /**
     * 与SurfaceHolder绑定的Canvas
     */
    private Canvas mCanvas;
    /**
     * 用于绘制的线程
     */
    private Thread thread;
    /**
     * 线程的控制开关
     */
    private boolean isRunning;

    /**
     * 抽奖的文字
     */
    private String[] mStrs = new String[]{"单反相机", "IPAD", "恭喜发财", "IPHONE",
            "妹子一只", "恭喜发财"};
    /**
     * 每个盘块的颜色
     */
    private int[] mColors = new int[]{ContextCompat.getColor(getContext(), R.color.red)
            , ContextCompat.getColor(getContext(), R.color.yellow)
            , ContextCompat.getColor(getContext(), R.color.blue)
            , ContextCompat.getColor(getContext(), R.color.cyan)
            , ContextCompat.getColor(getContext(), R.color.gray)
            , ContextCompat.getColor(getContext(), R.color.green)
    };
    /**
     * 与文字对应的图片
     */
    private int[] mImgs = new int[]{R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher};

    /**
     * 与文字对应图片的bitmap数组
     */
    private Bitmap[] mImgsBitmap;
    /**
     * 盘块的个数
     */
    private int mItemCount = mStrs.length;

    /**
     * 绘制盘块的范围
     */
    private RectF mRange = new RectF();
    /**
     * 圆的直径
     */
    private int mRadius;
    /**
     * 绘制盘快的画笔
     */
    private Paint mArcPaint;

    /**
     * 绘制文字的画笔
     */
    private Paint mTextPaint;

    /**
     * 滚动的速度
     */
    private double mSpeed;
    private volatile float mStartAngle = 0;
    /**
     * 是否点击了停止
     */
    private State state = State.WAIT;

    public enum State {
        START, END, WAIT;
    }

    /**
     * 控件的中心位置
     */
    private int mCenter;
    /**
     * 控件的padding，这里我们认为4个padding的值一致，以paddingleft为标准
     */
    private int mPadding;

    /**
     * 背景图的bitmap
     */
    private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(),
            R.drawable.nav_background);
    /**
     * 文字的大小
     */
    private float mTextSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());

    public LuckyWheelView(Context context) {
        this(context, null);
    }

    public LuckyWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mHolder = getHolder();
        mHolder.addCallback(this);

        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
    }

    /**
     * 设置控件为正方形
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
        // 获取圆形的直径
        mRadius = width - getPaddingLeft() - getPaddingRight();
        // padding值
        mPadding = getPaddingLeft();
        // 中心点
        mCenter = width / 2;
        setMeasuredDimension(width, width);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 初始化绘制圆弧的画笔
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);
        // 初始化绘制文字的画笔
        mTextPaint = new Paint();
        mTextPaint.setColor(0xFFFFFFFF);
        mTextPaint.setTextSize(mTextSize);
        // 圆弧的绘制范围
        mRange = new RectF(getPaddingLeft(), getPaddingLeft(),
                mRadius + getPaddingLeft(), mRadius + getPaddingLeft());

        // 初始化图片
        mImgsBitmap = new Bitmap[mItemCount];
        for (int i = 0; i < mItemCount; i++) {
            mImgsBitmap[i] = BitmapFactory.decodeResource(getResources(), mImgs[i]);
        }

        // 开启线程
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 通知关闭线程
        isRunning = false;
    }

    @Override
    public void run() {
        // 不断的进行draw
        while (isRunning) {
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();

            // 最少 50ms draw 一次
            try {
                if (end - start < 50) {
                    Thread.sleep(50 - (end - start));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void draw() {
        try {
            // 获得canvas
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null) {
                // 绘制背景图
                drawBg();

                /**
                 * 绘制每个块块，每个块块上的文本，每个块块上的图片
                 */
                float tmpAngle = mStartAngle;
                float sweepAngle = (float) (360 / mItemCount);
                for (int i = 0; i < mItemCount; i++) {
                    // 绘制快快
                    mArcPaint.setColor(mColors[i]);
                    mCanvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint);
                    // 绘制文本
                    drawText(tmpAngle, sweepAngle, mStrs[i]);
                    // 绘制Icon
                    drawIcon(tmpAngle, i);

                    tmpAngle += sweepAngle;
                }

                // 如果mSpeed不等于0，则相当于在滚动
                mStartAngle += mSpeed;

                // 点击停止时，设置mSpeed为递减，为0值转盘停止
                if (state == State.END) {
                    mSpeed -= 1;
                } else if (state == State.START) {
                    mSpeed += 1;
                    if (mSpeed >= 75) {
                        mSpeed = 75;
                    }
                }
                if (mSpeed <= 0 && state != State.WAIT) {
                    mSpeed = 0;
                    if (state != State.WAIT) {
                        state = State.WAIT;
                        // 根据当前旋转的mStartAngle计算当前滚动到的区域
                        final int index = calInExactArea(mStartAngle);
                        if (index != -1 && onEndListener != null) {
                            ((Activity)getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onEndListener.onEnd(index);
                                }
                            });
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null)
                mHolder.unlockCanvasAndPost(mCanvas);
        }

    }

    /**
     * 根据当前旋转的mStartAngle计算当前滚动到的区域 绘制背景，不重要，完全为了美观
     */
    private void drawBg() {
        mCanvas.drawColor(0xFFFFFFFF);
        mCanvas.drawBitmap(mBgBitmap, null, new Rect(mPadding / 2,
                mPadding / 2, getMeasuredWidth() - mPadding / 2,
                getMeasuredWidth() - mPadding / 2), null);
    }

    /**
     * 根据当前旋转的mStartAngle计算当前滚动到的区域
     *
     * @param startAngle
     */
    public int calInExactArea(float startAngle) {
        // 让指针从水平向右开始计算
        float rotate = startAngle + 90;
        rotate %= 360;
        return (int) ((360 - rotate) / (360 / mItemCount));
    }

    /**
     * 绘制图片
     *
     * @param startAngle
     * @param i
     */
    private void drawIcon(float startAngle, int i) {
        // 设置图片的宽度为直径的1/8
        int imgWidth = mRadius / 8;

        float angle = (float) ((30 + startAngle) * (Math.PI / 180));

        int x = (int) (mCenter + mRadius / 2 / 2 * Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2 / 2 * Math.sin(angle));

        // 确定绘制图片的位置
        Rect rect = new Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth
                / 2, y + imgWidth / 2);

        mCanvas.drawBitmap(mImgsBitmap[i], null, rect, null);

    }

    /**
     * 绘制文本
     *
     * @param startAngle
     * @param sweepAngle
     * @param string
     */
    private void drawText(float startAngle, float sweepAngle, String string) {
        Path path = new Path();
        path.addArc(mRange, startAngle, sweepAngle);
        float textWidth = mTextPaint.measureText(string);
        // 利用水平偏移让文字居中
        float hOffset = (float) (mRadius * Math.PI / mItemCount / 2 - textWidth / 2);// 水平偏移
        float vOffset = mRadius / 2 / 6;// 垂直偏移
        mCanvas.drawTextOnPath(string, path, hOffset, vOffset, mTextPaint);
    }

    /**
     * 点击开始旋转
     *
     * @param luckyIndex
     */
    public void luckyStart(int luckyIndex) {
        this.luckyIndex = luckyIndex;
        state = State.START;
    }

    int luckyIndex;

    public void luckyEnd() {

        // 如果指定了 luckyIndex 则作弊。。。
        if (luckyIndex >= 0 && luckyIndex < mItemCount) {

            // 每项角度大小
            float angle = (float) (360 / mItemCount);
            // 中奖角度范围（因为指针向上，所以水平第一项旋转到指针指向，需要旋转210~270；）
            float from = 270 - (luckyIndex + 1) * angle;
            float to = from + angle;

            /**
             * <pre>
             *  (1 + v1) * v1 / 2 = target ;
             *  v1*v1 + v1 - 2target = 0 ;
             *  v1=(-1+(1*1 + 8 * 1 * target))/2;  x = [-b +- 根号(b * b - 4ac)] / 2 * a
             * </pre>
             */

            int keep = (int) (((1 + mSpeed) * mSpeed / 2) / 360);

            // 停下来时旋转的距离
            float targetFrom = from + keep * 360;
            float v1 = (float) (Math.sqrt(1 * 1 + 8 * 1 * targetFrom) - 1) / 2;
            float targetTo = to + keep * 360;
            float v2 = (float) (Math.sqrt(1 * 1 + 8 * 1 * targetTo) - 1) / 2;

            mSpeed = (float) (v1 + Math.random() * (v2 - v1));
            mStartAngle = 0;
        }
        state = State.END;
    }

    public boolean isStart() {
        return mSpeed != 0;
    }

    public State getState() {
        return state;
    }

    private OnEndListener onEndListener;

    public interface OnEndListener {
        void onEnd(int index);
    }

    public void setOnEndListener(OnEndListener listener) {
        onEndListener = listener;
    }

}