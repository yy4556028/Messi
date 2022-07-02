package com.yuyang.messi.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyang.messi.R;
import com.yuyang.messi.ui.category.scroll.RecyclerViewActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyHolder> {

    private LayoutInflater mInflater;
    private Context context;
    public ItemTouchHelper itemTouchHelper;

    private List<RecyclerViewActivity.DataBean> list;

    public RecyclerViewAdapter(Context context) {
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateData(List<RecyclerViewActivity.DataBean> data) {
        if (list == null)
            list = new ArrayList<>();
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(list, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void onItemDismiss(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new MyHolder(mInflater.inflate(R.layout.activity_recyclerview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder viewHolder, final int i) {
        viewHolder.textView.setText(list.get(i).name);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;

        public MyHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.activity_recyclerView_item_text);
            imageView = itemView.findViewById(R.id.activity_recyclerView_item_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.get(getAdapterPosition()).intentClass != null) {
                        final Class c = list.get(getAdapterPosition()).intentClass;
                        context.startActivity(new Intent(context, c));
                    }
                }
            });
            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (itemTouchHelper != null) {
                            itemTouchHelper.startDrag(MyHolder.this);
                        }
                    }
                    return false;
                }
            });
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



