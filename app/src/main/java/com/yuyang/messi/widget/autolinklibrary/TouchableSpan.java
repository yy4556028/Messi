package com.yuyang.messi.widget.autolinklibrary;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;

/**
 * Created by Chatikyan on 26.09.2016-19:10.
 */

abstract class TouchableSpan extends ClickableSpan {

    private boolean isPressed;
    private int normalTextColor;
    private int pressedTextColor;
    private boolean isUnderLineEnabled;

    TouchableSpan(int normalTextColor, int pressedTextColor, boolean isUnderLineEnabled) {
        this.normalTextColor = normalTextColor;
        this.pressedTextColor = pressedTextColor;
        this.isUnderLineEnabled = isUnderLineEnabled;
    }

    void setPressed(boolean isSelected) {
        isPressed = isSelected;
    }

    @Override
    public void updateDrawState(TextPaint textPaint) {
        super.updateDrawState(textPaint);
        textPaint.setColor(isPressed ? pressedTextColor : normalTextColor);
        textPaint.bgColor = Color.TRANSPARENT;
        textPaint.setUnderlineText(isUnderLineEnabled);
    }
}