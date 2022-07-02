package com.yuyang.messi.widget.autolinklibrary;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RoundBackgroundColorSpan extends ReplacementSpan {

    private int bgColor;
    private int textColor;
    private int radiusDp;

    public RoundBackgroundColorSpan(int bgColor, int textColor, int radiusDp) {
        super();
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.radiusDp = radiusDp;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        return ((int) paint.measureText(text, start, end));
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        int originalColor = paint.getColor();
        paint.setColor(this.bgColor);
        //画圆角矩形背景
        canvas.drawRoundRect(new RectF(
                        x,
                        top,
                        x + ((int) paint.measureText(text, start, end)),
                        bottom),

                radiusDp,
                radiusDp,
                paint);
        paint.setColor(this.textColor);
        //画文字,两边各增加8dp
        canvas.drawText(text, start, end, x, y, paint);
        //将paint复原
        paint.setColor(originalColor);
    }
}
