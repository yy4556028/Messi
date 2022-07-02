package com.yuyang.messi.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

public class ActSwitchAnimTool {

    public final static int ANIM_DURATION = 700;
    public final static int MODE_UNINIT = -1;
    public final static int MODE_SPREAD = 0;//展开模式
    public final static int MODE_SHRINK = 1;//收缩模式

    private final String TRANSFORMER_COLOR_END = "ACT_SWITCH_ANIM_COLOR_END";
    private final String TRANSFORMER_TARGET_LOCATION = "ACT_SWITCH_ANIM_TARGET_LOCATION";

    private ViewGroup mDecorView;
    private SwitchAnimView mSwitchAnimView;
    private Activity mStartAct;
    private int mColorStart;
    private int mColorEnd;

    private int targetViewWidth;
    private int targetViewHeight;
    private int[] targetLocation = new int[2];

    private boolean isNeedShrinkBack;
    private boolean isWaitingResume;

    public ActSwitchAnimTool(Activity activity) {
        mStartAct = activity;
        mSwitchAnimView = new SwitchAnimView(mStartAct);
        mDecorView = (ViewGroup) mStartAct.getWindow().getDecorView();
    }

    public ActSwitchAnimTool startActivity(final Intent intent, final boolean isNeedFinish) {
        intent.putExtra(TRANSFORMER_TARGET_LOCATION, targetLocation);
        intent.putExtra(TRANSFORMER_COLOR_END, mColorEnd);
        mSwitchAnimView.setSwitchAnimCallback(new SwitchAnimCallback() {
            @Override
            public void onAnimationStart() {
                switch (mSwitchAnimView.getmAnimType()) {
                    case MODE_SPREAD:
                        mSwitchAnimView.setClickable(true);
                        break;

                    case MODE_SHRINK:
                        mSwitchAnimView.setClickable(false);
                        break;
                }
            }

            @Override
            public void onAnimationEnd() {
                switch (mSwitchAnimView.getmAnimType()) {
                    case MODE_SPREAD:
                        mStartAct.startActivity(intent);
                        if (isNeedFinish) {
                            mStartAct.finish();
                        } else {
                            mDecorView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //if you need turn back by shrinking~ you need add~
                                    if (!isNeedShrinkBack) {
                                        mSwitchAnimView.resetAnimParam();
                                        mDecorView.removeView(mSwitchAnimView);
                                    } else {
                                        isWaitingResume = true;
                                    }
                                }
                            }, 200);
                        }
                        break;

                    case MODE_SHRINK:
                        mDecorView.post(new Runnable() {
                            @Override
                            public void run() {
                                mDecorView.removeView(mSwitchAnimView);
                            }
                        });
                        break;
                }

            }

            @Override
            public void onAnimationUpdate(int progress) {
                /* Maybe you want change the alpha of Target-View ~~.
                if (mTargetView != null){
                    mTargetView.setAlpha((float) (1 - progress / 100.0f));
                }
                */
            }
        });
        return this;
    }

    public ActSwitchAnimTool receiveIntent(Intent intent) {
        targetLocation = intent.getIntArrayExtra(TRANSFORMER_TARGET_LOCATION);
        mColorEnd = intent.getIntExtra(TRANSFORMER_COLOR_END, Color.BLUE);
        setmColorEnd(mColorEnd);
        return this;
    }

    public ActSwitchAnimTool target(final View view) {
        final ViewGroup.LayoutParams bgParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //background image.
        mDecorView.addView(mSwitchAnimView, bgParams);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getLocationInWindow(targetLocation);
                targetViewWidth = view.getWidth();
                targetViewHeight = view.getHeight();
                int circleRadius = (targetViewHeight > targetViewWidth ? targetViewWidth : targetViewHeight) / 2;
                mSwitchAnimView.setmTargetCircleRadius(circleRadius);
                mSwitchAnimView.setCenter(targetLocation[0] + targetViewWidth / 2, targetLocation[1] + targetViewHeight / 2);
            }
        });
        return this;
    }

    public ActSwitchAnimTool setCustomEndCallBack(SwitchAnimCallback callback) {
        mSwitchAnimView.setSwitchAnimCallback(callback);
        return this;
    }

    public ActSwitchAnimTool addContainerView(final View view, final SwitchAnimCallback callback) {
        mSwitchAnimView.setSwitchAnimCallback(new SwitchAnimCallback() {
            @Override
            public void onAnimationStart() {
                switch (mSwitchAnimView.getmAnimType()) {
                    case MODE_SPREAD:
                        mSwitchAnimView.setClickable(true);
                        break;

                    case MODE_SHRINK:
                        mSwitchAnimView.setClickable(false);
                        break;
                }
            }

            @Override
            public void onAnimationEnd() {
                if (view.getParent() != null)
                    return;
                final ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                switch (mSwitchAnimView.getmAnimType()) {
                    case MODE_SPREAD:
                        mDecorView.addView(view);
                        callback.onAnimationEnd();
                        break;

                    case MODE_SHRINK:
                        mSwitchAnimView.setSwitchAnimCallback(null);
                        break;
                }
            }

            @Override
            public void onAnimationUpdate(int progress) {
                callback.onAnimationUpdate(progress);
            }
        });
        return this;
    }

    public ActSwitchAnimTool removeContainerView(View view) {
        if (mDecorView != null && view != null) {
            mDecorView.removeView(view);
        }
        return this;
    }

    public void build() {
        if (mSwitchAnimView.getmAnimType() == MODE_SPREAD) {
            mSwitchAnimView.startSpreadAnim();
        } else if (mSwitchAnimView.getmAnimType() == MODE_SHRINK) {
            mSwitchAnimView.startShrinkAnim();
        }
    }

    public ActSwitchAnimTool setAnimType(int type) {
        mSwitchAnimView.setmAnimType(type);
        return this;
    }

    public ActSwitchAnimTool setmColorStart(int color) {
        mColorStart = color;
        mSwitchAnimView.setmSpreadColor(color);
        return this;
    }

    public ActSwitchAnimTool setmColorEnd(int color) {
        mColorEnd = color;
        mSwitchAnimView.setmShrinkColor(color);
        return this;
    }

    public ActSwitchAnimTool setShrinkBack(boolean isShrinkBack) {
        isNeedShrinkBack = isShrinkBack;
        return this;
    }

    public boolean isShrinkBack() {
        return isNeedShrinkBack;
    }

    public boolean isWaitingResume() {
        return isWaitingResume;
    }

    public ActSwitchAnimTool setIsWaitingResume(boolean isWaitingResume) {
        this.isWaitingResume = isWaitingResume;
        return this;
    }

    public int getTargetViewWidth() {
        return targetViewWidth;
    }

    public void setTargetViewWidth(int targetViewWidth) {
        this.targetViewWidth = targetViewWidth;
    }

    public int getTargetViewHeight() {
        return targetViewHeight;
    }

    public void setTargetViewHeight(int targetViewHeight) {
        this.targetViewHeight = targetViewHeight;
    }

    public interface SwitchAnimCallback {
        void onAnimationStart();

        void onAnimationEnd();

        void onAnimationUpdate(int progress);
    }

    public class SwitchAnimView extends View {

        private int mCenterX;
        private int mCenterY;
        private int mSpreadColor;
        private int mShrinkColor;
        private int mAnimType;
        private int mRadius;
        private int mDuration;
        private Paint mSpreadPaint;
        private Paint mShrinkPaint;
        private ValueAnimator mAnimator;
        private int mTargetCircleRadius;
        private int mScreenLength;


        private boolean isAnimationReady = false;

        private SwitchAnimCallback mSwitchAnimCallback;

        public void setSwitchAnimCallback(SwitchAnimCallback callback) {
            mSwitchAnimCallback = callback;
        }

        public SwitchAnimView(Context context) {
            this(context, null);
        }

        public SwitchAnimView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public SwitchAnimView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
            initAnimation();
        }

        private void init() {
            mAnimType = MODE_UNINIT;
            mSpreadPaint = new Paint();
            mSpreadPaint.setStrokeWidth(1);
            mSpreadPaint.setStyle(Paint.Style.STROKE);
            mSpreadPaint.setColor(Color.BLUE);
            mSpreadPaint.setAntiAlias(true);
            mShrinkPaint = new Paint();
            mShrinkPaint.setStrokeWidth(1);
            mShrinkPaint.setStyle(Paint.Style.STROKE);
            mShrinkPaint.setColor(Color.BLUE);
            mShrinkPaint.setAntiAlias(true);
            mDuration = ANIM_DURATION;
            mScreenLength = (int) Math.sqrt(Math.pow(getResources().getDisplayMetrics().heightPixels, 2)
                    + Math.pow(getResources().getDisplayMetrics().widthPixels, 2));
        }

        public void resetAnimParam() {
            mAnimType = -1;
            mRadius = 0;
            mSpreadPaint.setStrokeWidth(mRadius);
            mShrinkPaint.setStrokeWidth(mRadius);
            invalidate();
        }

        private void initAnimation() {
            isAnimationReady = true;
            mAnimator = ValueAnimator.ofInt(0, mScreenLength);
            mAnimator.setDuration(mDuration);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                int lastFactor = 0;

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int factor = (int) animation.getAnimatedValue();
                    mRadius = factor;
                    invalidate();
                    if (mSwitchAnimCallback != null && lastFactor != factor) {
                        mSwitchAnimCallback.onAnimationUpdate(factor * 100 / mScreenLength);
                        lastFactor = factor;
                    }
                }
            });
            mAnimator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    if (mSwitchAnimCallback != null) {
                        mSwitchAnimCallback.onAnimationStart();
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mSwitchAnimCallback != null) {
                        mSwitchAnimCallback.onAnimationEnd();
                    }
                }
            });
        }

        private void startSpreadAnim() {
            mAnimator.start();
        }

        private void startShrinkAnim() {
            mAnimator.reverse();
        }

        public void setCenter(int centerX, int centerY) {
            mCenterX = centerX;
            mCenterY = centerY;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (getmAnimType() == MODE_UNINIT) {
                return;
            }
            if (!isAnimationReady) {
                initAnimation();
            }
            switch (getmAnimType()) {
                case MODE_SPREAD:
                    mSpreadPaint.setStrokeWidth(mRadius);
                    canvas.drawCircle(mCenterX, mCenterY, mRadius / 2 + mTargetCircleRadius, mSpreadPaint);
                    break;
                case MODE_SHRINK:
                    mShrinkPaint.setStrokeWidth(mRadius);
                    canvas.drawCircle(mCenterX, mCenterY, mRadius / 2 + mTargetCircleRadius, mShrinkPaint);
                    break;
            }
        }

        private void setAlpha(int alpha) {
            mSpreadPaint.setAlpha(alpha);
            mShrinkPaint.setAlpha(alpha);
        }

        public int getmRadius() {
            return mRadius;
        }

        public void setmRadius(int mRadius) {
            this.mRadius = mRadius;
        }

        public int getmDuration() {
            return mDuration;
        }

        public void setmDuration(int mDuration) {
            this.mDuration = mDuration;
            isAnimationReady = false;
        }

        public int getmSpreadColor() {
            return mSpreadColor;
        }

        public void setmSpreadColor(int color) {
            mSpreadPaint.setColor(color);
            this.mSpreadColor = color;
        }

        public int getmShrinkColor() {
            return mShrinkColor;
        }

        public void setmShrinkColor(int color) {
            mShrinkPaint.setColor(color);
            this.mShrinkColor = color;
        }

        public int getmAnimType() {
            return mAnimType;
        }

        public void setmAnimType(int mAnimType) {
            this.mAnimType = mAnimType;
        }

        public int getmTargetCircleRadius() {
            return mTargetCircleRadius;
        }

        public void setmTargetCircleRadius(int mTargetCircleRadius) {
            this.mTargetCircleRadius = mTargetCircleRadius;
        }
    }
}
