package com.yuyang.messi.view.unLock

import android.graphics.Color

abstract class UnLockBaseAdapter {
    // 设置宫格个数
    abstract fun getNumberHrz(): Int
    abstract fun getNumberVer(): Int

    // 设置样式
    abstract fun getStyle(): UnLockView.Style

    /*
     * 作者:史大拿
     * 创建时间: 9/14/22 10:24 AM
     * TODO 画连接线时,是否穿过圆心
     */
    open fun lineCenterCircle() = false

    // 设置原始颜色
    open fun getOriginColor(): Int = let {
        return Color.parseColor("#D8D9D8")
    }

    // 设置按下颜色
    open fun getDownColor(): Int = let {
        return Color.parseColor("#3AD94E")
    }

    // 设置抬起颜色
    open fun getUpColor(): Int = let {
        return Color.parseColor("#57D900")
    }

    // 设置错误颜色
    open fun getErrorColor(): Int = let {
        return Color.parseColor("#D9251E")
    }
}