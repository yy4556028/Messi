package com.yuyang.messi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yuyang.lib_base.myglide.GlideApp;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.riffsy.RiffsyMediaBean;

import java.util.List;

/**
 * http://blog.csdn.net/lmj623565791/article/details/45059587
 */
public class GifAdapter extends RecyclerView.Adapter<GifAdapter.MyHolder> {

    private Context context;
    private int itemWidth;

    private LayoutInflater mInflater;

    public List<RiffsyMediaBean> mediaBeanList;

    public GifAdapter(Context context, int itemWidth) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.itemWidth = itemWidth;
    }

    public void updateData(List<RiffsyMediaBean> mediaBeanList) {
        this.mediaBeanList = mediaBeanList;
        notifyDataSetChanged();
    }

    public void addData(List<RiffsyMediaBean> mediaBeanList) {
        this.mediaBeanList.addAll(mediaBeanList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new MyHolder(mInflater.inflate(R.layout.activity_staggered_recyclerview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {

        final RiffsyMediaBean mediaBean = mediaBeanList.get(position);

        if (mediaBean.getTinygif().getDims()[0] != 0) {
            holder.imageView.getLayoutParams().height = (itemWidth * mediaBean.getTinygif().getDims()[1] / mediaBean.getTinygif().getDims()[0]);
        } else {
            holder.imageView.getLayoutParams().height = RecyclerView.LayoutParams.WRAP_CONTENT;
        }

        GlideApp.with(context)
                .load(mediaBean.getTinygif().getPreview())
                .diskCacheStrategy(DiskCacheStrategy.NONE)//NONE 跳过硬盘缓存
                .skipMemoryCache(true)//true 跳过内存缓存
//                .crossFade()//淡入显示 默认300
                .into(holder.imageView);

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView, pos);
                    return true;
                }
            });
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return mediaBeanList == null ? 0 : mediaBeanList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public MyHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.activity_staggered_recyclerView_item_image);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
