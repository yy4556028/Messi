package com.yuyang.messi.ui.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.yuyang.messi.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MindCameraView extends View {

    private static final String TAG = MindCameraView.class.getSimpleName();

    private int radiusMax;
    private int radiusMin;

    private List<MyDotView> dotViewList = new ArrayList<>();

    private Paint circlePaint = new Paint();
    private Paint tipPaint = new Paint();

    private Random random = new Random();

    private boolean canTouch = false;
    private boolean isTip = false;

    public MindCameraView(Context context) {
        super(context);
    }

    public MindCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MindCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        init();
    }

    private void init() {
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(ContextCompat.getColor(getContext(), R.color.white));

        tipPaint.setStyle(Paint.Style.FILL);
        tipPaint.setAntiAlias(true);
        tipPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        radiusMax = Math.min(width, height) / 12;
        radiusMin = Math.min(width, height) / 20;

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (canTouch && event.getAction() == MotionEvent.ACTION_UP) {
                    Point point = new Point((int) event.getX(), (int) event.getY());
                    int index = findCircle(point, 0, 0);
                    if (listener != null) {
                        listener.onClickIndex(index);
                    }
                }
                return true;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < dotViewList.size(); i++) {
            MyDotView circleView = dotViewList.get(i);
            if (dotViewList.size() - 1 == i && isTip) {
                canvas.drawCircle(circleView.getCenterPoint().x, circleView.getCenterPoint().y, circleView.getRadius(), tipPaint);
            } else {
                canvas.drawCircle(circleView.getCenterPoint().x, circleView.getCenterPoint().y, circleView.getRadius(), circlePaint);
            }
        }
    }

    public List<MyDotView> getDotViewList() {
        return dotViewList;
    }

    public void setCanTouch(boolean canTouch) {
        this.canTouch = canTouch;
    }

    public void tip() {
        if (isTip) return;
        isTip = true;
        invalidate();
    }

    public void addNewCircle() {
        isTip = false;
        MyDotView dotView;
        do {
            dotView = new MyDotView(new Point(random.nextInt(getWidth() - radiusMax * 2) + radiusMax, random.nextInt(getHeight() - radiusMax * 2) + radiusMax), random.nextInt(radiusMax - radiusMin) + radiusMin);
        } while (-1 != findCircle(dotView.getCenterPoint(), dotView.getRadius(), 10));
        dotViewList.add(dotView);
        invalidate();
    }

    private int findCircle(Point point, int radius, int space) {
        for (int i = 0; i < dotViewList.size(); i++) {
            MyDotView view = dotViewList.get(i);
            if ((point.x - view.getCenterPoint().x) * (point.x - view.getCenterPoint().x) +
                    (point.y - view.getCenterPoint().y) * (point.y - view.getCenterPoint().y) <
                    (radius + view.getRadius() + space) * (radius + view.getRadius() + space)) {
                Log.e(TAG, "point = " + point.x + " " + point.y + " " + radius);
                return i;
            }
        }
        return -1;
    }

    public interface OnMindClickListener {
        void onClickIndex(int index);
    }

    private OnMindClickListener listener;

    public void setOnMindClickListener(OnMindClickListener listener) {
        this.listener = listener;
    }

    private class MyDotView {
        private Point centerPoint;
        private int radius;

        public MyDotView(Point centerPoint, int radius) {
            this.centerPoint = centerPoint;
            this.radius = radius;
        }

        public Point getCenterPoint() {
            return centerPoint;
        }

        public void setCenterPoint(Point centerPoint) {
            this.centerPoint = centerPoint;
        }

        public int getRadius() {
            return radius;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }
    }
}
