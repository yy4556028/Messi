package com.yuyang.lib_base.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;

public class RoundRectConstraintLayout extends ConstraintLayout {

    private float[] rect;
    private Path path;

    public RoundRectConstraintLayout(Context context) {
        super(context);
    }

    public RoundRectConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundRectConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setRound(float radius) {
        this.setRound(radius, radius, radius, radius);
    }

    public void setRound(float leftTop, float rightTop, float rightBottom, float leftBottom) {
        this.rect = new float[]{leftTop, leftTop, rightTop, rightTop, rightBottom, rightBottom, leftBottom, leftBottom};
    }

    //为了clip背景色
    @Override
    public void draw(Canvas canvas) {
        if (this.rect != null) {
            if (this.path == null) {
                int width = this.getWidth();
                int height = this.getHeight();
                this.path = new Path();
                RectF bound = new RectF(0.0F, 0.0F, (float) width, (float) height);
                this.path.addRoundRect(bound, this.rect, Path.Direction.CW);
            }

            canvas.clipPath(this.path);
        }
        super.draw(canvas);
    }

    protected void dispatchDraw(Canvas canvas) {
        if (this.rect != null) {
            if (this.path == null) {
                int width = this.getWidth();
                int height = this.getHeight();
                this.path = new Path();
                RectF bound = new RectF(0.0F, 0.0F, (float) width, (float) height);
                this.path.addRoundRect(bound, this.rect, Path.Direction.CW);
            }

            canvas.clipPath(this.path);
        }

        super.dispatchDraw(canvas);
    }
}
