package com.yuyang.messi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuyang.messi.R;
import com.yuyang.messi.utils.FontUtil;

import java.util.ArrayList;
import java.util.List;

public class MainPopListAdapter extends BaseAdapter {

    private Context context;
    private List<Integer> iconList;
    private List<String> textList;

    private LayoutInflater inflater;

    public MainPopListAdapter(Context context) {
        this.context = context;
        this.iconList = new ArrayList<>();
        this.textList = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return textList == null ? 0 : textList.size();
    }

    @Override
    public String getItem(int position) {
        return textList == null ? null : textList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.view_choose_pop_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.icon.setImageResource(iconList.get(position));
//        holder.text.setTypeface(FontUtil.getLightTypeFace());
        holder.text.setTypeface(FontUtil.getRegularTypeFace());
//        holder.text.setTypeface(FontUtil.getLightItalicTypeFace());

        holder.text.setText(textList.get(position));

        return convertView;
    }

    public void updateData(List<Integer> iconList, List<String> textList) {

        this.iconList = iconList;
        this.textList = textList;
        notifyDataSetChanged();
    }

    private class ViewHolder {

        private ImageView icon;
        private TextView text;

        public ViewHolder(View view) {
            icon = (ImageView) view.findViewById(R.id.view_choose_pop_item_image);
            text = (TextView) view.findViewById(R.id.view_choose_pop_item_text);
        }
    }
}
