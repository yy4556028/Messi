package com.yuyang.lib_base.kotlinext

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * 作者：唐子玄
 * 链接：https://juejin.cn/post/7165428399282847757
 * 来源：稀土掘金
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 */
fun RecyclerView.onItemVisibilityChange(
    percent: Float = 0.5f,
    viewGroups: List<ViewGroup>? = null,
    block: (itemView: View, adapterIndex: Int, isVisible: Boolean) -> Unit
) {
    val childVisibleRect = Rect()
    val visibleAdapterIndexs = mutableSetOf<Int>()
    val checkVisibility = {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val adapterIndex = getChildAdapterPosition(child)
            if(adapterIndex == RecyclerView.NO_POSITION) continue
            val isChildVisible = child.getLocalVisibleRect(childVisibleRect)
            val visibleArea = childVisibleRect.let { it.height() * it.width() }
            val realArea = child.width * child.height
            if (this.isInScreen && isChildVisible && visibleArea >= realArea * percent) {
                if (visibleAdapterIndexs.add(adapterIndex)) {
                    block(child, adapterIndex, true)
                }
            } else {
                if (adapterIndex in visibleAdapterIndexs) {
                    block(child, adapterIndex, false)
                    visibleAdapterIndexs.remove(adapterIndex)
                }
            }
        }
    }
    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            checkVisibility()
        }
    }
    addOnScrollListener(scrollListener)
    // 为列表添加全局可见性检测 ???
    onVisibilityChange(viewGroups?: emptyList(),false) { view, isVisible ->
        // 当列表可见时，检测其表项的可见性
        if (isVisible) {
            checkVisibility()
        } else {
            // 当列表不可见时，回调所有可见表项为不可见
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                val adapterIndex = getChildAdapterPosition(child)
                if (adapterIndex in visibleAdapterIndexs) {
                    block(child, adapterIndex, false)
                    visibleAdapterIndexs.remove(adapterIndex)
                }
            }
        }
    }
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
        }

        override fun onViewDetachedFromWindow(v: View) {
            if (v !is RecyclerView) return
            v.removeOnScrollListener(scrollListener)
            removeOnAttachStateChangeListener(this)
        }
    })
}