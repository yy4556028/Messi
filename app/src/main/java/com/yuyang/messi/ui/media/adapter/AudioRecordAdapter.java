package com.yuyang.messi.ui.media.adapter;

import android.media.MediaMetadataRetriever;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyang.lib_base.BaseApp;
import com.yuyang.messi.R;

import java.io.File;
import java.util.List;

public class AudioRecordAdapter extends RecyclerView.Adapter<AudioRecordAdapter.MyViewHolder> {

    private List<String> mBeanList;


    public AudioRecordAdapter(List<String> beanList) {
        mBeanList = beanList;
    }

    public void setData(List<String> mBeanList) {
        this.mBeanList = mBeanList;
        notifyDataSetChanged();
    }

    public List<String> getData() {
        return mBeanList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_audio_record_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String filePath = mBeanList.get(position);
        holder.tvName.setText(filePath);
        holder.tvSize.setText(Formatter.formatFileSize(BaseApp.getInstance(), new File(filePath).length()));
        holder.tvDuration.setText(Math.round(getDuration(filePath) / 1000f) + "s");
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mBeanList == null ? 0 : mBeanList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvSize;
        TextView tvDuration;
        Button btnPlay;
        Button btnDetect;

        MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvSize = itemView.findViewById(R.id.tvSize);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            btnPlay = itemView.findViewById(R.id.btnPlay);
            btnDetect = itemView.findViewById(R.id.btnDetect);

            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMyClickListener != null) {
                        mMyClickListener.onItemPlay(getAdapterPosition());
                    }
                }
            });
            btnDetect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMyClickListener != null) {
                        mMyClickListener.onItemDetect(getAdapterPosition());
                    }
                }
            });
        }
    }

    private long getDuration(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        long duration = 0;
        try {
            if (path != null) {
                mmr.setDataSource(path);
            }
            String time = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            duration = Long.parseLong(time);
        } catch (Exception ex) {
        } finally {
            mmr.release();
        }
        return duration;
    }

    public interface MyClickListener {
        void onItemPlay(int index);

        void onItemDetect(int index);
    }

    private MyClickListener mMyClickListener;

    public void setMyClickListener(MyClickListener myClickListener) {
        mMyClickListener = myClickListener;
    }
}
