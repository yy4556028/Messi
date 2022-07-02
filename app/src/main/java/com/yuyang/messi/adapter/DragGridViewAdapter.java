package com.yuyang.messi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.yuyang.lib_base.myglide.GlideApp;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;

import java.util.List;

public class DragGridViewAdapter extends BaseAdapter {

    private Context context;

    private List<String> imageUrlList;

    private int gridItemHeight;

    public DragGridViewAdapter(Context context, List<String> imageUrlList, int gridItemHeight) {
        this.context = context;
        this.imageUrlList = imageUrlList;
        this.gridItemHeight = gridItemHeight;
    }

    @Override
    public int getCount() {
        return imageUrlList == null ? 0 : imageUrlList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageUrlList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_drag_grid_item, null);
            convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, gridItemHeight));
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        GlideApp.with(parent.getContext())
                .load(imageUrlList.get(position))
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast(position + " click");
            }
        });

        return convertView;
    }

    class ViewHolder {
        ImageView imageView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.activity_drag_grid_item_image);
        }
    }
}
