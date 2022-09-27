package com.yuyang.lib_base.kotlinext

import android.content.res.Resources
import android.util.TypedValue
import kotlin.math.pow
import kotlin.math.sqrt

val Int.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(),
        Resources.getSystem().displayMetrics
    )

infix fun Int.diagonalDistance(b: Int): Float = let {
    val a = this
    return sqrt(a.toDouble().pow(2.0) + b.toDouble().pow(2.0)).toFloat()
}

val Float.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this,
        Resources.getSystem().displayMetrics
    )

infix fun Float.diagonalDistance(b: Float): Float = let {
    val a = this
    return sqrt(a.toDouble().pow(2.0) + b.toDouble().pow(2.0)).toFloat()
}