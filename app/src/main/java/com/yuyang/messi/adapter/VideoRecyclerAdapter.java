package com.yuyang.messi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyang.lib_base.utils.ConvertUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.VideoBean;
import com.yuyang.messi.helper.VideoHelper;
import com.yuyang.messi.ui.media.VideoActivity;
import com.yuyang.lib_base.utils.CommonUtil;

import java.util.List;

public class VideoRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    private LayoutInflater mInflater;

    public List<VideoBean> videoBeanList;

    public VideoRecyclerAdapter(Context context, List<VideoBean> videoList) {
        this.context = context;
        this.videoBeanList = videoList;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<VideoBean> beanList) {
        videoBeanList = beanList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemHolder(mInflater.inflate(R.layout.activity_video_recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {

        ItemHolder holder = (ItemHolder) viewHolder;
        holder.itemView.getLayoutParams().height = (int) (CommonUtil.getScreenWidth() / VideoActivity.SPAN_COUNT * 0.75f);

        VideoBean videoBean = videoBeanList.get(position);

        holder.imageView.setImageBitmap(videoBean.getBitmap());
        holder.lengthText.setText(ConvertUtil.convertTimeMs(videoBean.getLength()));
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return videoBeanList == null ? 0 : videoBeanList.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView lengthText;

        private ItemHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.activity_video_recycler_item_image);
            lengthText = itemView.findViewById(R.id.activity_video_recycler_item_length);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(getAdapterPosition());
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemLongClick(getAdapterPosition());
                    }
                    return true;
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onItemLongClick(int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

}
