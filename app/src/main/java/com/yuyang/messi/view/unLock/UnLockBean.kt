package com.yuyang.messi.view.unLock

data class UnLockBean(
    val x: Float, // x坐标
    val y: Float, // y坐标
    val index: Int, // 下标
    /*
     * 当前类型(主要用来区分画笔颜色)
     *  - ORIGIN // 原始 (灰色)
     *  - DOWN // 按下 (浅绿色)
     *  - UP // 抬起 (深绿色)
     *  - ERROR // 错误 (红色)
     */
    var type: UnLockView.Type =
        UnLockView.Type.ORIGIN,
)