package com.yuyang.messi.view;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 单行滚动 TextView
 */

public class AlwaysMarqueeTextView extends TextView {
	// android:ellipsize="marquee"
	// android:marqueeRepeatLimit="marquee_forever"
	// android:singleLine="true"
	public AlwaysMarqueeTextView(Context context) {
		super(context);
		init();
	}

	public AlwaysMarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public AlwaysMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		this.setSingleLine();
		this.setMarqueeRepeatLimit(-1);
		this.setEllipsize(TruncateAt.MARQUEE);
	}

	@Override
	public boolean isFocused() {
		return true;
	}

//	@Override
//	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
//		//        super.onFocusChanged(focused, direction, previouslyFocusedRect);??
//	}
}
