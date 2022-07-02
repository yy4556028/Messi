package com.yuyang.messi.adapter;

import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.yuyang.messi.view.DragGridView;

import java.util.List;

public class DragGridViewPagerAdapter extends PagerAdapter {

    private List<DragGridView> myPagerList;

    public DragGridViewPagerAdapter(List<DragGridView> myPagerList) {
        this.myPagerList = myPagerList;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (position < myPagerList.size())
            container.removeView(myPagerList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(myPagerList.get(position), 0);
        return myPagerList.get(position);
    }

    @Override
    public int getCount() {
        return myPagerList == null ? 0 : myPagerList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }
}
