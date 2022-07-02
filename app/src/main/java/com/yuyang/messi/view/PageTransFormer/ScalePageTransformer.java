package com.yuyang.messi.view.PageTransFormer;

import androidx.viewpager.widget.ViewPager.PageTransformer;
import android.view.View;

/**
 * @author yuy
 */
public class ScalePageTransformer implements PageTransformer {

	private float minScale = 0.7f;

	public ScalePageTransformer(float minScale) {
		if (minScale < 0 || minScale > 1){
			minScale = 0.7f;
		}
		this.minScale = minScale;
	}

	@Override
	public void transformPage(View view, float position) {
		if (position < -1 || position > 1) {
			view.setScaleX(minScale);
			view.setScaleY(minScale);
		} else if (position <= 1) { // [-1,1]
			if (position < 0) {
				float scale = 1 + (1 - minScale) * position;
				view.setScaleX(scale);
				view.setScaleY(scale);
			} else {
				float scale = 1 - (1 - minScale) * position;
				view.setScaleX(scale);
				view.setScaleY(scale);
			}
		}
	}
}
