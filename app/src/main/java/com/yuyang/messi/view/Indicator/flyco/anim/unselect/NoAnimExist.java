package com.yuyang.messi.view.Indicator.flyco.anim.unselect;

import android.animation.ObjectAnimator;
import android.view.View;

import com.yuyang.messi.view.Indicator.flyco.anim.base.IndicatorBaseAnimator;

public class NoAnimExist extends IndicatorBaseAnimator {
    public NoAnimExist() {
        this.duration = 200;
    }

    public void setAnimation(View view) {
        this.animatorSet.playTogether(ObjectAnimator.ofFloat(view, "alpha", 1, 1));
    }
}
