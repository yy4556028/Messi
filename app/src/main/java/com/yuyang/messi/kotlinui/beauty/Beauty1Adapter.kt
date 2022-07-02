package com.yuyang.messi.kotlinui.beauty

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yuyang.lib_base.myglide.GlideApp
import com.yuyang.messi.R

class Beauty1Adapter(private val itemWidth: Int, data: MutableList<Beauty1Bean>?) :
    BaseQuickAdapter<Beauty1Bean, BaseViewHolder>(R.layout.activity_beauty_recyclerview_item, data) {

    override fun convert(helper: BaseViewHolder, item: Beauty1Bean?) {

        val imageView = helper.getView(R.id.activity_beauty_recyclerView_item_image) as ImageView
        val textView: TextView = helper.getView(R.id.activity_beauty_recyclerView_item_text)

        item?.apply {
            if (TextUtils.isEmpty(imageUrl)) {
                helper.itemView.visibility = View.GONE
            } else {
                helper.itemView.visibility = View.VISIBLE
            }

            GlideApp.with(mContext)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView)
        }
    }
}