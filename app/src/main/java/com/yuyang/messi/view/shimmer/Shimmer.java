package com.yuyang.messi.view.shimmer;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.view.View;

/**
 * Shimmer
 * User: romainpiel
 * Date: 06/03/2014
 * Time: 15:42
 */
public class Shimmer {

    // 动画方向从左向右
    public static final int ANIMATION_DIRECTION_LTR = 0;

    // 动画方向从右向左
    public static final int ANIMATION_DIRECTION_RTL = 1;

    // 默认重复次数
    private static final int DEFAULT_REPEAT_COUNT = ValueAnimator.INFINITE;

    // 默认动画持续时间
    private static final long DEFAULT_DURATION = 1000;

    // 默认开始动画延迟
    private static final long DEFAULT_START_DELAY = 0;

    // 默认动画方向
    private static final int DEFAULT_DIRECTION = ANIMATION_DIRECTION_LTR;

    // 重复次数
    private int repeatCount;

    // 动画持续时间
    private long duration;

    // 开始动画延迟
    private long startDelay;

    // 默认动画方向
    private int direction;

    private Animator.AnimatorListener animatorListener;

    private ObjectAnimator animator;

    public Shimmer() {
        repeatCount = DEFAULT_REPEAT_COUNT;
        duration = DEFAULT_DURATION;
        startDelay = DEFAULT_START_DELAY;
        direction = DEFAULT_DIRECTION;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public Shimmer setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
        return this;
    }

    public long getDuration() {
        return duration;
    }

    public Shimmer setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public long getStartDelay() {
        return startDelay;
    }

    public Shimmer setStartDelay(long startDelay) {
        this.startDelay = startDelay;
        return this;
    }

    public int getDirection() {
        return direction;
    }

    public Shimmer setDirection(int direction) {

        if (direction != ANIMATION_DIRECTION_LTR && direction != ANIMATION_DIRECTION_RTL) {
            throw new IllegalArgumentException("The animation direction must be either ANIMATION_DIRECTION_LTR or ANIMATION_DIRECTION_RTL");
        }

        this.direction = direction;
        return this;
    }

    public Animator.AnimatorListener getAnimatorListener() {
        return animatorListener;
    }

    public Shimmer setAnimatorListener(Animator.AnimatorListener animatorListener) {
        this.animatorListener = animatorListener;
        return this;
    }

    public <V extends View & ShimmerViewBase> void start(final V shimmerView) {

        if (isAnimating()) {
            return;
        }

        final Runnable animate = new Runnable() {
            @Override
            public void run() {

                shimmerView.setShimmering(true);

                float fromX = 0;
                float toX = shimmerView.getWidth();
                if (direction == ANIMATION_DIRECTION_RTL) {
                    fromX = shimmerView.getWidth();
                    toX = 0;
                }

                animator = ObjectAnimator.ofFloat(shimmerView, "gradientX", fromX, toX);
                animator.setRepeatCount(repeatCount);
                animator.setDuration(duration);
                animator.setStartDelay(startDelay);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        shimmerView.setShimmering(false);

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            shimmerView.postInvalidate();
                        } else {
                            shimmerView.postInvalidateOnAnimation();
                        }

                        animator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                if (animatorListener != null) {
                    animator.addListener(animatorListener);
                }

                animator.start();
            }
        };

        if (!shimmerView.isSetUp()) {
            shimmerView.setAnimationSetupCallback(new ShimmerViewHelper.AnimationSetupCallback() {
                @Override
                public void onSetupAnimation(final View target) {
                    animate.run();
                }
            });
        } else {
            animate.run();
        }
    }

    public void cancel() {
        if (animator != null) {
            animator.cancel();
        }
    }

    public boolean isAnimating() {
        return animator != null && animator.isRunning();
    }
}
