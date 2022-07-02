package com.yuyang.lib_xunfei;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class WaveformView extends View {
    private static final float MIN_AMPLITUDE = 0.0575f;
    private float mPrimaryWidth = 1.0f;
    private float mSecondaryWidth = 0.5f;
    private float amplitude = MIN_AMPLITUDE;
    private int mWaveColor = Color.BLACK;
    private int mDensity = 2;
    private int mWaveCount = 5;
    private float mFrequency = 0.1875f;
    private float mPhaseShift = -0.1875f;
    private float mPhase = mPhaseShift;

    private Paint mPrimaryPaint;
    private Paint mSecondaryPaint;

    private Path mPath;

    private float mLastX;
    private float mLastY;

    public WaveformView(Context context) {
        this(context, null);
    }

    public WaveformView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveformView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyConfig(context, attrs);
        initialize();
    }

    private void applyConfig(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WaveformView);
        mWaveColor = a.getColor(R.styleable.WaveformView_waveColor, Color.BLACK);
        a.recycle();
    }

    private void initialize() {
        mPrimaryPaint = new Paint();
        mPrimaryPaint.setStrokeWidth(mPrimaryWidth);
        mPrimaryPaint.setAntiAlias(true);
        mPrimaryPaint.setStyle(Paint.Style.STROKE);
        mPrimaryPaint.setColor(mWaveColor);

        mSecondaryPaint = new Paint();
        mSecondaryPaint.setStrokeWidth(mSecondaryWidth);
        mSecondaryPaint.setAntiAlias(true);
        mSecondaryPaint.setStyle(Paint.Style.STROKE);
        mSecondaryPaint.setColor(mWaveColor);

        mPath = new Path();
    }

    public void updateAmplitude(float amplitude) {
        ObjectAnimator.ofFloat(this, "amplitude", getAmplitude(), Math.max(amplitude, MIN_AMPLITUDE))
        .start();
    }

    public float getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        for (int i = 0; i < mWaveCount; ++i) {
            float midH = height / 2f;
            float midW = width / 2f;

            float maxAmplitude = midH / 2f - 4.0f;
            float progress = 1.0f - i * 1.0f / mWaveCount;
            float normalAmplitude = (1.5f * progress - 0.5f) * amplitude;

            float multiplier = (float) Math.min(1.0, (progress / 3.0f * 2.0f) + (1.0f / 3.0f));

            if (i != 0) {
                mSecondaryPaint.setAlpha((int) (multiplier * 255));
            }

            mPath.reset();
            for (int x = 0; x < width + mDensity; x += mDensity) {
                float scaling = 1f - (float) Math.pow(1 / midW * (x - midW), 2);
                float y = scaling * maxAmplitude * normalAmplitude * (float) Math.sin(
                        180 * x * mFrequency / (width * Math.PI) + mPhase) + midH;
                //canvas.drawPoint(x, y, l == 0 ? mPrimaryPaint : mSecondaryPaint);

                //canvas.drawLine(x, y, x, 2*midH - y, mSecondaryPaint);
                if (x == 0) {
                    mPath.moveTo(x, y);
                } else {
                    mPath.lineTo(x, y);
                    //final float x2 = (x + mLastX) / 2;
                    //final float y2 = (y + mLastY) / 2;
                    //mPath.quadTo(x2, y2, x, y);
                }

                mLastX = x;
                mLastY = y;
            }

            if (i == 0) {
                canvas.drawPath(mPath, mPrimaryPaint);
            } else {
                canvas.drawPath(mPath, mSecondaryPaint);
            }
        }

        mPhase += mPhaseShift;
        invalidate();
    }
}
