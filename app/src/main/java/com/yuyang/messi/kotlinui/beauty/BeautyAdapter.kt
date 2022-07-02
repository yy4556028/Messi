package com.yuyang.messi.kotlinui.beauty

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yuyang.lib_base.myglide.GlideApp
import com.yuyang.messi.R

class BeautyAdapter(private val itemWidth: Int, data: MutableList<BeautyBean>?) :
    BaseQuickAdapter<BeautyBean, BaseViewHolder>(R.layout.activity_beauty_recyclerview_item, data) {

    override fun convert(helper: BaseViewHolder, item: BeautyBean?) {

        val imageView = helper.getView(R.id.activity_beauty_recyclerView_item_image) as ImageView
        val textView: TextView = helper.getView(R.id.activity_beauty_recyclerView_item_text)

        item?.apply {
            if (TextUtils.isEmpty(thumbURL)) {
                helper.itemView.visibility = View.GONE
            } else {
                helper.itemView.visibility = View.VISIBLE
            }

            if (width != 0) {
                imageView.layoutParams.height = itemWidth * height / width
            } else {
                imageView.layoutParams.height = RecyclerView.LayoutParams.WRAP_CONTENT
            }

            GlideApp.with(mContext)
                .load(thumbURL)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView)

            if (!TextUtils.isEmpty(fromPageTitleEnc)) {
                textView.visibility = View.VISIBLE
                textView.text = fromPageTitleEnc
            } else {
                textView.visibility = View.INVISIBLE
            }
        }
    }
}