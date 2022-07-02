package com.yuyang.messi.view.PageTransFormer;

import androidx.viewpager.widget.ViewPager;
import android.view.View;

public class AlphaPageTransformer implements ViewPager.PageTransformer {

    private static final float DEFAULT_MIN_ALPHA = 0.5f;
    private float mMinAlpha = DEFAULT_MIN_ALPHA;

    @Override
    public void transformPage(View view, float position) {

        if (position < -1) {
            // This page is way off-screen to the left.
            view.setAlpha(mMinAlpha);
        } else if (position <= 0) { // [-1,0]
            // Use the default slide transition when moving to the left page
            float factor = mMinAlpha + (1 - mMinAlpha) * (1 + position);
            view.setAlpha(factor);
        } else if (position <= 1) {//[1，0]
            float factor = mMinAlpha + (1 - mMinAlpha) * (1 - position);
            view.setAlpha(factor);
        } else { // (1,+Infinity]
            view.setAlpha(mMinAlpha);
        }
    }

}
