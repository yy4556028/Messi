package com.yuyang.messi.behavior;

import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class TestBahavior extends CoordinatorLayout.Behavior {

    /*
     * @param child: 当前view
     * @param dependency: 需要依赖的View
     */
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        return super.onDependentViewChanged(parent, child, dependency);
    }
}
