package com.yuyang.messi.kotlinui.gank

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yuyang.lib_base.myglide.GlideApp
import com.yuyang.messi.R
import com.yuyang.messi.bean.GankBean
import com.yuyang.lib_base.browser.BrowserActivity
import java.util.*

class GankChildRecyclerAdapter(private val context: Context) : RecyclerView.Adapter<GankChildRecyclerAdapter.MyHolder>() {

    private val mInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var list: MutableList<GankBean> = ArrayList()

    fun updateData(data: List<GankBean>?) {
        list.clear()
        if (data != null) {
            list.addAll(data)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): MyHolder {
        return MyHolder(mInflater.inflate(R.layout.fragment_gank_child_recycler_item, parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {

        val gankBean = list[position]

        holder.dateTextView.visibility = View.GONE
        holder.descTextView.visibility = View.GONE
        holder.imageView.visibility = View.GONE

        if (position == 0) {
            holder.dateTextView.visibility = View.VISIBLE

        } else {

            val isEqual = list[position - 1].publishedAt == gankBean.publishedAt
            if (!isEqual) {
                holder.dateTextView.visibility = View.VISIBLE
            } else {
                holder.dateTextView.visibility = View.GONE
            }
        }
        if (gankBean.url.endsWith(".jpg")) { //if it's image
            holder.imageView.visibility = View.VISIBLE
            GlideApp.with(context)
                    .load(list[position].url)
                    .into(holder.imageView)
        } else {
            holder.descTextView.visibility = View.VISIBLE
            holder.descTextView.text = gankBean.desc
        }

        gankBean.apply {
            holder.dateTextView.text = publishedAt
            holder.sourceTextView.text = source
            holder.peopleTextView.text = who
            holder.timeTextView.text = publishedAt.substring(0, 10)
            holder.tagTextView.text = type
        }
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dateTextView: TextView = itemView.findViewById(R.id.fragment_child_item_date)
        val descTextView: TextView = itemView.findViewById(R.id.fragment_child_item_desc)
        var imageView: ImageView = itemView.findViewById(R.id.fragment_child_item_image)
        val sourceTextView: TextView = itemView.findViewById(R.id.fragment_child_item_source)
        val peopleTextView: TextView = itemView.findViewById(R.id.fragment_child_item_people)
        val timeTextView: TextView = itemView.findViewById(R.id.fragment_child_item_time)
        val tagTextView: TextView = itemView.findViewById(R.id.fragment_child_item_tag)

        init {
            itemView.setOnClickListener {
                val url = list[adapterPosition].url
                val images = ArrayList<String>()
                images.add(url)
                if (!url.endsWith(".jpg")) {
                    BrowserActivity.launchActivity(context, list[adapterPosition].desc, url)
                } else {
//                    Intent intent = new Intent(context, ViewPicActivity.class);
//                    intent.putStringArrayListExtra(ViewPicActivity.IMG_URLS, images);
//                    context.startActivity(intent);
                }
            }
        }
    }
}