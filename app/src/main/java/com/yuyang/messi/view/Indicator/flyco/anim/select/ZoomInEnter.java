package com.yuyang.messi.view.Indicator.flyco.anim.select;

import android.animation.ObjectAnimator;
import android.view.View;

import com.yuyang.messi.view.Indicator.flyco.anim.base.IndicatorBaseAnimator;

public class ZoomInEnter extends IndicatorBaseAnimator {
    public ZoomInEnter() {
        this.duration = 200;
    }

    public void setAnimation(View view) {
        this.animatorSet.playTogether(ObjectAnimator.ofFloat(view, "scaleX", 1.0F, 1.5F),
                ObjectAnimator.ofFloat(view, "scaleY", 1.0F, 1.5F));
    }
}
