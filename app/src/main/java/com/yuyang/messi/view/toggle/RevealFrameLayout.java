package com.yuyang.messi.view.toggle;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class RevealFrameLayout extends FrameLayout {

    protected boolean firstIsTop;
    protected boolean mIsFirstInit = true;
    private float mCenterX;
    private float mCenterY;
    private float mRevealRadius = 0;
    private Path mPath = new Path();
    private View mFirstChild;//默认下面
    private View mSecondChild;//默认上面

    public RevealFrameLayout(Context paramContext) {
        super(paramContext);
    }

    public RevealFrameLayout(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public RevealFrameLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mFirstChild = null;
        mSecondChild = null;
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i).getVisibility() == VISIBLE) {
                if (mFirstChild == null) {
                    mFirstChild = getChildAt(i);
                } else if (mSecondChild == null) {
                    mSecondChild = getChildAt(i);
                    break;
                }
            }
        }
        switchTop(false, false);
    }

    private boolean isValidClick(float x, float y) {
        if (x >= 0 && x <= getWidth() && y >= 0 && y <= getHeight()) {
            return true;
        }
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
                return true;
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isValidClick(event.getX(), event.getY())) {
                    return false;
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (!isValidClick(event.getX(), event.getY())) {
                    return false;
                }
                mIsFirstInit = false;
                mCenterX = event.getX();
                mCenterY = event.getY();
                mRevealRadius = 0;
                if (mFirstChild != null && mSecondChild != null) {
                    if (listener != null) {
                        listener.onRevealStart(!firstIsTop);
                    }
                    switchTop(!firstIsTop, true);
                }
                return true;
        }
        return false;
    }

    protected void switchTop(final boolean firstIsTop, boolean needAnimate) {
        this.firstIsTop = firstIsTop;
        if (firstIsTop) {
            mFirstChild.setVisibility(View.VISIBLE);
            mSecondChild.setVisibility(View.VISIBLE);
            mFirstChild.bringToFront();
        } else {
            mFirstChild.setVisibility(View.VISIBLE);
            mSecondChild.setVisibility(View.VISIBLE);
            mSecondChild.bringToFront();
        }
        if (needAnimate) {
            ValueAnimator animator = ValueAnimator.ofFloat(0.0F, (float) Math.hypot(getMeasuredWidth(), getMeasuredHeight()));
            animator.setDuration(1000);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mRevealRadius = (Float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (listener != null) {
                        listener.onRevealEnd(firstIsTop);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator.start();
        }
    }

    private boolean drawBackground(View paramView) {
        if (mIsFirstInit) {
            return true;
        }
        if (firstIsTop && paramView == mSecondChild) {
            return true;
        } else if (!firstIsTop && paramView == mFirstChild) {
            return true;
        }
        return false;
    }

    protected boolean drawChild(Canvas canvas, View paramView, long paramLong) {
        if (drawBackground(paramView)) {
            return super.drawChild(canvas, paramView, paramLong);
        }
        int i = canvas.save();
        mPath.reset();
        mPath.addCircle(mCenterX, mCenterY, mRevealRadius, Path.Direction.CW);
        canvas.clipPath(this.mPath);
        boolean bool2 = super.drawChild(canvas, paramView, paramLong);
        canvas.restoreToCount(i);
        return bool2;
    }

    /************************* interface **************************/

    public interface OnRevealListener {
        void onRevealStart(boolean firstIsTop);
        void onRevealEnd(boolean firstIsTop);
    }

    private OnRevealListener listener;

    public void setOnRevealListener(OnRevealListener listener) {
        this.listener = listener;
    }
}
