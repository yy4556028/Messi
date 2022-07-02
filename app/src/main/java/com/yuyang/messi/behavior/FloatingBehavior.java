package com.yuyang.messi.behavior;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

public class FloatingBehavior extends CoordinatorLayout.Behavior<View> {

    private boolean hide;

    public FloatingBehavior() {
    }

    public FloatingBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*
     * @param child: 当前view
     * @param dependency: 需要依赖的View
     */
    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        if (dependency instanceof RecyclerView ||
                dependency instanceof ScrollView ||
                dependency instanceof NestedScrollView) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return true;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        if (dyConsumed > 0 && !hide) {
            hide = true;
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(ObjectAnimator.ofFloat(child, "scaleX", child.getScaleX(), 0),
                    ObjectAnimator.ofFloat(child, "scaleY", child.getScaleY(), 0));
            animatorSet.start();
        } else if (dyConsumed < 0 && hide) {
            hide = false;
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(ObjectAnimator.ofFloat(child, "scaleX", child.getScaleX(), 1),
                    ObjectAnimator.ofFloat(child, "scaleY", child.getScaleY(), 1));
            animatorSet.start();
        }
    }
}
