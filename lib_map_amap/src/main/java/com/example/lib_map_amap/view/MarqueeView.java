package com.example.lib_map_amap.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by lenovo on 2017/4/14.
 */

public class MarqueeView extends LinearLayout {

    private static final int TEXTVIEW_VIRTUAL_WIDTH = 5000;

    private Context context;
    private TextView mTextField;
    private ScrollView mScrollView;

    private Paint mPaint;

    // 动画需要在字符串确定后才能确定
    private Animation mMoveText = null;

    private float widthOfMarqueeView;
    private float heightOfMarqueeView;

    /**
     * 字符串之间的间隔
     */
    private String interval = "    ";// 文字间隔

    private String stringOfItem = "";
    private String stringOfTextView = "";
    private String stringOfOrigin = "33";
    private float widthOfItem = 0;
    private float widthOfString = 0;
    private float startXOfAnimation = 0;// 动画的起始坐标
    private float endXOfAnimation = 0;
    private Runnable mAnimationStartRunnable;
    private int mSpeed = 5;
    private Interpolator mInterpolator = new LinearInterpolator();

    public MarqueeView(Context context) {
        super(context);
        init(context);
    }

    public MarqueeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public MarqueeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        // init helper
        this.context = context;
    }

    private Runnable startRunnable = new Runnable() {

        @Override
        public void run() {
            setText(stringOfOrigin);
        }
    };

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed) {

            widthOfMarqueeView = getWidth();
            heightOfMarqueeView = getHeight();

            // Logcat.d(TAG, "widthOfMarqueeView: " + widthOfMarqueeView);
            // Logcat.d(TAG, "heightOfMarqueeView: " + heightOfMarqueeView);
            // ////这里直接调用setText("");是不行的，为什么！！！
            postDelayed(startRunnable, 0);
        }
    }

    public void setText(String string) {
        stringOfOrigin = string;
        stringOfItem = string + interval;
        initViews();
        dealChange();
    }

    public void initViews() {
        clearMarquee();
        removeAllViews();
        mScrollView = new ScrollView(context);
        mTextField = new TextView(context);
        mPaint = mTextField.getPaint();

        mTextField.setSingleLine(true);
        mTextField.setTextColor(Color.parseColor("#FFFFFF"));
        mPaint.setFakeBoldText(true);
        mPaint.setAntiAlias(true);

        LayoutParams sv1lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        sv1lp.gravity = Gravity.CENTER;
        LayoutParams tv1lp = new LayoutParams(TEXTVIEW_VIRTUAL_WIDTH,
                LayoutParams.MATCH_PARENT);
        tv1lp.gravity = Gravity.CENTER;

        mScrollView.addView(mTextField, tv1lp);
        addView(mScrollView, sv1lp);
    }

    public void clearMarquee() {
        stopMarquee();
    }

    public void stopMarquee() {
        if (mAnimationStartRunnable == null)
            return;
        removeCallbacks(mAnimationStartRunnable);
        mTextField.clearAnimation();
        invalidate();
    }

    public void startMarquee() {
        mAnimationStartRunnable = new Runnable() {
            public void run() {
                mTextField.startAnimation(mMoveText);
            }
        };
        postDelayed(mAnimationStartRunnable, 0);
        invalidate();
    }

    /**
     * 文字高度固定时就可以获取这些宽度信息,这时认为字符串已经加工好了
     */
    public void dealLayoutChange() {

        // 需要重新计算
        stringOfTextView = stringOfItem + stringOfItem;
        widthOfItem = mPaint.measureText(stringOfItem);
        widthOfString = mPaint.measureText(stringOfTextView);
        while (widthOfString <= 2 * widthOfMarqueeView) {
            stringOfTextView += stringOfItem;
            widthOfString = mPaint.measureText(stringOfTextView);
        }
        widthOfString = mPaint.measureText(stringOfTextView);
        expandTextView();
        mTextField.setText(stringOfTextView);

    }

    public void dealChange() {
        // 设置字体大小
        setTextFitSize();
        // 处理文字 布局大小发生变化，布局变而文字不变，重设文字而布局不变，
        dealLayoutChange();
        // 设置动画
        prepareAnimation();
        // 开始滚动
        startMarquee();
    }

    /**
     * 设置TextView大小！！！
     */
    private void expandTextView() {

        mTextField.layout(getLeft(), getTop(),
                (int) (getLeft() + widthOfString + 5), getTop() + getHeight());
    }

    public void setTextFitSize() {
        mTextField.setTextSize(14);
    }

    public int getFitTextSize(Paint paint, int height) {

        // System.out.println("height: " + height);
        int minSize = 10;
        int maxSize = 200;
        int step = 1;

        // int heightOfText = height * 2 / 3;
        int heightOfText = height;
        while (minSize < maxSize) {

            paint.setTextSize(minSize);
            Paint.FontMetrics fm = paint.getFontMetrics();

            // //System.out.println("Math.ceil(fm.descent - fm.top): "
            // + Math.ceil(fm.descent - fm.top));
            if (Math.ceil(fm.descent - fm.top) >= heightOfText) {
                break;
            }
            minSize += step;
        }
        System.out.println("--- fit size: " + minSize + " ---");
        return minSize;
    }

    /**
     * 根据文字长度、view长度、item长度确定动画参数
     */
    private void prepareAnimation() {

        startXOfAnimation = -(widthOfString - widthOfMarqueeView) % widthOfItem;
        endXOfAnimation = -widthOfString + widthOfMarqueeView;

        final int duration = ((int) Math.abs(startXOfAnimation
                - endXOfAnimation) * mSpeed);


        mMoveText = new TranslateAnimation(startXOfAnimation, endXOfAnimation,
                0, 0);
        mMoveText.setDuration(duration);
        mMoveText.setInterpolator(mInterpolator);
        mMoveText.setFillAfter(true);

        mMoveText.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                startMarquee();
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}