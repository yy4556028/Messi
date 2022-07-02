package com.yuyang.messi.view;

import android.content.Context;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * 使用setMovementMethod才能使TextView里面的元素自动拥有点击功能，支持ClickSpan。
 * 但是加上这个方法会造成ListView的每个项无的文本会占用ListView的ItemClick,解决这个办法需要重写一个setMovementMethod方法
 * {@link MyLinkMovementMethod}
 */
public class TextViewFixTouchConsume extends TextView {

    private boolean linkHit;

    public TextViewFixTouchConsume(Context context) {
        super(context);
    }

    public TextViewFixTouchConsume(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewFixTouchConsume(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean touchResult = getMovementMethod().onTouchEvent(this,
                        (Spannable) getText(), event);
        return touchResult;
    }

    @Override
    public boolean hasFocusable() {
        return false;
    }
}
