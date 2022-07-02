package com.yuyang.lib_base.ui.view.Mask;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;

public class MaskUtil {

    /**
     * 生成 maskBitmap
     */
    public static Bitmap createMaskBitmap(Bitmap originBitmap, Drawable maskDrawable) {

        Bitmap resultBitmap = Bitmap.createBitmap(originBitmap.getWidth(), originBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas();
        canvas.setBitmap(resultBitmap);

        maskDrawable.setBounds(0, 0, originBitmap.getWidth(), originBitmap.getHeight());
        maskDrawable.draw(canvas);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(originBitmap, 0, 0, paint);
        return resultBitmap;
    }
}
