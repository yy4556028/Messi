package com.yuyang.lib_base.utils

import android.content.res.ColorStateList
import android.graphics.Paint
import androidx.annotation.ColorInt
import com.google.android.material.shape.*

/**
 * https://blog.csdn.net/yechaoa/article/details/117339632
 */
object DrawableUtil {

    fun createUnknownDrawable(@ColorInt color: Int, strokeColorStateList: ColorStateList?): MaterialShapeDrawable {
        val shapeAppearanceModel = ShapeAppearanceModel.builder().apply {
            setAllCorners(RoundedCornerTreatment())
            setAllCornerSizes(50f)
            setAllEdges(TriangleEdgeTreatment(50f, false))
        }.build()

        val drawable = MaterialShapeDrawable(shapeAppearanceModel).apply {
            //        drawable.setTint(ContextCompat.getColor(BaseApp.getInstance(), R.color.colorPrimary));
            setTint(color)
            paintStyle = Paint.Style.FILL_AND_STROKE
            strokeWidth = 50f
            //        drawable.setStrokeColor(ContextCompat.getColorStateList(BaseApp.getInstance(), R.color.red));
            strokeColor = strokeColorStateList
        }

        return drawable
    }

    fun createChatLeftDrawable(@ColorInt color: Int): MaterialShapeDrawable {
        // 代码设置 聊天框效果
        val shapeAppearanceModel3 = ShapeAppearanceModel.builder().apply {
            setAllCorners(RoundedCornerTreatment())
            setAllCornerSizes(20f)
            setRightEdge(object : TriangleEdgeTreatment(20f, false) {
                // center 位置 ， interpolation 角的大小
                override fun getEdgePath(length: Float, center: Float, interpolation: Float, shapePath: ShapePath) {
                    super.getEdgePath(length, 35f, interpolation, shapePath)
                }
            })
        }.build()

        val drawable = MaterialShapeDrawable(shapeAppearanceModel3).apply {
            setTint(color)
            paintStyle = Paint.Style.FILL
        }

        return drawable
    }
}