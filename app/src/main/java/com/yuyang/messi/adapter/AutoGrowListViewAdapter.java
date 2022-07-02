package com.yuyang.messi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.yuyang.lib_base.myglide.GlideApp;
import com.yuyang.messi.R;

import java.util.List;

public class AutoGrowListViewAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;

    private LayoutInflater inflater;

    public AutoGrowListViewAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_autogrowlist_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        GlideApp.with(context)
                .load(list.get(position))
                .into(holder.imageView);

        return convertView;
    }

    private class ViewHolder {

        private ImageView imageView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.activity_autoGrowList_img);
        }
    }
}
