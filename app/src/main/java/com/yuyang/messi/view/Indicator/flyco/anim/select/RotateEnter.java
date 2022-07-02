package com.yuyang.messi.view.Indicator.flyco.anim.select;

import android.animation.ObjectAnimator;
import android.view.View;

import com.yuyang.messi.view.Indicator.flyco.anim.base.IndicatorBaseAnimator;

public class RotateEnter extends IndicatorBaseAnimator {
    public RotateEnter() {
        this.duration = 250;
    }

    public void setAnimation(View view) {
        this.animatorSet.playTogether(ObjectAnimator.ofFloat(view, "rotation", 0, 180));
    }
}
