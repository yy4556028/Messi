package com.yuyang.lib_base.utils;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.RequiresApi;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class RevealUtil {

    private static final String KEY_REVEAL_X = "key_reveal_x";
    private static final String KEY_REVEAL_Y = "key_reveal_y";
    private static boolean isAnimIng;

    public static Intent wrapIntent(Intent intent, View anchor) {
        int[] xy = new int[2];
        anchor.getLocationOnScreen(xy);
        intent.putExtra(KEY_REVEAL_X, xy[0] + anchor.getMeasuredWidth() / 2);
        intent.putExtra(KEY_REVEAL_Y, xy[1] + anchor.getMeasuredHeight() / 2);
        return intent;
    }

    public static void onCreate(final Activity activity) {
        onCreate(activity, null);
    }

    public static void onCreate(final Activity activity, final OnRevealListener revealListener) {
        activity.getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                activity.getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                startRevealAnimator(activity, true, revealListener);
            }
        });
    }

    public static void finish(final Activity activity) {
        finish(activity, null);
    }

    public static void finish(final Activity activity, final OnRevealListener revealListener) {
        startRevealAnimator(activity, false, revealListener);
    }

    private static void startRevealAnimator(final Activity activity, final boolean isOpen, final OnRevealListener revealListener) {
        if (isAnimIng) return;

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float hypot = (float) Math.hypot(metrics.widthPixels, metrics.heightPixels);
        float startRadius = isOpen ? 0 : hypot;
        float endRadius = isOpen ? hypot : 0;

        int x = activity.getIntent().getIntExtra(KEY_REVEAL_X, 0);
        int y = activity.getIntent().getIntExtra(KEY_REVEAL_Y, 0);

        Animator animator = ViewAnimationUtils.createCircularReveal(
                activity.getWindow().getDecorView(), x, y,
                startRadius,
                endRadius);
        animator.setDuration(1000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimIng = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimIng = false;
                if (revealListener != null && revealListener.onRevealEnd()) {
                    //自己处理 返回true
                    return;
                }
                if (isOpen) {
                    //do nothing
                } else {
                    activity.getWindow().getDecorView().setVisibility(View.INVISIBLE);
                    activity.finishActivity(0);
                    activity.overridePendingTransition(0, 0);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    public interface OnRevealListener {
        boolean onRevealEnd();
    }
}
