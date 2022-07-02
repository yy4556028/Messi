package com.yuyang.messi.view.Indicator.flyco.anim.base;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.view.View;
import android.view.animation.Interpolator;

public abstract class IndicatorBaseAnimator {
    protected long duration = 200;
    protected AnimatorSet animatorSet = new AnimatorSet();
    private Interpolator interpolator;
    private long delay;
    private AnimatorListener listener;

    public abstract void setAnimation(View view);

    protected void start(final View view) {
        reset(view);
        setAnimation(view);

        animatorSet.setDuration(duration);
        if (interpolator != null) {
            animatorSet.setInterpolator(interpolator);
        }

        if (delay > 0) {
            animatorSet.setStartDelay(delay);
        }

        if (listener != null) {
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    listener.onAnimationStart(animator);
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                    listener.onAnimationRepeat(animator);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    listener.onAnimationEnd(animator);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    listener.onAnimationCancel(animator);
                }
            });
        }

        animatorSet.setTarget(view);
        animatorSet.start();
    }

    public static void reset(View view) {
        view.setAlpha(1);
        view.setScaleX(1);
        view.setScaleY(1);
        view.setTranslationX(0);
        view.setTranslationY(0);
        view.setRotation(0);
        view.setRotationY(0);
        view.setRotationX(0);
    }

    public IndicatorBaseAnimator duration(long duration) {
        this.duration = duration;
        return this;
    }

    public IndicatorBaseAnimator delay(long delay) {
        this.delay = delay;
        return this;
    }

    public IndicatorBaseAnimator interpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    public IndicatorBaseAnimator listener(AnimatorListener listener) {
        this.listener = listener;
        return this;
    }

    public void playOn(View view) {
        start(view);
    }

    public interface AnimatorListener {
        void onAnimationStart(Animator animator);

        void onAnimationRepeat(Animator animator);

        void onAnimationEnd(Animator animator);

        void onAnimationCancel(Animator animator);
    }
}
