package com.yuyang.messi.view.PageTransFormer;

import androidx.viewpager.widget.ViewPager.PageTransformer;
import android.view.View;

/**
 * liteplayer by loader
 * @author qibin
 */
public class RotateDownPageTransformer implements PageTransformer {

	private static final float DEFAULT_MAX_ROTATE = 15.0f;
	private float mMaxRotate = DEFAULT_MAX_ROTATE;

	@Override
	public void transformPage(View view, float position) {
		if(position < -1) { // [-Infinity,-1) 左边看不见了
			view.setRotation(mMaxRotate * -1);
			view.setPivotX(view.getWidth());
			view.setPivotY(view.getHeight());
		}else if(position <= 0) { // [-1,0]左边向中间 或 中间向左边
			view.setPivotX(view.getWidth() * (0.5f + 0.5f * (-position)));
			view.setPivotY(view.getHeight());
			view.setRotation(mMaxRotate * position);
		}else if(position <= 1) { // (0,1] 右边向中间 或 中间向右边
			view.setPivotX(view.getWidth() * 0.5f * (1 - position));
			view.setPivotY(view.getHeight());
			view.setRotation(mMaxRotate * position);
		}else if(position > 1) { // (1,+Infinity] 右边看不见了
			view.setRotation(mMaxRotate);
			view.setPivotX(view.getWidth() * 0);
			view.setPivotY(view.getHeight());
		}
	}
}
