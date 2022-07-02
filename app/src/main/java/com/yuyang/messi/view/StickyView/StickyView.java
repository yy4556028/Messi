package com.yuyang.messi.view.StickyView;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.yuyang.lib_base.utils.statusbar.MyStatusBarUtil;
import com.yuyang.messi.utils.GeometryUtil;

public class StickyView extends View {

    /**
     * 拖拽圆到固定圆之间的距离
     */
    private float rangeMove;
    private View originView;
    private Bitmap originBitmap;
    /**
     * 拖拽圆的圆心
     */
    private PointF mDragCentrePoint = new PointF();
    /**
     * 固定圆的圆心
     */
    private PointF mFixCentrePoint = new PointF();
    /**
     * 控制点
     */
    private PointF middlePoint = new PointF();

    /**
     * 最大拖拽范围
     */
    private float mFarthestDistance;
    /**
     * 动画中固定圆的最小半径
     */
    private float mMinFixRadius;
    /**
     * 拖拽圆半径
     */
    private float mDragRadius;
    /**
     * 固定圆半径
     */
    private float mFixRadius;

    /**
     * 超出范围
     */
    private boolean isOut;
    /**
     * 在超出范围的地方松手
     */
    private boolean isOutUp;
    private Paint mPaint;
    private Path mPath;
    private int mStatusBarHeight;
    private StickViewListener stickViewListener;

    public StickyView(View originView) {
        super(originView.getContext());
        this.originView = originView;
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPath = new Path();
        // 需要手动测量
        originView.measure(0, 0);
        mDragRadius = Math.min(originView.getWidth() / 2, originView.getHeight() / 2);

        //计算小圆点在屏幕的坐标
        int[] points = new int[2];
        originView.getLocationInWindow(points);
        int x = points[0] + originView.getWidth() / 2;
        int y = points[1] + originView.getHeight() / 2;
        mFixCentrePoint.set(x, y);
        mDragCentrePoint.set(x, y);

        DisplayMetrics displayMetrics = originView.getResources().getDisplayMetrics();

        mFixRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, displayMetrics);
        mFarthestDistance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, displayMetrics);
        mMinFixRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, displayMetrics);

        //开启mDragItemView绘图缓存
        originView.setDrawingCacheEnabled(true);
        //获取mDragItemView在缓存中的Bitmap对象
        originBitmap = Bitmap.createBitmap(originView.getDrawingCache());
        //这一步很关键，释放绘图缓存，避免出现重复的镜像
        originView.destroyDrawingCache();

        mStatusBarHeight = MyStatusBarUtil.getStatusBarHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        //需要去除状态栏高度偏差
        canvas.translate(0, -mStatusBarHeight);
        if (!isOut) {//移出了范围后将不再绘制链接部分和固定圆
            //根据圆心距动态绘制固定圆大小
            float mFixRadius = updateStickRadius();
            canvas.drawCircle(mFixCentrePoint.x, mFixCentrePoint.y, mFixRadius, mPaint);
            //设置控制点，这里的控制点选择的是两圆心连接成的直线的中心位置
            middlePoint.set((mDragCentrePoint.x + mFixCentrePoint.x) / 2, (mDragCentrePoint.y + mFixCentrePoint.y) / 2);
            //接下来是计算两个圆的外切点，会用到一点几何知识，忘了的回去找高中老师
            float dy = mDragCentrePoint.y - mFixCentrePoint.y;
            float dx = mDragCentrePoint.x - mFixCentrePoint.x;

            PointF[] mDragTangentPoints;//拖拽圆的切点
            PointF[] mFixTangentPoints;//固定圆的切点
            if (dx != 0) {
                float k1 = dy / dx;
                float k2 = -1 / k1;
                mDragTangentPoints = GeometryUtil.getIntersectionPoints(mDragCentrePoint, mDragRadius, (double) k2);
                mFixTangentPoints = GeometryUtil.getIntersectionPoints(mFixCentrePoint, mFixRadius, (double) k2);
            } else {
                mDragTangentPoints = GeometryUtil.getIntersectionPoints(mDragCentrePoint, mDragRadius, (double) 0);
                mFixTangentPoints = GeometryUtil.getIntersectionPoints(mFixCentrePoint, mFixRadius, (double) 0);
            }
            //必须重设上一次的路径
            mPath.reset();
            //moveTo顾名思义就是移动到某个位置，这里移动到固定圆的第一个外切点
            mPath.moveTo(mFixTangentPoints[0].x, mFixTangentPoints[0].y);
            //quadTo是绘制二阶贝塞尔曲线，这种曲线很想ps里面画矢量路径的那种。二阶的话需要一个控制点，一个起点一个终点
            mPath.quadTo(
                    (mDragCentrePoint.x + mFixCentrePoint.x) / 2,
                    (mDragCentrePoint.y + mFixCentrePoint.y) / 2,
                    mDragTangentPoints[0].x,
                    mDragTangentPoints[0].y);
            //从上一个点绘制一条直线到下面这个位置
            mPath.lineTo(mDragTangentPoints[1].x, mDragTangentPoints[1].y);
            //再绘制一条二阶贝塞尔曲线
            mPath.quadTo((mDragCentrePoint.x + mFixCentrePoint.x) / 2,
                    (mDragCentrePoint.y + mFixCentrePoint.y) / 2,
                    mFixTangentPoints[1].x,
                    mFixTangentPoints[1].y);
            //执行close，表示形成闭合路径
            mPath.close();
            //绘制到界面上
            canvas.drawPath(mPath, mPaint);
        }
        //当在范围外松手的时候是不再绘制拖拽圆的
        if (!isOutUp) {
//            canvas.drawCircle(mDragCentrePoint.x, mDragCentrePoint.y, mDragRadius, mPaint);
            canvas.drawBitmap(originBitmap, mDragCentrePoint.x - originBitmap.getWidth() / 2, mDragCentrePoint.y - originBitmap.getHeight() / 2, null);
        }

        //参考范围，没实际作用
        /*mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mFixCentrePoint.x, mFixCentrePoint.y, mFarthestDistance, mPaint);
		mPaint.setStyle(Paint.Style.FILL);*/
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isOut = false;
                isOutUp = false;
                mDragCentrePoint.set(event.getRawX(), event.getRawY());
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                mDragCentrePoint.set(event.getRawX(), event.getRawY());
                invalidate();
                rangeMove = GeometryUtil.getDistanceBetween2Points(mFixCentrePoint, mDragCentrePoint);
//                当移出了规定的范围的时候
                if (rangeMove > mFarthestDistance) {
                    isOut = true;
                } else {
                    //不能把isOut改为false,因为移出一次后就算它移出过了
                    //isOut=false;
                }
                break;
            case MotionEvent.ACTION_UP:
                rangeMove = GeometryUtil.getDistanceBetween2Points(mFixCentrePoint, mDragCentrePoint);
                if (isOut) {
                    outUp();
                }
                // 没有超出，做动画
                else {
                    inUp();
                }
                invalidate();
                break;
        }
        return true;
    }

    /**
     * 移动的时候一直在范围内，最后在范围内松手
     */
    private void inUp() {
        final PointF startPoint = new PointF(mDragCentrePoint.x, mDragCentrePoint.y);
        final PointF endPoint = new PointF(mFixCentrePoint.x, mFixCentrePoint.y);
        ValueAnimator backAnimator = ValueAnimator.ofFloat(1.0f);
        backAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                PointF byPercent = GeometryUtil.getPointByPercent(startPoint, endPoint, fraction);
                mDragCentrePoint.set(byPercent.x, byPercent.y);
                invalidate();
            }
        });
        backAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (stickViewListener != null) {
                    stickViewListener.inRangeUp(mDragCentrePoint);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        backAnimator.setInterpolator(new OvershootInterpolator(4.0f));
        backAnimator.setDuration(500);
        backAnimator.start();
    }

    /**
     * 移动出规定范围
     */
    private void outUp() {
        // 外面松手
        if (rangeMove > mFarthestDistance) {
            isOutUp = true;
            if (stickViewListener != null) {
                stickViewListener.outRangeUp(mDragCentrePoint);
            }
        }
        // 里面松手
        else {
            isOutUp = false;
            if (stickViewListener != null) {
                stickViewListener.out2InRangeUp(mDragCentrePoint);
            }
            mDragCentrePoint.set(mFixCentrePoint.x, mFixCentrePoint.y);
            invalidate();
        }
    }

    /**
     * 计算拖动过程中固定圆的半径
     */
    private float updateStickRadius() {
        float distance = GeometryUtil.getDistanceBetween2Points(mDragCentrePoint, mFixCentrePoint);
        distance = Math.min(distance, mFarthestDistance);
        float percent = distance * 1.0f / mFarthestDistance;

        return mFixRadius + (mMinFixRadius - mFixRadius) * percent;
    }

    /**
     * 设置画笔颜色
     *
     * @param mPaintColor
     */
    public void setPaintColor(int mPaintColor) {
        if (mPaint != null) {
            mPaint.setColor(mPaintColor);
        }
    }

    /**
     * 设置最大拖拽范围
     *
     * @param mFarthestDistance
     */
    public void setFarthestDistance(float mFarthestDistance) {
        this.mFarthestDistance = mFarthestDistance;
    }

    /**
     * 拖拽过程监听接口
     */
    public interface StickViewListener {

        /**
         * 当移出了规定范围，最后在范围内松手的回调
         *
         * @param dragCanterPoint
         */
        void out2InRangeUp(PointF dragCanterPoint);

        /**
         * 当移出了规定范围，最后在范围外松手的回调
         *
         * @param dragCanterPoint
         */
        void outRangeUp(PointF dragCanterPoint);

        /**
         * 一直没有移动出范围，在范围内松手的回调
         *
         * @param dragCanterPoint
         */
        void inRangeUp(PointF dragCanterPoint);
    }

    public void setStickViewListener(StickViewListener stickViewListener) {
        this.stickViewListener = stickViewListener;
    }
}
