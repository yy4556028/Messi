package com.yuyang.messi.view.unLock

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.yuyang.lib_base.kotlinext.dp
import kotlin.math.hypot

// https://gitee.com/lanyangyangzzz/custom-view-project/blob/master/app/src/main/java/com/example/customviewproject/d/view/d3/JiuGonGeUnLockView.kt
open class UnLockView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeJoin = Paint.Join.BEVEL//Paint.Join.BEVEL
    }

    private val unLockPoints = arrayListOf<ArrayList<UnLockBean>>()

    // 大圆半径
    private val bigRadius by lazy { width / (NUMBER_HRZ * 2) * 0.7f }//矩形块的0.7

    // 小圆半径
    private val smallRadius by lazy { bigRadius * 0.2f }

    // 当前状态
    private var currentType = Type.ORIGIN
    private var currentStyle = Style.FILL

    // 是否穿过圆心
    private var isDrawLineCenterCircle = false

    // 密码
    open var password = listOf<Int>()

    // 点击回调
    open var resultClick: Click? = null

    open var adapter: UnLockBaseAdapter = UnLockAdapter()

    companion object {
        private var NUMBER_HRZ = 3
        private var NUMBER_VER = 5

        // 原始颜色
        private var ORIGIN_COLOR = Color.parseColor("#D8D9D8")

        // 按下颜色
        private var DOWN_COLOR = Color.parseColor("#3AD94E")

        // 抬起颜色
        private var UP_COLOR = Color.parseColor("#57D900")

        // 错误颜色
        private var ERROR_COLOR = Color.parseColor("#D9251E")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        adapter.also {
            NUMBER_HRZ = it.getNumberHrz()
            NUMBER_VER = it.getNumberVer()
            currentStyle = it.getStyle()
            ORIGIN_COLOR = it.getOriginColor()
            DOWN_COLOR = it.getDownColor()
            UP_COLOR = it.getUpColor()
            ERROR_COLOR = it.getErrorColor()
            isDrawLineCenterCircle = it.lineCenterCircle()
        }

        val width = resolveSize(measuredWidth, widthMeasureSpec)
        val height = resolveSize(measuredWidth * NUMBER_VER / NUMBER_HRZ, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 单个直径
        val diameter = width / NUMBER_HRZ
        var index = 1

        for (i in 0 until NUMBER_VER) {// 循环每一行
            val list = arrayListOf<UnLockBean>()

            for (j in 0 until NUMBER_HRZ) {// 循环每一列
                list.add(
                    UnLockBean(
                        diameter / 2f + diameter * j,
                        diameter / 2f + diameter * i,
                        index++
                    )
                )
            }
            unLockPoints.add(list)
        }
    }

    // 记录选中的坐标
    private val recordList = arrayListOf<UnLockBean>()

    private val path = Path()

    private val line = Pair(PointF(), PointF())

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {

                if (event.action == MotionEvent.ACTION_DOWN) clear()

                val pointF = isContains(event.x, event.y)
                pointF?.also {
                    // 将当前类型改变为按下类型
                    it.type = Type.DOWN
                    currentType = Type.DOWN
                    if (!recordList.contains(it)) {// 这里会重复调用，所以需要判断是否包含，如果不包含才添加
                        recordList.add(it)

                        if (isDrawLineCenterCircle) {
                            if (recordList.size == 1) {
                                path.moveTo(it.x, it.y)
                            } else {
                                path.lineTo(it.x, it.y)
                            }
                            line.first.x = it.x
                            line.first.y = it.y
                        } else {
                            if (recordList.size >= 2) {
                                val pathStart = recordList[recordList.size - 2]
                                val pathEnd = recordList[recordList.size - 1]

                                val dx = (pathEnd.x - pathStart.x)
                                val dy = (pathEnd.y - pathStart.y)
                                val distance = hypot(dx, dy)
                                val offsetX = (dx * smallRadius / distance)
                                val offsetY = (dy * smallRadius / distance)

                                path.moveTo(pathStart.x + offsetX, pathStart.y + offsetY)
                                path.lineTo(pathEnd.x - offsetX, pathEnd.y - offsetY)
                            }
                        }
                    }
                }

                if (!isDrawLineCenterCircle && recordList.isNotEmpty()) {
                    val lineStart = recordList.last()

                    val lineDx = (event.x - lineStart.x)
                    val lineDy = (event.y - lineStart.y)
                    val lineDistance = hypot(lineDx, lineDy)
                    val lineOffsetX = (lineDx * smallRadius / lineDistance)
                    val lineOffsetY = (lineDy * smallRadius / lineDistance)

                    line.first.x = lineStart.x + lineOffsetX
                    line.first.y = lineStart.y + lineOffsetY
                }

                // 手指的位置
                line.second.x = event.x
                line.second.y = event.y
            }

            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                // 清空移动线
                line.first.x = 0f
                line.first.y = 0f
                line.second.x = 0f
                line.second.y = 0f

                if (recordList.isEmpty()) {
                    return super.onTouchEvent(event)
                }

                // 标记是否成功
                val isSuccess =
                    if (recordList.size == password.size) {
                        val list = recordList.zip(password).filter {
                            // 通过判断每一个值
                            it.first.index == it.second
                        }.toList()

                        // 如果每一个值都相同，那么就回调成功
                        list.size == password.size
                    } else {
                        false
                    }

                // 密码错误，将标记改变成错误
                if (!isSuccess) {
                    recordList.forEach {
                        it.type = Type.ERROR
                    }

                    // 延迟1秒清空
                    postDelayed({
                        clear()
                    }, 1000)
                }

                // 设置当前状态
                currentType =
                    if (isSuccess) Type.UP else Type.ERROR

                resultClick?.result(password, recordList.map { it.index }.toList(), isSuccess)
            }
        }

        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {

        if (currentStyle == Style.FILL) {
            paint.style = Paint.Style.FILL
            paint.strokeWidth = 2.dp
        } else {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2.dp
        }

        unLockPoints.forEach {
            it.forEach { data ->

                // 绘制大圆
                paint.color = getTypeColor(data.type)
                paint.alpha = (255 * 0.6).toInt()
                canvas.drawCircle(data.x, data.y, bigRadius, paint)

                // 绘制小圆
                paint.alpha = 255
                canvas.drawCircle(data.x, data.y, smallRadius, paint)
            }
        }

        paint.color = getTypeColor(currentType) // 默认按下颜色
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4.dp
        canvas.drawPath(path, paint)

        // 绘制移动线
        if (line.first.x != 0f && line.second.x != 0f) {
            canvas.drawLine(
                line.first.x,
                line.first.y,
                line.second.x,
                line.second.y,
                paint
            )
        }
    }

    private fun clear() {
        if (currentType == Type.DOWN) {
            return
        }
        unLockPoints.forEach {
            it.forEach { data ->
                data.type = Type.ORIGIN
            }
        }
        recordList.clear()

        currentType = Type.ORIGIN

        path.reset() // 重置

        line.first.x = 0f
        line.first.y = 0f
        line.second.x = 0f
        line.second.y = 0f

        invalidate()
    }

    /*
    * 作者:史大拿
    * 创建时间: 9/13/22 10:17 AM
    * @param x,y: 点击坐标位置
    */
    private fun isContains(x: Float, y: Float) = let {
        unLockPoints.forEach {
            it.forEach { data ->
                // 循环所有坐标 判断两个位置是否相同
                if (RectF(
                        data.x - bigRadius,
                        data.y - bigRadius,
                        data.x + bigRadius,
                        data.y + bigRadius
                    ).contains(x, y)) {
                    return@let data
                }
            }
        }
        return@let null
    }

    /*
    * 作者:史大拿
    * 创建时间: 9/13/22 1:25 PM
    * TODO 获取类型对应颜色
    */
    private fun getTypeColor(type: Type): Int {
        return when (type) {
            Type.ORIGIN -> ORIGIN_COLOR
            Type.DOWN -> DOWN_COLOR
            Type.UP -> UP_COLOR
            Type.ERROR -> ERROR_COLOR
        }
    }

    enum class Type {
        ORIGIN, // 原始
        DOWN, // 按下
        UP, // 抬起
        ERROR // 错误
    }

    enum class Style {
        FILL, // 占满
        STROKE, // 空心
    }

    fun interface Click {
        /*
         * TODO
         * @param pwd:正确密码
         * @param inputPuw:输入密码
         * @param isSuccess:是否输入成功
         */
        fun result(pwd: List<Int>, inputPwd: List<Int>, isSuccess: Boolean)
    }
}