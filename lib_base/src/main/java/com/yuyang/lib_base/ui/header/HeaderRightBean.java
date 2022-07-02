package com.yuyang.lib_base.ui.header;

import android.view.View;

import com.yuyang.lib_base.utils.PixelUtils;


public class HeaderRightBean {

    private int imageRes;
    private int imagePadding = PixelUtils.dp2px(12);//默认12
    private String text;
    private View.OnClickListener clickListener;

    public HeaderRightBean(String text, int imageRes) {
        this.imageRes = imageRes;
        this.text = text;
    }

    public HeaderRightBean(int imageRes, View.OnClickListener clickListener) {
        this.imageRes = imageRes;
        this.clickListener = clickListener;
    }

    public HeaderRightBean(int imageRes, int paddingPx, View.OnClickListener clickListener) {
        this.imageRes = imageRes;
        this.imagePadding = paddingPx;
        this.clickListener = clickListener;
    }

    public HeaderRightBean(String text, View.OnClickListener clickListener) {
        this.text = text;
        this.clickListener = clickListener;
    }

    public int getImageRes() {
        return imageRes;
    }

    public int getImagePadding() {
        return imagePadding;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public View.OnClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
