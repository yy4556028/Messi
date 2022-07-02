package com.yuyang.messi.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuyang.messi.R;

/**
 * 创建者: yuyang
 * 创建日期: 2015-05-21
 * 创建时间: 21:14
 * AnimFragment:模仿建行圆形菜单 ViewGroup
 * <p>
 * http://blog.csdn.net/lmj623565791/article/details/43131133
 *
 * @author yuyang
 * @version 1.0
 */
public class CircleMenuLayout extends ViewGroup {
    private int mRadius;

    // 该容器内child item 默认尺寸
    private static final float RADIO_DEFAULT_CHILD_DIMENSION = 1 / 4f;
    /**
     * 菜单中心child默认尺寸
     */
    private static final float RADIO_DEFAULT_CENTERITEM_DIMENSION = 1 / 3f;
    /**
     * 该容器内边距 无视padding，如需边距设置该属性
     */
    private static final float RADIO_PADDING_LAYOUT = 1 / 12f;

    /**
     * 当每秒移动角度达到该值时，认为是快速移动
     */
    private static final int FLINGABLE_VALUE = 300;

    /**
     * 如果移动角度达到改值，屏蔽点击
     */
    private static final int NOCLICK_VALUE = 3;

    /**
     * 当每秒移动角度达到该值时，认为是快速移动
     */
    private int mFlingableValue = FLINGABLE_VALUE;
    /**
     * 该容器内边距 无视padding，如需边距设置该属性
     */
    private float mPadding;
    /**
     * 布局时的开始角度
     */
    private double mStartAngle = -90;
    /**
     * 菜单项XX
     */
    private String[] mItemTexts;
    /**
     * 菜单项图标
     */
    private int[] mItemImgs;

    /**
     * 菜单的个数
     */
    private int mMenuItemCount;

    /**
     * 检测按下到抬起时旋转的角度
     */
    private float mTmpAngle;
    /**
     * 检测按下到抬起时使用的时间
     */
    private long mDownTime;

    /**
     * 判断是否正在自动滚动
     */
    private boolean isFling;

    public boolean isOpen = true;

    private boolean isAnimPlaying = false;

    private int mMenuItemLayoutId = R.layout.view_circle_menu_layout;

    public CircleMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 无视padding
        setPadding(0, 0, 0, 0);
    }

    /**
     * 设置布局的宽和高 并测量menu item 宽高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int resWidth = 0;
        int resHeight = 0;

        /**
         * 根据传入的参数，分别获取测量模式和测量值
         */
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        /**
         * 如果宽和高的测量模式非精确值
         */
        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY) {
            // 主要设置为背景图的高度
            resWidth = getSuggestedMinimumWidth();
            // 如果未设置背景图片，则设置为屏幕宽高默认值
            resWidth = resWidth == 0 ? getDefaultWidth() : resWidth;

            resHeight = getSuggestedMinimumHeight();
            // 果未设置背景图片，则设置为屏幕宽高默认值
            resHeight = resHeight == 0 ? getDefaultWidth() : resHeight;
        } else {
            // 如果都设置为精确值， 则取最小值
            resWidth = resHeight = Math.min(width, height);
        }

        setMeasuredDimension(resWidth, resHeight);

        // 获得半径
        mRadius = Math.max(getMeasuredWidth(), getMeasuredHeight());

        // menu item 数量
        final int count = getChildCount();
        // menu item 尺寸
        int childSize = (int) (mRadius * RADIO_DEFAULT_CHILD_DIMENSION);
        // menu item 测量模式
        int childMode = MeasureSpec.EXACTLY;

        // 迭代测量
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            // 计算menu item 尺寸 以及设置好的模式 去对item进行测量
            int makeMeasureSpec = -1;

            if (child.getId() == R.id.id_circle_menu_item_center) {
                makeMeasureSpec = MeasureSpec.makeMeasureSpec((int) (mRadius * RADIO_DEFAULT_CENTERITEM_DIMENSION), childMode);
            } else {
                makeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize, childMode);
            }
            child.measure(makeMeasureSpec, makeMeasureSpec);
        }

        mPadding = RADIO_PADDING_LAYOUT * mRadius;

    }

    public interface OnMenuItemClickListener {
        void itemClick(View view, int pos);

        void itemCenterClick(View view);
    }

    private OnMenuItemClickListener mOnMenuItemClickListener;

    public void setOnMenuItemClickListener(OnMenuItemClickListener mOnMenuItemClickListener) {
        this.mOnMenuItemClickListener = mOnMenuItemClickListener;
    }

    /**
     * 设置menu item的位置
     */
    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int layoutRadius = mRadius;

        // Laying out the child views
        final int childCount = getChildCount();

        int left, top;
        // menu item 的尺寸
        int cWidth = (int) (layoutRadius * RADIO_DEFAULT_CHILD_DIMENSION);

        double corner;//角度

        // 遍历去设置menu item 的位置
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            if (child.getId() == R.id.id_circle_menu_item_center)
                continue;

            if (child.getVisibility() == GONE) {
                continue;
            }
            corner = 360 / (childCount - 1) * (i - 1);

            mStartAngle %= 360;

            // 计算中心点到menu item中心的距离
            float tmp = layoutRadius / 2f - cWidth / 2 - mPadding;

            // tmp cosa 即menu item中心点的横坐标
            left = layoutRadius / 2 + (int) Math.round(tmp * Math.cos(Math.toRadians(mStartAngle + corner)) - 1 / 2f * cWidth);
            // tmp sina 即menu item中心点的纵坐标
            top = layoutRadius / 2 + (int) Math.round(tmp * Math.sin(Math.toRadians(mStartAngle + corner)) - 1 / 2f * cWidth);

            child.layout(left, top, left + cWidth, top + cWidth);
        }

        // 找到中心的view 如果存在 设置onclick事件
        View cView = findViewById(R.id.id_circle_menu_item_center);
        if (cView != null) {
            cView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mOnMenuItemClickListener != null && !isAnimPlaying) {
                        mOnMenuItemClickListener.itemCenterClick(v);
                        startAnim(!isOpen, 300);
                    }
                }
            });
            // 设置center item位置
            int cl = layoutRadius / 2 - cView.getMeasuredWidth() / 2;
            int cr = cl + cView.getMeasuredWidth();
            cView.layout(cl, cl, cr, cr);
        }

    }

    /**
     * 记录上一次的 x y 坐标
     */
    private float mLastX;
    private float mLastY;

    /**
     * 自动滚动的runnable
     */
    private AutoFlingRunnable mFlingRunnable;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if (isAnimPlaying || !isOpen) {
            return super.dispatchTouchEvent(event);
        }

        float x = event.getX();
        float y = event.getY();

        // Log.e("TAG", "x = " + x + " , y = " + y);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mLastX = x;
                mLastY = y;
                mDownTime = System.currentTimeMillis();
                mTmpAngle = 0;

                // 如果当前已经在快速滚动
                if (isFling) {
                    // 移除快速滚动回调
                    removeCallbacks(mFlingRunnable);
                    isFling = false;
                    return true;
                }

                break;
            case MotionEvent.ACTION_MOVE:

                /**
                 * 获得开始角度
                 */
                float start = getAngle(mLastX, mLastY);
                /**
                 * 获得当前角度
                 */
                float end = getAngle(x, y);

                // Log.e("TAG", "start = " + start + " , end =" + end);
                // 如果1 4 象限 直接end-start 角度都是正值
                if (getQuadrant(x, y) == 1 || getQuadrant(x, y) == 4) {
                    mStartAngle += end - start;
                    mTmpAngle += end - start;
                } else
                // 2 3 象限角度是负值
                {
                    mStartAngle += start - end;
                    mTmpAngle += start - end;
                }

                // 重新布局
                requestLayout();

                mLastX = x;
                mLastY = y;

                break;
            case MotionEvent.ACTION_UP:

                // 还原item的press状态
                if (Math.abs(mTmpAngle) > NOCLICK_VALUE) {
                    for (int i = 0; i < getChildCount(); i++) {
                        getChildAt(i).setPressed(false);
                    }
                }
                // 计算每秒移动的角度
                float anglePerSecond = mTmpAngle * 1000 / (System.currentTimeMillis() - mDownTime);

                // Log.e("TAG", anglePrMillionSecond + " , mTmpAngel = " +
                // mTmpAngle);

                // 如果达到该值 认为是快速移动
                if (Math.abs(anglePerSecond) > mFlingableValue && !isFling) {
                    // post一个任务 去自动滚动
                    post(mFlingRunnable = new AutoFlingRunnable(anglePerSecond));

                    return true;
                }

                // 如果当前角度超过 NOCLICK_VALUE 屏蔽点击
                if (Math.abs(mTmpAngle) > NOCLICK_VALUE) {
                    return true;
                }

                break;

        }
        return super.dispatchTouchEvent(event);
    }

    // 返回false 触摸非item区域 控件不转动
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    /**
     * 根据触摸的位置 计算角度
     *
     * @param xTouch
     * @param yTouch
     * @return
     */
    private float getAngle(float xTouch, float yTouch) {
        double x = xTouch - (mRadius / 2d);
        double y = yTouch - (mRadius / 2d);
        return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
    }

    /**
     * 根据当前位置 计算象限
     *
     * @param x
     * @param y
     * @return
     */
    private int getQuadrant(float x, float y) {
        int tmpX = (int) (x - mRadius / 2);
        int tmpY = (int) (y - mRadius / 2);
        if (tmpX >= 0) {
            return tmpY >= 0 ? 4 : 1;
        } else {
            return tmpY >= 0 ? 3 : 2;
        }

    }

    /**
     * 设置图标和文本
     *
     * @param resIds
     */
    public void setMenuItemIconsAndTexts(int[] resIds, String[] textStrs) {
        mItemImgs = resIds;
        mItemTexts = textStrs;

        // 参数检查
        if (resIds == null && textStrs == null) {
            throw new IllegalArgumentException("图片和文本至少设置一项");
        }

        // 初始化 mMenuItemCount
        mMenuItemCount = resIds == null ? textStrs.length : resIds.length;

        if (resIds != null && textStrs != null) {
            mMenuItemCount = Math.min(resIds.length, textStrs.length);
        }

        addMenuItems();
    }

    /**
     * 设置layoutId
     *
     * @param mMenuItemLayoutId
     */
    public void setMenuItemLayoutId(int mMenuItemLayoutId) {
        this.mMenuItemLayoutId = mMenuItemLayoutId;
    }

    /**
     * 添加菜单项
     */
    private void addMenuItems() {
        LayoutInflater mInflater = LayoutInflater.from(getContext());

        /**
         * 根据用户设置的参数 初始化view
         */
        for (int i = 0; i < mMenuItemCount; i++) {
            final int j = i;
            View view = mInflater.inflate(mMenuItemLayoutId, this, false);
            ImageView iv = view.findViewById(R.id.id_circle_menu_item_image);
            TextView tv = view.findViewById(R.id.id_circle_menu_item_text);

            if (iv != null) {
                iv.setVisibility(View.VISIBLE);
//                iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                iv.setImageResource(mItemImgs[i]);
            }

//            if (mItemTexts != null) {
//                view.setBackgroundResource(mItemTexts[i]);
//            }

            if (tv != null && mItemTexts != null) {
                tv.setVisibility(View.VISIBLE);
                tv.setText(mItemTexts[i]);
            }

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {

                    if (mOnMenuItemClickListener != null && !isAnimPlaying && isOpen) {
                        AnimatorSet animatorSet = startAnim(false, 300);
                        if (animatorSet != null) {
                            animatorSet.addListener(new Animator.AnimatorListener() {

                                @Override
                                public void onAnimationStart(Animator arg0) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator arg0) {
                                }

                                @Override
                                public void onAnimationEnd(Animator arg0) {
                                    mOnMenuItemClickListener.itemClick(v, j);
                                }

                                @Override
                                public void onAnimationCancel(Animator arg0) {
                                }
                            });
                        }
                    }
                }
            });
            // 添加view到容器
            addView(view);
        }
    }

    public void setFlingableValue(int mFlingableValue) {
        this.mFlingableValue = mFlingableValue;
    }

    public void setPadding(float mPadding) {
        this.mPadding = mPadding;
    }

    /**
     * 获得默认该layout的尺寸
     */
    private int getDefaultWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return Math.min(outMetrics.widthPixels, outMetrics.heightPixels);
    }

    /**
     * 自动滚动的任务
     */
    private class AutoFlingRunnable implements Runnable {

        private float angelPerSecond;

        public AutoFlingRunnable(float velocity) {
            this.angelPerSecond = velocity;
        }

        public void run() {
            // 如果小于20 则停止
            if ((int) Math.abs(angelPerSecond) < 20) {
                isFling = false;
                return;
            }
            isFling = true;
            // 不断改变 mStartAngle 让其滚动， / 30 为了避免滚动太快
            mStartAngle += (angelPerSecond / 30);
            // 逐渐减小这个值
            angelPerSecond /= 1.0666F;
            postDelayed(this, 30);
            // 重新布局
            requestLayout();
        }
    }

    /*-------------------------------------------------------------------------------------*/

    public AnimatorSet startAnim(boolean openOrCloseAnim, long duration) {

        int layoutRadius = mRadius;
        int cWidth = (int) (layoutRadius * RADIO_DEFAULT_CHILD_DIMENSION);
        float angleDelay = 360 / (getChildCount() - 1);
        AnimatorSet animSet = null;

        if (openOrCloseAnim) {

            for (int i = 0; i < getChildCount(); i++) {
                final int j = i;
                final View child = getChildAt(i);

                if (child.getId() == R.id.id_circle_menu_item_center)
                    continue;

                if (child.getVisibility() == GONE) {
                    continue;
                }

                mStartAngle %= 360;
                float tmp = layoutRadius / 2f - cWidth / 2 - mPadding;
                double itemX = tmp * Math.cos(Math.toRadians(mStartAngle));
                double itemY = tmp * Math.sin(Math.toRadians(mStartAngle));

                ObjectAnimator animZoomInX1 = ObjectAnimator.ofFloat(getChildAt(i), "scaleX", 0, 1.0f);
                ObjectAnimator animZoomInY1 = ObjectAnimator.ofFloat(getChildAt(i), "scaleY", 0, 1.0f);

                ObjectAnimator transX = ObjectAnimator.ofFloat(getChildAt(i), "translationX", (float) -itemX, 0);
                ObjectAnimator transY = ObjectAnimator.ofFloat(getChildAt(i), "translationY", (float) -itemY, 0);

                ObjectAnimator animZoomOutX = ObjectAnimator.ofFloat(getChildAt(i), "scaleX", 1f, 0.8f);
                ObjectAnimator animZoomOutY = ObjectAnimator.ofFloat(getChildAt(i), "scaleY", 1f, 0.8f);

                ObjectAnimator animZoomInX2 = ObjectAnimator.ofFloat(getChildAt(i), "scaleX", 0.8f, 1f);
                ObjectAnimator animZoomInY2 = ObjectAnimator.ofFloat(getChildAt(i), "scaleY", 0.8f, 1f);

                animSet = new AnimatorSet();
                animSet.play(animZoomInX1).with(animZoomInY1);
                animSet.play(animZoomInY1).with(transX);
                animSet.play(transX).with(transY);
                animSet.play(animZoomOutX).after(animZoomInX1);
                animSet.play(animZoomOutX).with(animZoomOutY);
                animSet.play(animZoomInX2).after(animZoomOutX);
                animSet.play(animZoomInX2).with(animZoomInY2);
                animSet.setDuration(duration);
                animSet.addListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator arg0) {
                        if (j == 0 || j == 1)
                            isAnimPlaying = true;
                    }

                    @Override
                    public void onAnimationRepeat(Animator arg0) {
                    }

                    @Override
                    public void onAnimationEnd(Animator arg0) {
                        if (j == getChildCount() - 1)
                            isAnimPlaying = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator arg0) {
                    }
                });
                animSet.setStartDelay(i * 100);
                animSet.start();

                mStartAngle += angleDelay;
            }

        } else {
            for (int i = 0; i < getChildCount(); i++) {
                final int j = i;
                final View child = getChildAt(i);

                if (child.getId() == R.id.id_circle_menu_item_center)
                    continue;

                if (child.getVisibility() == GONE) {
                    continue;
                }

                mStartAngle %= 360;
                float tmp = layoutRadius / 2f - cWidth / 2 - mPadding;
                double itemX = tmp * Math.cos(Math.toRadians(mStartAngle));
                double itemY = tmp * Math.sin(Math.toRadians(mStartAngle));

                ObjectAnimator animZoomInX = ObjectAnimator.ofFloat(getChildAt(i), "scaleX", 1f, 1.2f);
                ObjectAnimator animZoomInY = ObjectAnimator.ofFloat(getChildAt(i), "scaleY", 1f, 1.2f);

                ObjectAnimator transX = ObjectAnimator.ofFloat(getChildAt(i), "translationX", 0, (float) -itemX);
                ObjectAnimator transY = ObjectAnimator.ofFloat(getChildAt(i), "translationY", 0, (float) -itemY);

                ObjectAnimator animZoomOutX = ObjectAnimator.ofFloat(getChildAt(i), "scaleX", 1.2f, 0);
                ObjectAnimator animZoomOutY = ObjectAnimator.ofFloat(getChildAt(i), "scaleY", 1.2f, 0);

                animSet = new AnimatorSet();
                animSet.play(animZoomInX).with(animZoomInY);
                animSet.play(animZoomOutX).after(animZoomInX);
                animSet.play(animZoomOutX).with(animZoomOutY);
                animSet.play(animZoomOutY).with(transX);
                animSet.play(transX).with(transY);
                animSet.setDuration(duration);
                animSet.addListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator arg0) {
                        if (j == 0 || j == 1)
                            isAnimPlaying = true;
                    }

                    @Override
                    public void onAnimationRepeat(Animator arg0) {
                    }

                    @Override
                    public void onAnimationEnd(Animator arg0) {
                        if (j == getChildCount() - 1)
                            isAnimPlaying = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator arg0) {
                    }
                });
                animSet.setStartDelay(i * 100);
                animSet.start();

                mStartAngle += angleDelay;
            }
        }
        isOpen = openOrCloseAnim;
        return animSet;
    }

}
