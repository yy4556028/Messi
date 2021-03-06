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
     * ????????????????????????
     *
     * @param mFarthestDistance px
     */
    public void setFarthestDistance(float mFarthestDistance) {
        this.mFarthestDistance = mFarthestDistance;
    }

    /**
     * ??????????????????
     *
     * @param mPathColor
     */
    public void setmPathColor(int mPathColor) {
        this.mPathColor = mPathColor;
    }

    /**
     * ????????????????????????????????????????????????????????????
     *
     * @param dragCanterPoint
     */
    @Override
    public void out2InRangeUp(PointF dragCanterPoint) {
        removeView();
        originView.setVisibility(View.VISIBLE);
    }

    /**
     * ????????????????????????????????????????????????????????????
     *
     * @param dragCanterPoint
     */
    @Override
    public void outRangeUp(PointF dragCanterPoint) {
        removeView();
        playAnim(dragCanterPoint);
    }

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @param dragCanterPoint
     */
    @Override
    public void inRangeUp(PointF dragCanterPoint) {
        removeView();
        originView.setVisibility(View.VISIBLE);
    }

    /**
     * ??????????????????(?????????)?????????????????????????????????
     *
     * @param dragCanterPoint
     */
    private void playAnim(PointF dragCanterPoint) {
        final ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(R.drawable.view_sticky_out_anim);
        final AnimationDrawable mAnimDrawable = (AnimationDrawable) imageView.getDrawable();

        //???????????????????????????????????????????????????????????????????????????
        int intrinsicWidth = imageView.getDrawable().getIntrinsicWidth();
        int intrinsicHeight = imageView.getDrawable().getIntrinsicHeight();

        mParams.x = (int) dragCanterPoint.x - intrinsicWidth / 2;
        mParams.y = (int) dragCanterPoint.y - intrinsicHeight / 2 - mStatusBarHeight;
        windowManager.addView(imageView, mParams);

        //???????????????????????????????????????
        long duration = getAnimDuration(mAnimDrawable);

        mAnimDrawable.start();
        //????????????????????????????????????????????????????????????

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
     * ??????????????????????????????
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
