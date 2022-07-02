package com.yuyang.messi.view.Crop;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.yuyang.lib_base.myglide.GlideApp;
import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.messi.R;

import java.io.File;

/**
 * 创建者: yuyang
 * 创建日期: 2015-08-17
 * 创建时间: 15:00
 * CropImageLayout: ClipImageLayout
 *
 * @author yuyang
 * @version 1.0
 */
public class CropImageLayout extends RelativeLayout {


    private CropZoomImageView mZoomImageView;

    private CropImageBorderView mClipImageView;

    private int mHorizontalPadding;

    private int DEFAULT_HRZ_PADDING = 20;

    public CropImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mZoomImageView = new CropZoomImageView(context);
        mClipImageView = new CropImageBorderView(context);

        android.view.ViewGroup.LayoutParams lp = new LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);

        this.addView(mZoomImageView, lp);
        this.addView(mClipImageView, lp);

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CropImageLayout);

        mHorizontalPadding = PixelUtils.dp2px(ta.getDimension(R.styleable.CropImageLayout_horizontalPadding, DEFAULT_HRZ_PADDING));

        ta.recycle();

        mZoomImageView.setHorizontalPadding(mHorizontalPadding);
        mClipImageView.setHorizontalPadding(mHorizontalPadding);
    }

    public void setImageViewBitmap(Bitmap bitmap) {
        mZoomImageView.setImageBitmap(bitmap);
    }

    public void setImageViewPath(String path) {
        GlideApp.with(getContext())
                .load(new File(path))
                .dontAnimate()
                .dontTransform()
                .override(800, 800)
                .into(mZoomImageView);
    }

    /**
     * 对外公布设置边距的方法,单位为dp
     *
     * @param mHorizontalPadding
     */
    public void setHorizontalPadding(int mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;
        mZoomImageView.setHorizontalPadding(mHorizontalPadding);
        mClipImageView.setHorizontalPadding(mHorizontalPadding);
    }

    /**
     * 裁切图片
     *
     * @return
     */
    public Bitmap crop() {
        return mZoomImageView.clip();
    }

}
