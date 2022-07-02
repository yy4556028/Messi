package com.yamap.lib_chat.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.yamap.lib_chat.utils.CommonUtil;

public class ResizeImageView extends AppCompatImageView {

    private final int MaxWidth = (int) CommonUtil.dp2px(200);
    private final int MaxHeight = (int) CommonUtil.dp2px(200);

    public ResizeImageView(Context context) {
        super(context);
    }

    public ResizeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        resize();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        resize();
    }

    private void resize() {
        if (getDrawable() == null)
            return;

        int width = getDrawable().getIntrinsicWidth();
        int height = getDrawable().getIntrinsicHeight();
        float ratio = (float) width / height;

        if (ratio > ((float) MaxWidth / MaxHeight)) {

//            getLayoutParams().width = (width > MaxWidth ? MaxWidth : width);
            getLayoutParams().width = MaxWidth;
            getLayoutParams().height = (int) (MaxWidth / ratio);
        } else {

//            getLayoutParams().height = (height > MaxHeight ? MaxHeight : height);
            getLayoutParams().height = MaxHeight;
            getLayoutParams().width = (int) (MaxHeight * ratio);
        }
    }

}
