package com.yuyang.messi.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.yuyang.lib_base.utils.PixelUtils;

public class AutoScrollTextView extends AppCompatTextView {

    private Paint paint;
    private String textContent;//文本内容
    private float textX;//文字的横坐标
    private float textY;//文字的纵坐标
    private float textLength;//文本长度
    private float viewWidth;

    private boolean isScrolling = false;//是否开始滚动
    private int scrollSpeed = 1;
    private float textGap = PixelUtils.dp2px(8);

    public AutoScrollTextView(Context context) {
        this(context, null);
    }

    public AutoScrollTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoScrollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isScrolling)
                    stopScroll();
                else
                    startScroll();
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        init();
    }

    private void init() {
        paint = getPaint();
        viewWidth = getWidth();
        textX = viewWidth;
        textY = getTextSize() + getPaddingTop();
        textContent = getText().toString();
        textLength = paint.measureText(textContent);
    }

    public void startScroll() {
        isScrolling = true;
        invalidate();
    }

    public void stopScroll() {
        isScrolling = false;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawText(textContent, textX, textY, paint);
        float nextX = textX;
        while ((nextX += textLength + textGap) < viewWidth) {
            canvas.drawText(textContent, nextX, textY, paint);
        }

        if (!isScrolling) {
            return;
        }
        textX -= scrollSpeed;//文字滚动速度。
        if (textX < -textLength - textGap)
            textX += textLength + textGap;
        invalidate();
    }
}
