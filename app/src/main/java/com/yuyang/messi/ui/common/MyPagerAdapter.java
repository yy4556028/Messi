package com.yuyang.messi.ui.common;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class MyPagerAdapter extends FragmentPagerAdapter {

    private List<String> titleList;
    private List<Fragment> fragmentList;

    public MyPagerAdapter(FragmentManager fm, List<String> titleList, List<Fragment> fragmentList) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.titleList = titleList;
        this.fragmentList = fragmentList;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList == null ? 0 : fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titleList != null && position < titleList.size()) {
            return titleList.get(position);
        } else {
            return super.getPageTitle(position);
        }
    }
}