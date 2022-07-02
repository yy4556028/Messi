package com.yuyang.messi.behavior;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class AAA_Behavior extends CoordinatorLayout.Behavior{

    //当解析layout完成时候调用 View#onAttachedToWindow() 然后紧接着调用该方法
    @Override
    public void onAttachedToLayoutParams(@NonNull CoordinatorLayout.LayoutParams params) {
        super.onAttachedToLayoutParams(params);
    }

    //当 view销毁的时候调用
    @Override
    public void onDetachedFromLayoutParams() {
        super.onDetachedFromLayoutParams();
    }

    //当 CoordinatorLayout#onInterceptTouchEvent() 事件的时候调用
    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull MotionEvent ev) {
        return super.onInterceptTouchEvent(parent, child, ev);
    }

    //当 CoordinatorLayout#onTouchEvent() 事件的时候调用
    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull MotionEvent ev) {
        return super.onTouchEvent(parent, child, ev);
    }

    /*
     * 设置背景色
     * 需要配合 getScrimOpacity() 使用 因为 getScrimOpacity() 默认 = 0f
     */
    @Override
    public int getScrimColor(@NonNull CoordinatorLayout parent, @NonNull View child) {
        return super.getScrimColor(parent, child);
    }

    //设置不透明度
    @Override
    public float getScrimOpacity(@NonNull CoordinatorLayout parent, @NonNull View child) {
        return super.getScrimOpacity(parent, child);
    }

    /*
     * 需要依赖的view
     *
     * @param child: 当前view
     * @param dependency: 需要依赖的View
     */
    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        return super.layoutDependsOn(parent, child, dependency);
    }

    //需要依赖的view发生变化的时候调用
    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        return super.onDependentViewChanged(parent, child, dependency);
    }

    //当被依赖的view移除view的时候调用
    @Override
    public void onDependentViewRemoved(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        super.onDependentViewRemoved(parent, child, dependency);
    }

    //调用 CoordinatorLayout#onMeasureChild() 的时候调用
    @Override
    public boolean onMeasureChild(@NonNull CoordinatorLayout parent, @NonNull View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    //调用CoordinatorLayout$onLayout() 的时候调用
    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull View child, int layoutDirection) {
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    //当 NestedScrollingChild#startNestedScroll() 的时候调用
    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
    }
}
