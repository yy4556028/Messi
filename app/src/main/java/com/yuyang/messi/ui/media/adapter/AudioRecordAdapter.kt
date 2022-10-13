package com.yuyang.messi.ui.media.adapter

import android.media.MediaMetadataRetriever
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yuyang.lib_base.BaseApp
import com.yuyang.messi.R
import com.yuyang.messi.ui.media.adapter.AudioRecordAdapter.MyViewHolder
import java.io.File
import javax.inject.Inject
import kotlin.math.roundToLong

class AudioRecordAdapter @Inject constructor() : RecyclerView.Adapter<MyViewHolder>() {

    private var mBeanList: List<String>? = null

    fun setData(mBeanList: List<String>?) {
        this.mBeanList = mBeanList
        notifyDataSetChanged()
    }

    fun getData(): List<String?>? {
        return mBeanList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_audio_record_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val filePath = mBeanList!![position]
        holder.tvName.text = filePath
        holder.tvSize.text = Formatter.formatFileSize(
            BaseApp.getInstance(),
            File(filePath).length()
        )
        holder.tvDuration.text = String.format("%ss", (getDuration(filePath) / 1000f).roundToLong().toString())
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return mBeanList?.size ?: 0
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView
        var tvSize: TextView
        var tvDuration: TextView
        var btnPlay: Button
        var btnDetect: Button

        init {
            tvName = itemView.findViewById(R.id.tvName)
            tvSize = itemView.findViewById(R.id.tvSize)
            tvDuration = itemView.findViewById(R.id.tvDuration)
            btnPlay = itemView.findViewById(R.id.btnPlay)
            btnDetect = itemView.findViewById(R.id.btnDetect)
            btnPlay.setOnClickListener {
                if (mMyClickListener != null) {
                    mMyClickListener!!.onItemPlay(adapterPosition)
                }
            }
            btnDetect.setOnClickListener {
                if (mMyClickListener != null) {
                    mMyClickListener!!.onItemDetect(adapterPosition)
                }
            }
        }
    }

    private fun getDuration(path: String?): Long {
        val mmr = MediaMetadataRetriever()
        var duration: Long = 0
        try {
            if (path != null) {
                mmr.setDataSource(path)
            }
            val time = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            duration = time!!.toLong()
        } catch (ex: Exception) {
        } finally {
            mmr.release()
        }
        return duration
    }

    interface MyClickListener {
        fun onItemPlay(index: Int)
        fun onItemDetect(index: Int)
    }

    private var mMyClickListener: MyClickListener? = null
    fun setMyClickListener(myClickListener: MyClickListener?) {
        mMyClickListener = myClickListener
    }
}