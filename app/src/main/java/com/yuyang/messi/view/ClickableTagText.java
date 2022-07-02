package com.yuyang.messi.view;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by yy on 2015/11/23.
 */
public class ClickableTagText extends ClickableSpan {
    private final int mColor;
    OnClickLister lister;
    private boolean underLine = false;

    public ClickableTagText(int color) {
        mColor = color;
    }

    public ClickableTagText(int color, boolean underLine) {
        mColor = color;
        this.underLine = underLine;
    }

    @Override
    public void onClick(View widget) {
        lister.OnClick(widget);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(mColor);
        ds.setUnderlineText(underLine);
    }

    public void setOnClickLister(OnClickLister lister) {
        this.lister = lister;
    }

    public interface OnClickLister {
        public void OnClick(View widget);
    }
}
