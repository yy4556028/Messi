package com.yuyang.messi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yuyang.messi.R;
import com.yuyang.messi.recycler.DragItemTouchCallBack;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FlowDragRecyclerAdapter extends RecyclerView.Adapter<FlowDragRecyclerAdapter.MyHolder> implements DragItemTouchCallBack.ItemTouchHelperAdapter {

    public interface ShowingType {
        int TAGS = 0;
        int ESSAY = 1;
    }

    private LayoutInflater mInflater;
    private Context context;
    public List<String> dataList = new ArrayList<>();
    private int contentType = -1;

    public FlowDragRecyclerAdapter(Context context) {
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateData(List<String> dataList, int type) {
        this.contentType = type;
        this.dataList.clear();
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

    @NotNull
    @Override
    public MyHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ShowingType.ESSAY) {
            view = mInflater.inflate(R.layout.activity_flow_drag_recycler_item_essay, parent, false);
        } else {
            view = mInflater.inflate(R.layout.activity_flow_drag_recycler_item_tag, parent, false);
        }
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder viewHolder, final int position) {
        final String word = dataList.get(position);
        viewHolder.textView.setText(word);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return contentType;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        final String animStartString = dataList.remove(fromPosition);
        dataList.add(toPosition, animStartString);
        notifyItemMoved(fromPosition, toPosition);
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public MyHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.activity_flow_drag_recycler_item_textView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        listener.onClick(position);
                    }
                }
            });
        }
    }

    public interface OnFlowClickListener {
        void onClick(int position);
    }

    private OnFlowClickListener listener;

    public void setOnFlowClickListener(OnFlowClickListener listener) {
        this.listener = listener;
    }

}



