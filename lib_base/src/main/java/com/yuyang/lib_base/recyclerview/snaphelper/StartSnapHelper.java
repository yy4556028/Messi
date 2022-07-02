package com.yuyang.lib_base.recyclerview.snaphelper;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

public class StartSnapHelper extends LinearSnapHelper {

    private OrientationHelper mVerticalHelper, mHorizontalHelper;

    /**
     * 计算到targetView要移动的距离  这个也是关键  这个方法是在我们判断处理之后（也就是findSnapView方法）  所以distanceToStart的方法中直接计算此时recycleview的离最屏幕开始处的距离减去内边距就是需要移动的距离
     */
    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        //这里是个数组0返回的是横向的距离  1返回的是纵向的距离  默认的话你将每次都返回到第一个view
        int[] out = new int[2];
        //这里判断你设置的recycleview的布局管理器方向是横向还是纵向
        if (layoutManager.canScrollHorizontally()) {
            //然后将距离开始的距离存放在内
            out[0] = distanceToStart(targetView, getHorizontalHelper(layoutManager));
        } else {
            out[0] = 0;
        }

        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToStart(targetView, getVerticalHelper(layoutManager));
        } else {
            out[1] = 0;
        }
        return out;
    }

    //这里是获取开始显示的view方法
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {

        if (layoutManager instanceof LinearLayoutManager) {

            if (layoutManager.canScrollHorizontally()) {
                return getStartView(layoutManager, getHorizontalHelper(layoutManager));
            } else {
                return getStartView(layoutManager, getVerticalHelper(layoutManager));
            }
        }

        return super.findSnapView(layoutManager);
    }

    //这里是 计算距离开始的距离   由于calculateDistanceToFinalSnap的方法是在findSnapView之后所以这里计算的是recycleview的离最屏幕开始处的距离减去内边距就是需要移动的距离
    private int distanceToStart(View targetView, OrientationHelper helper) {
        return helper.getDecoratedStart(targetView) - helper.getStartAfterPadding();
    }

    //这里是我们自定义的右对齐的显示规则  也就是这里就是我实现自动最左对齐的关键  当然如果有其他的奇葩右对齐 --  （什么奇葩客户没见过）实现原理一样的
    private View getStartView(RecyclerView.LayoutManager layoutManager, OrientationHelper helper) {
        //这里返回null表示不做任何操作保持当前的位置

        if (layoutManager instanceof LinearLayoutManager) {
            int firstChild = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();

            /**
             * 2种效果，看需要哪种，这种比较正常
             */
//            boolean isLastItem = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition() == layoutManager.getItemCount() - 1;
            boolean isLastItem = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1;
            //当发现显示了最后一个item的时候不执行任何操作
            if (firstChild == RecyclerView.NO_POSITION || isLastItem) {
                return null;
            }

            View child = layoutManager.findViewByPosition(firstChild);
            //当发现当前第一个view最后到结束位置到屏幕距离大于item的一半的时候自动复位  或者用getDecoratedStart()判断条件反过来而已
            if (helper.getDecoratedEnd(child) >= helper.getDecoratedMeasurement(child) / 2 && helper.getDecoratedEnd(child) > 0) {
                return child;
            } else {
                //如果发现最后停留的位置小于item自身的一半  则再次判断是不是最后一个view 如果是将不执行任何操作
                if (((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1) {
                    return null;
                } else {
                    //如果不是返回下一个view
                    return layoutManager.findViewByPosition(firstChild + 1);
                }
            }
        }

        return super.findSnapView(layoutManager);
    }

    private OrientationHelper getVerticalHelper(RecyclerView.LayoutManager layoutManager) {
        if (mVerticalHelper == null) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return mVerticalHelper;
    }

    private OrientationHelper getHorizontalHelper(RecyclerView.LayoutManager layoutManager) {
        if (mHorizontalHelper == null) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return mHorizontalHelper;
    }
}
