package com.yuyang.lib_base.recyclerview.layout_manager;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 * edit by yc
 */

public class ViewPagerLayoutManager extends LinearLayoutManager {

    private PagerSnapHelper mPagerSnapHelper;
    private OnViewPagerListener mOnViewPagerListener;
    private int mDrift;//位移，用来判断移动方向

    public ViewPagerLayoutManager(Context context, int orientation) {
        super(context, orientation, false);
        init();
    }

    public ViewPagerLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        init();
    }

    private void init() {
        mPagerSnapHelper = new PagerSnapHelper();
    }

    @Override
    public void onAttachedToWindow(RecyclerView recyclerView) {
        super.onAttachedToWindow(recyclerView);

        mPagerSnapHelper.attachToRecyclerView(recyclerView);
        recyclerView.addOnChildAttachStateChangeListener(mChildAttachStateChangeListener);
    }


    /**
     * 滑动状态的改变
     * 缓慢拖拽-> SCROLL_STATE_DRAGGING
     * 快速滚动-> SCROLL_STATE_SETTLING
     * 空闲状态-> SCROLL_STATE_IDLE
     *
     * @param state
     */
    @Override
    public void onScrollStateChanged(int state) {
        switch (state) {
            case RecyclerView.SCROLL_STATE_IDLE:
                View viewIdle = mPagerSnapHelper.findSnapView(this);
                if (viewIdle != null) {
                    int positionIdle = getPosition(viewIdle);
                    int childCount = getChildCount();
                    if (mOnViewPagerListener != null && getChildCount() <= 2) {
                        mOnViewPagerListener.onPageSelected(positionIdle, positionIdle == getItemCount() - 1);
                    }
                }
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                break;
            default:
                break;
        }
    }


    /**
     * 监听竖直方向的相对偏移量
     *
     * @param dy
     * @param recycler
     * @param state
     * @return
     */
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        this.mDrift = dy;
        return super.scrollVerticallyBy(dy, recycler, state);
    }


    /**
     * 监听水平方向的相对偏移量
     *
     * @param dx
     * @param recycler
     * @param state
     * @return
     */
    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        this.mDrift = dx;
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    /**
     * if return >= 0 snap is exist
     * if return < 0 snap is not exist
     *
     * @return
     */
    public int findSnapPosition() {
        View viewIdle = mPagerSnapHelper.findSnapView(this);
        if (viewIdle != null) {
            return getPosition(viewIdle);
        } else {
            return -1;
        }
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    public void setOnViewPagerListener(OnViewPagerListener listener) {
        this.mOnViewPagerListener = listener;
    }

    private RecyclerView.OnChildAttachStateChangeListener mChildAttachStateChangeListener = new RecyclerView.OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(View view) {
            if (mOnViewPagerListener != null && getChildCount() == 1) {
                mOnViewPagerListener.onInitComplete();
            }
        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {
            if (mOnViewPagerListener != null) {
                if (mDrift >= 0) {
                    mOnViewPagerListener.onPageRelease(true, getPosition(view));
                } else {
                    mOnViewPagerListener.onPageRelease(false, getPosition(view));
                }
            }

        }
    };

    public interface OnViewPagerListener {


        /**
         * 初始化第一个View
         */
        void onInitComplete();


        /**
         * 选中的监听以及判断是否滑动到底部
         *
         * @param position
         * @param isBottom
         */
        void onPageSelected(int position, boolean isBottom);


        /**
         * 释放的监听
         *
         * @param isNext
         * @param position
         */
        void onPageRelease(boolean isNext, int position);
    }
}
