package com.yuyang.messi.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.core.view.MotionEventCompat;

import com.yuyang.lib_base.utils.statusbar.MyStatusBarUtil;


/**
 * 节点可拖曳的Gridview
 */
public class DragGridView extends GridView {

    private final static String TAG = DragGridView.class.getSimpleName();

    /**
     * item 长按响应时间
     */
    private long dragResponseMS = 500;

    /**
     * 是否默认拖拽， 默认不可以
     */
    private boolean isDrag = false;

    private int mDownX;
    private int mDownY;
    private int moveX;
    private int moveY;

    /**
     * 正在拖拽的position
     */
    private int mDragPosition;

    /**
     * 刚开始拖拽的 item 对应的View
     */
    private View mStartDragItemView = null;

    /**
     * 用于拖拽的镜像， 这里直接用一个ImageView
     */
    private ImageView mDragImageView;

    /**
     * 振动器
     */
    private Vibrator mVibrator;

    private WindowManager mWindowManager;

    private WindowManager.LayoutParams mDragParam;

    /**
     * 拖拽的 item 对应的bitmap
     */
    private Bitmap mDragBitmap;

    /**
     * 按下的点到 item 上边缘的距离
     */
    private int mPoint2ItemTop;

    /**
     * 按下的点到 item 左边缘的距离
     */
    private int mPoint2ItemLeft;

    /**
     * DragGridView距离屏幕顶部的偏移量
     */
    private int mOffset2Top;

    /**
     * DragGridView距离屏幕左边的偏移量
     */
    private int mOffset2Left;

    /**
     * 状态栏高度
     */
    private int mStatusHeight;

    /**
     * DragGridView自动向上 向下滚动的边界值
     */
    private int mUpScrollBorder;
    private int mDownScrollBorder;

    /**
     * DragGridView 自动滚动的速度
     */
    private static final int speed = 20;

    private OnDragListener mListener;

    // 新定义

    private DisplayMetrics metrics;

    public DragGridView(Context context) {
        this(context, null);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mStatusHeight = MyStatusBarUtil.getStatusBarHeight();
        metrics = context.getResources().getDisplayMetrics();
    }


    public void setListener(OnDragListener mListener) {
        this.mListener = mListener;
    }

    private final Handler mHandler = new Handler(Looper.myLooper());
    private Runnable mLongClickRunnable = new Runnable() {
        public void run() {
            // 设置可拖拽
            isDrag = true;
            // 震动一下
            mVibrator.vibrate(50);
//            // 触发 drag 事件时 隐藏该item
//            mStartDragItemView.setVisibility(View.INVISIBLE);
            createDragImage(mDragBitmap, mDownX, mDownY);
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mDownX = (int) ev.getX();
                mDownY = (int) ev.getY();

                mDragPosition = pointToPosition(mDownX, mDownY);//根据x,y坐标获取所点击的item的position
                if (mDragPosition == AbsListView.INVALID_POSITION) {
                    return super.dispatchTouchEvent(ev);
                }

                mHandler.postDelayed(mLongClickRunnable, dragResponseMS);//延迟执行长按

                mStartDragItemView = getChildAt(mDragPosition - getFirstVisiblePosition()); //根据position获取item所对应的Item

                mPoint2ItemTop = mDownY - mStartDragItemView.getTop();
                mPoint2ItemLeft = mDownX - mStartDragItemView.getLeft();

                // grid 到 屏幕 top 和 left 的距离
                mOffset2Top = (int) (ev.getRawY() - mDownY);
                mOffset2Left = (int) (ev.getRawX() - mDownX);

                //获取DragGridView自动向上滚动的偏移量，小于这个值，DragGridView向下滚动
                mDownScrollBorder = metrics.heightPixels / 4;
                //获取DragGridView自动向下滚动的偏移量，大于这个值，DragGridView向上滚动
                mUpScrollBorder = getHeight() / 4;

                //开启mDragItemView绘图缓存
                mStartDragItemView.setDrawingCacheEnabled(true);
                //获取mDragItemView在缓存中的Bitmap对象
                mDragBitmap = Bitmap.createBitmap(mStartDragItemView.getDrawingCache());
                //这一步很关键，释放绘图缓存，避免出现重复的镜像
                mStartDragItemView.destroyDrawingCache();
                break;

            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getX();
                int moveY = (int) ev.getY();

                if (!isTouchInItem(mStartDragItemView, moveX, moveY)) {
                    mHandler.removeCallbacks(mLongClickRunnable);
                }
                break;

            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacks(mLongClickRunnable);
                mHandler.removeCallbacks(mScrollRunnable);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isDrag && mDragImageView != null) {
            switch (MotionEventCompat.getActionMasked(event)) {
                case MotionEvent.ACTION_MOVE:
                    moveX = (int) event.getX();
                    moveY = (int) event.getY();
                    onDragItem(moveX, moveY);
                    break;

                case MotionEvent.ACTION_UP:
                    onStopDrag();
                    isDrag = false;
                    onMoveItem(mDragPosition);
                    break;

                default:
                    break;
            }
            return true;
        }

        return super.onTouchEvent(event);
    }


    /**
     * 是否点击在GridView的Item上面
     *
     * @param dragView
     * @param x
     * @param y
     * @return
     */
    private boolean isTouchInItem(View dragView, int x, int y) {
        if (dragView == null) {
            return false;
        }

        //        Rect rect = new Rect();
        //        dragView.getHitRect(rect);
        //        if (rect.contains(x, y))

        int leftOffset = dragView.getLeft();
        int topOffset = dragView.getTop();
        if (x < leftOffset || x > leftOffset + dragView.getWidth()) {
            return false;
        }

        if (y < topOffset || y > topOffset + dragView.getHeight()) {
            return false;
        }

        return true;
    }


    /**
     * 创建拖动镜像
     *
     * @param bitmap
     * @param downX  按下的点相对父控件的X坐标
     * @param downY  按下的点相对父控件的Y坐标
     */
    private void createDragImage(Bitmap bitmap, int downX, int downY) {
        mDragParam = new WindowManager.LayoutParams();
        mDragParam.format = PixelFormat.TRANSLUCENT; //图片之外的其他地方透明
        mDragParam.gravity = Gravity.TOP | Gravity.LEFT;
        mDragParam.x = downX - mPoint2ItemLeft + mOffset2Left;
        mDragParam.y = downY - mPoint2ItemTop + mOffset2Top;
        mDragParam.alpha = 0.55f;
        mDragParam.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mDragParam.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mDragParam.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        mDragImageView = new ImageView(getContext());
        mDragImageView.setImageBitmap(bitmap);
        mWindowManager.addView(mDragImageView, mDragParam);
    }

    private void onDragItem(int moveX, int moveY) {
        mDragParam.x = moveX - mPoint2ItemLeft + mOffset2Left;
        mDragParam.y = moveY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
        mWindowManager.updateViewLayout(mDragImageView, mDragParam);

        mHandler.post(mScrollRunnable);
    }

    /**
     * 当moveY的值大于向上滚动的边界值，触发GridView自动向上滚动
     * 当moveY的值小于向下滚动的边界值，触犯GridView自动向下滚动
     * 否则不进行滚动
     */
    private Runnable mScrollRunnable = new Runnable() {

        @Override
        public void run() {
            int scrollY;
            if (moveY > mUpScrollBorder) {
                scrollY = speed;
                mHandler.postDelayed(mScrollRunnable, 25);
            } else if (moveY < mDownScrollBorder) {
                scrollY = -speed;
                mHandler.postDelayed(mScrollRunnable, 25);
            } else {
                scrollY = 0;
                mHandler.removeCallbacks(mScrollRunnable);
            }


            smoothScrollBy(scrollY, 10);
        }
    };

    private void onMoveItem(int position) {
        if (mListener != null && mDragParam.y < metrics.heightPixels * 1 / 2) {
            mListener.onDrag(position);
        }
    }


    /**
     * 停止拖拽我们将之前隐藏的item显示出来，并将镜像移除
     */
    private void onStopDrag() {
        View view = getChildAt(mDragPosition - getFirstVisiblePosition());
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
        removeDragImage();
    }

    private void removeDragImage() {
        if (mDragImageView != null) {
            mWindowManager.removeView(mDragImageView);
            mDragImageView = null;
        }
    }

    public interface OnDragListener {
        public void onDrag(int position);
    }

    /**
     * 销毁 grid view 时销毁镜像
     */
    @Override
    protected void onDetachedFromWindow() {
        removeDragImage();
        super.onDetachedFromWindow();
    }

}
