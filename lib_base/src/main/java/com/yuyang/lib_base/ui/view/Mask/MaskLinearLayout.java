package com.yuyang.lib_base.ui.view.Mask;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.yuyang.lib_base.R;


public class MaskLinearLayout extends LinearLayout {

    private Drawable maskDrawable = null;
    private final Paint mPaint;

    public MaskLinearLayout(Context context) {
        this(context, null);
    }

    public MaskLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MaskLinearLayout);
        int resourceId = a.getResourceId(R.styleable.MaskLinearLayout_maskDrawable, 0);
        if (resourceId != 0) {
            setMaskImage(resourceId);
        }
        a.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setFilterBitmap(false);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    }

    public void setMaskImage(int resId) {
        maskDrawable = ContextCompat.getDrawable(getContext(), resId);
        postInvalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (maskDrawable == null) {
            super.dispatchDraw(canvas);
        } else {
            //将绘制操作保存到新的图层，因为图像合成是很昂贵的操作，将用到硬件加速，这里将图像合成的处理放到离屏缓存中进行
            int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
            super.dispatchDraw(canvas);
            canvas.drawBitmap(convertDrawable2Bitmap(maskDrawable), 0, 0, mPaint);
            //还原画布
            canvas.restoreToCount(saveCount);
        }
    }

    private Bitmap convertDrawable2Bitmap(Drawable drawable) {
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), config);

        drawable.setBounds(0, 0, getWidth(), getHeight());
        drawable.draw(new Canvas(bitmap));
        return bitmap;
    }
}
