package com.yuyang.messi.ui.douban.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.yuyang.messi.ui.common.MyPagerAdapter;
import com.yuyang.messi.view.LivingTabLayout;

import java.util.List;

public class DoubanPagerAdapter extends MyPagerAdapter implements LivingTabLayout.DrawableResIconAdapter{

    private List<Integer> drawableResList;

    public DoubanPagerAdapter(FragmentManager fm, List<String> titleList, List<Fragment> fragmentList, List<Integer> drawableResList) {
        super(fm, titleList, fragmentList);
        this.drawableResList = drawableResList;
    }

    @Override
    public int getIcon(int position) {
        return drawableResList.get(position);
    }
}
