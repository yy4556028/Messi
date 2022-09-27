package com.yuyang.messi.view.unLock

class UnLockAdapter : UnLockBaseAdapter() {
    override fun getNumberHrz(): Int = 3
    override fun getNumberVer(): Int = 3

    override fun getStyle(): UnLockView.Style = UnLockView.Style.FILL

//    override fun getOriginColor(): Int {
//        return Color.YELLOW
//    }
}