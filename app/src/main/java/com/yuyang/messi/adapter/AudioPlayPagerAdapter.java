package com.yuyang.messi.adapter;

import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class AudioPlayPagerAdapter extends PagerAdapter {

    private ArrayList<View> views;

    public AudioPlayPagerAdapter(ArrayList<View> views) {
        this.views = views;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (position < views.size())
            container.removeView(views.get(position));// 删除页卡
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) { // 这个方法用来实例化页卡
        container.addView(views.get(position), 0);// 添加页卡
        return views.get(position);
    }

    @Override
    public int getCount() {
        return views == null ? 0 : views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
