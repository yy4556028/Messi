package com.yuyang.messi.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.messi.R;

public class MeterView extends View {

    private int colorArc = Color.parseColor("#FF0000");
    private int colorPointer = Color.parseColor("#000000");

    private Paint arcPaint;
    private Paint pointerPaint;
    private Paint.FontMetrics fontMetrics;
    private RectF arcRect;
    private int mRadius;

    private float sweepAngel = 180;
    private int mArcWidth = 8;

    private int totalPointGap = 12;
    private int longPerShort = 3;
    private int longPointerLength = 18;
    private int longPointerWidth = 1;
    private int shortPointerLength = 14;
    private int shortPointerWidth = 1;
    private int pointerTextSp = 12;
    private Drawable pointerDrawable;

    private float totalValue = 100;
    private float currentValue = 20;

    public MeterView(Context context) {
        this(context, null);
    }

    public MeterView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MeterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        calc();
    }

    private void init() {
        arcPaint = new Paint();
        arcPaint.setAntiAlias(true);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setColor(colorArc);

        pointerPaint = new Paint();
        pointerPaint.setAntiAlias(true);
        pointerPaint.setTextAlign(Paint.Align.CENTER);
        pointerPaint.setColor(colorPointer);

        pointerDrawable = getResources().getDrawable(R.drawable.arrow_left);
    }

    private void calc() {
        arcPaint.setStrokeWidth(mArcWidth);
        mRadius = (getMeasuredWidth() - getPaddingLeft() - getPaddingRight()) / 2;
        float mRealRadius = mRadius - mArcWidth / 2f;
        arcRect = new RectF(-mRealRadius, -mRealRadius, mRealRadius, mRealRadius);

        pointerPaint.setTextSize(PixelUtils.sp2px(pointerTextSp));
        fontMetrics = pointerPaint.getFontMetrics();
    }

    public Paint getArcPaint() {
        return arcPaint;
    }

    public void setColorArc(int colorArc) {
        this.colorArc = colorArc;
        invalidate();
    }

    public void setColorPointer(int colorPointer) {
        this.colorPointer = colorPointer;
        invalidate();
    }

    public void setSweepAngel(float sweepAngel) {
        this.sweepAngel = sweepAngel;
        invalidate();
    }

    public void setArcWidth(int arcWidth) {
        mArcWidth = arcWidth;
        calc();
        invalidate();
    }

    public void setTotalPointGap(int totalPointGap) {
        this.totalPointGap = totalPointGap;
        invalidate();
    }

    public void setLongPerShort(int longPerShort) {
        this.longPerShort = longPerShort;
        invalidate();
    }

    public void setLongPointerLength(int longPointerLength) {
        this.longPointerLength = longPointerLength;
        invalidate();
    }

    public void setLongPointerWidth(int longPointerWidth) {
        this.longPointerWidth = longPointerWidth;
        invalidate();
    }

    public void setShortPointerLength(int shortPointerLength) {
        this.shortPointerLength = shortPointerLength;
        invalidate();
    }

    public void setShortPointerWidth(int shortPointerWidth) {
        this.shortPointerWidth = shortPointerWidth;
        invalidate();
    }

    public void setPointerTextSp(int pointerTextSp) {
        this.pointerTextSp = pointerTextSp;
        calc();
        invalidate();
    }

    public void setPointerDrawable(Drawable pointerDrawable) {
        this.pointerDrawable = pointerDrawable;
        invalidate();
    }

    public void setTotalValue(float totalValue) {
        this.totalValue = totalValue;
        invalidate();
    }

    public void setCurrentValue(float currentValue, boolean smooth) {
        if (smooth) {
            ValueAnimator animator = ValueAnimator.ofFloat(this.currentValue, currentValue);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    MeterView.this.currentValue = (float) animation.getAnimatedValue();
                    invalidate();
                    if (mValueSetListener != null) {
                        mValueSetListener.onValueSet(MeterView.this.currentValue);
                    }
                }
            });
            animator.setInterpolator(new DecelerateInterpolator());
            animator.start();
        } else {
            this.currentValue = currentValue;
            invalidate();
            if (mValueSetListener != null) {
                mValueSetListener.onValueSet(MeterView.this.currentValue);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArc(canvas);
        drawPointerLine(canvas);
        drawPointer(canvas);
    }

    private void drawArc(Canvas canvas) {
        float startAngel = 270 - sweepAngel / 2;
        canvas.translate(getPaddingLeft() + mRadius, getPaddingTop() + mRadius);
        canvas.drawArc(arcRect, startAngel, sweepAngel, false, arcPaint);
    }

    private void drawPointerLine(Canvas canvas) {
        canvas.save();
        float startAngel = 270 - sweepAngel / 2;
        canvas.rotate(startAngel + 90);
        for (int i = 0; i <= totalPointGap; i++) {

            if (i % longPerShort == 0) {     //长pointer
                pointerPaint.setStrokeWidth(longPointerWidth);
                canvas.drawLine(0, -mRadius + mArcWidth, 0, -mRadius + mArcWidth + longPointerLength, pointerPaint);
                drawPointerText(canvas, i);
            } else {    //短pointer
                pointerPaint.setStrokeWidth(shortPointerWidth);
                canvas.drawLine(0, -mRadius + mArcWidth, 0, -mRadius + mArcWidth + shortPointerLength, pointerPaint);
            }
            canvas.rotate(sweepAngel / totalPointGap);
        }
        canvas.restore();
    }

    private void drawPointerText(Canvas canvas, int i) {
        String text = String.valueOf((int) (totalValue / totalPointGap * i));
        int textBaseLine = (int) (0 + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom);
        canvas.drawText(text, 0, -mRadius + mArcWidth + longPointerLength + pointerPaint.getTextSize(), pointerPaint);
    }

    private void drawPointer(Canvas canvas) {
        float startAngel = 270 - sweepAngel / 2;
        canvas.rotate(startAngel + currentValue / totalValue * sweepAngel + 90);
        Bitmap bitmap = ((BitmapDrawable) pointerDrawable).getBitmap();
        canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2,
            -mRadius + mArcWidth + longPointerLength + pointerPaint.getTextSize() + PixelUtils.dp2px(4), new Paint());
    }

    public interface ValueSetListener {

        void onValueSet(float currentValue);
    }

    private ValueSetListener mValueSetListener;

    public void setValueSetListener(ValueSetListener valueSetListener) {
        mValueSetListener = valueSetListener;
    }
}
