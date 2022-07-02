package com.yuyang.messi.view.StickyView;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.core.view.MotionEventCompat;

import com.yuyang.lib_base.utils.statusbar.MyStatusBarUtil;
import com.yuyang.messi.R;

public class StickyViewHelper implements View.OnTouchListener, StickyView.StickViewListener {

    private WindowManager windowManager;
    private WindowManager.LayoutParams mParams;
    private StickyView mStickyView;
    private final Context mContext;
    private View originView;
    private int mStatusBarHeight;
    private float mFarthestDistance;
    private int mPathColor;

    public StickyViewHelper(Context mContext, View originView) {
        this.mContext = mContext;
        this.originView = originView;
        originView.setOnTouchListener(this);
        mStatusBarHeight = MyStatusBarUtil.getStatusBarHeight();
        windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.gravity = Gravity.TOP | Gravity.START;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_DOWN: {
                ViewParent parent = v.getParent();
                if (parent == null) {
                    return false;
                }
                parent.requestDisallowInterceptTouchEvent(true);

                mStickyView = new StickyView(originView);
                if (mFarthestDistance > 0) {
                    mStickyView.setFarthestDistance(mFarthestDistance);
                }
                if (mPathColor != 0) {
                    mStickyView.setPaintColor(mPathColor);
                }
                mStickyView.setStickViewListener(this);

                originView.setVisibility(View.INVISIBLE);

                mParams.x = 0;
                mParams.y = 0;
                windowManager.addView(mStickyView, mParams);
                break;
            }
        }
        mStickyView.onTouchEvent(event);
        return true;
    }

    /**
     * 设置最大拖拽范围
     *
     * @param mFarthestDistance px
     */
    public void setFarthestDistance(float mFarthestDistance) {
        this.mFarthestDistance = mFarthestDistance;
    }

    /**
     * 设置绘制颜色
     *
     * @param mPathColor
     */
    public void setmPathColor(int mPathColor) {
        this.mPathColor = mPathColor;
    }

    /**
     * 当移出了规定范围，最后在范围内松手的回调
     *
     * @param dragCanterPoint
     */
    @Override
    public void out2InRangeUp(PointF dragCanterPoint) {
        removeView();
        originView.setVisibility(View.VISIBLE);
    }

    /**
     * 当移出了规定范围，最后在范围外松手的回调
     *
     * @param dragCanterPoint
     */
    @Override
    public void outRangeUp(PointF dragCanterPoint) {
        removeView();
        playAnim(dragCanterPoint);
    }

    /**
     * 一直没有移动出范围，在范围内松手的回调
     *
     * @param dragCanterPoint
     */
    @Override
    public void inRangeUp(PointF dragCanterPoint) {
        removeView();
        originView.setVisibility(View.VISIBLE);
    }

    /**
     * 播放移除动画(帧动画)，这个过程根据个人喜好
     *
     * @param dragCanterPoint
     */
    private void playAnim(PointF dragCanterPoint) {
        final ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(R.drawable.view_sticky_out_anim);
        final AnimationDrawable mAnimDrawable = (AnimationDrawable) imageView.getDrawable();

        //这里得到的是其真实的大小，因为此时还得不到其测量值
        int intrinsicWidth = imageView.getDrawable().getIntrinsicWidth();
        int intrinsicHeight = imageView.getDrawable().getIntrinsicHeight();

        mParams.x = (int) dragCanterPoint.x - intrinsicWidth / 2;
        mParams.y = (int) dragCanterPoint.y - intrinsicHeight / 2 - mStatusBarHeight;
        windowManager.addView(imageView, mParams);

        //获取播放一次帧动画的总时长
        long duration = getAnimDuration(mAnimDrawable);

        mAnimDrawable.start();
        //由于帧动画不能定时停止，只能采用这种办法

        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAnimDrawable.stop();
                imageView.clearAnimation();
                windowManager.removeView(imageView);
                originView.setVisibility(View.VISIBLE);
            }
        }, duration);
    }

    /**
     * 得到帧动画的摧毁时间
     *
     * @param mAnimDrawable
     * @return
     */
    private long getAnimDuration(AnimationDrawable mAnimDrawable) {
        long duration = 0;
        for (int i = 0; i < mAnimDrawable.getNumberOfFrames(); i++) {
            duration += mAnimDrawable.getDuration(i);
        }
        return duration;
    }

    private void removeView() {
        if (windowManager != null) {
            windowManager.removeView(mStickyView);
            mStickyView = null;
        }
    }
}
