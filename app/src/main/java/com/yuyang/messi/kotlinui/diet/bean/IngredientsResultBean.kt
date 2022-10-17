package com.yuyang.messi.kotlinui.diet.bean

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.IntDef

@Retention(AnnotationRetention.SOURCE)
@IntDef(EAT_LEVEL.UNKNOWN, EAT_LEVEL.OK, EAT_LEVEL.LESS, EAT_LEVEL.NO)
annotation class EAT_LEVEL {
    companion object {
        const val UNKNOWN = 0
        const val OK = 1
        const val LESS = 2
        const val NO = 3
    }
}

class IngredientsResultBean {

    @EAT_LEVEL
    var eatLevel: Int = EAT_LEVEL.UNKNOWN
    var name: String? = null
    var type: String? = null
    var result: String? = null

    constructor()

    @JvmOverloads
    constructor(eatLevel: Int = EAT_LEVEL.UNKNOWN, name: String?, type: String?, result: String?) {
        this.eatLevel = eatLevel
        this.name = name
        this.type = type
        this.result = result
    }

    @ColorInt
    fun getColor(): Int {
        return when (eatLevel) {
            EAT_LEVEL.OK -> Color.GREEN
            EAT_LEVEL.LESS -> Color.YELLOW
            EAT_LEVEL.NO -> Color.RED
            else -> Color.GRAY
        }
    }
}
