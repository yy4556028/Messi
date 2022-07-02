package com.yuyang.messi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyang.aidl_audioplayer.AudioBean;
import com.yuyang.lib_base.myglide.GlideApp;
import com.yuyang.messi.R;

import java.util.ArrayList;

public class AudioRecyclerAdapter extends RecyclerView.Adapter<AudioRecyclerAdapter.MyHolder> {

    private final LayoutInflater mInflater;

    public ArrayList<AudioBean> audioBeanList;

    private AudioBean currentAudioBean;

    public AudioRecyclerAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(ArrayList<AudioBean> audioBeanList) {
        this.audioBeanList = audioBeanList;
        notifyDataSetChanged();
    }

    public void setCurrentAudioBean(AudioBean currentAudioBean) {
        this.currentAudioBean = currentAudioBean;
        notifyDataSetChanged();
    }

    public AudioBean getCurrentAudioBean() {
        return currentAudioBean;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new MyHolder(mInflater.inflate(R.layout.activity_audio_recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        AudioBean audioBean = audioBeanList.get(position);

        if (audioBean.equals(currentAudioBean)) {
            holder.mark.setVisibility(View.VISIBLE);
        } else {
            holder.mark.setVisibility(View.INVISIBLE);
        }

        GlideApp.with(holder.itemView.getContext())
                .load(audioBean.getImage())
                .error(R.mipmap.ic_launcher)
                .into(holder.icon);
        holder.title.setText(audioBean.getTitle());
        holder.artist.setText(audioBean.getArtist());
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return audioBeanList == null ? 0 : audioBeanList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        View mark;
        ImageView icon;
        TextView title;
        TextView artist;

        public MyHolder(View itemView) {
            super(itemView);
            mark = itemView.findViewById(R.id.activity_audio_recycler_item_selected);
            icon = itemView.findViewById(R.id.activity_audio_recycler_item_icon);
            title = itemView.findViewById(R.id.activity_audio_recycler_item_title);
            artist = itemView.findViewById(R.id.activity_audio_recycler_item_artist);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {

        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

}



