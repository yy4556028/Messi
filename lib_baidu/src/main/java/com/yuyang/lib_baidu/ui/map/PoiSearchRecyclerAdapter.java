package com.yuyang.lib_baidu.ui.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.yuyang.lib_baidu.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PoiSearchRecyclerAdapter extends RecyclerView.Adapter<PoiSearchRecyclerAdapter.MyHolder> {

    private Context context;

    public List<PoiInfo> beanList = new ArrayList<>();

    public PoiSearchRecyclerAdapter(Context context) {
        this.context = context;
    }

    public void addData(List<PoiInfo> beanList) {
        if (beanList == null) return;
        this.beanList.addAll(beanList);
        notifyDataSetChanged();
    }

    @NotNull
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int i) {
        return new MyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_baidu_poi_search_recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {

        final PoiInfo poiInfo = beanList.get(position);
        holder.nameText.setText(poiInfo.name);
        holder.addressText.setText(poiInfo.address);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return beanList == null ? 0 : beanList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        public TextView nameText;
        public TextView addressText;

        public MyHolder(View itemView) {
            super(itemView);
            nameText = (TextView) itemView.findViewById(R.id.activity_baidu_poi_search_recycler_item_name);
            addressText = (TextView) itemView.findViewById(R.id.activity_baidu_poi_search_recycler_item_address);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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



