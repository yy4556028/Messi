package com.yuyang.messi.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyang.messi.R;

import java.util.List;

public class SwipeRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mInflater;
    private Context context;

    private List<String> beanList;

    public SwipeRecyclerViewAdapter(Context context) {
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateData(List<String> list) {
        beanList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return TextUtils.isEmpty(beanList.get(position))?1:0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        if (i == 0) {
            return new MyHolder(mInflater.inflate(R.layout.activity_recyclerview_item, parent, false));
        } else {
            return new TempHolder(mInflater.inflate(R.layout.view_progress_dialog, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        if (getItemViewType(position) == 0) {
            MyHolder myHolder = (MyHolder)viewHolder;
            myHolder.textView.setText("title - " + beanList.get(position));
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return beanList == null ? 0 : beanList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;

        public MyHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.activity_recyclerView_item_text);
            imageView = itemView.findViewById(R.id.activity_recyclerView_item_image);
        }
    }

    public static class TempHolder extends RecyclerView.ViewHolder {
        public TempHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.progress_without_text).setVisibility(View.VISIBLE);
            itemView.findViewById(R.id.progress_with_text).setVisibility(View.VISIBLE);
        }
    }

//    public interface OnPopClickListener {
//        void onItemClick(View view, int position);
//    }
//
//    private OnPopClickListener mOnItemClickListener;
//
//    public void setOnPopClickListener(OnPopClickListener mOnItemClickListener) {
//        this.mOnItemClickListener = mOnItemClickListener;
//    }
}



