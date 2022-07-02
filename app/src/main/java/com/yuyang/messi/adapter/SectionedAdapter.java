package com.yuyang.messi.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.messi.R;
import com.yuyang.messi.view.scroll.pinnedsectionlistview.SectionedBaseAdapter;

public class SectionedAdapter extends SectionedBaseAdapter {

    @Override
    public Object getItem(int section, int position) {
        return null;
    }

    @Override
    public long getItemId(int section, int position) {
        return 0;
    }

    @Override
    public int getSectionCount() {
        return 7;
    }

    @Override
    public int getCountForSection(int section) {
        return 15;
    }

    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        TextView itemView = null;
        if (convertView == null) {
            itemView = new TextView(parent.getContext());
            itemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtils.dp2px(40)));
            itemView.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.colorPrimary));
        } else {
            itemView = (TextView) convertView;
        }
        itemView.setText("Section " + section + " Item " + position);
        return itemView;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        TextView sectionHeader = null;
        if (convertView == null) {
            sectionHeader = new TextView(parent.getContext());
            sectionHeader.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtils.dp2px(40)));
            sectionHeader.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.colorAccent));
        } else {
            sectionHeader = (TextView) convertView;
        }
        sectionHeader.setText("Header for section " + section);
        return sectionHeader;
    }

}
