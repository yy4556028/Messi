package com.yuyang.messi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.messi.R;

/**
 * 创建者: yuyang
 * 创建日期: 2015-07-02
 * 创建时间: 16:14
 * DrawColorGridAdapter: 涂鸦demo中的画笔颜色 grid adapter
 *
 * @author yuyang
 * @version 1.0
 */
public class DrawColorGridAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private int[] paintColors;

    // 当前选中颜色的位置
    public int selectPosition = 0;

    public DrawColorGridAdapter(Context context, int[] paintColors) {
        inflater = LayoutInflater.from(context);
        this.paintColors = paintColors;
    }

    @Override
    public int getCount() {
        return paintColors == null ? 0 : paintColors.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(parent.getContext());
            imageView.setLayoutParams(new AbsListView.LayoutParams(PixelUtils.dp2px(32), PixelUtils.dp2px(32)));
            imageView.setPadding(PixelUtils.dp2px(8), PixelUtils.dp2px(8), PixelUtils.dp2px(8), PixelUtils.dp2px(8));

        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(paintColors[position]);

        if (selectPosition == position)
            imageView.setBackgroundResource(R.drawable.activity_draw_paint_select);
        else
            imageView.setBackgroundResource(0);

        return imageView;
    }
}
