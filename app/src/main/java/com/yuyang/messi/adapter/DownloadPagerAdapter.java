package com.yuyang.messi.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.yuyang.messi.ui.category.download.DownloadInstalledFragment;
import com.yuyang.messi.ui.category.download.DownloadManagerFragment;
import com.yuyang.messi.ui.category.download.DownloadUpdateFragment;
import java.util.List;

public class DownloadPagerAdapter extends FragmentPagerAdapter {

    private final List<String> mTitleList;
    private final List<Fragment> fragmentList;

    public DownloadPagerAdapter(List<String> titles, List<Fragment> fragmentList, FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mTitleList = titles;
        this.fragmentList = fragmentList;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitleList == null || position >= mTitleList.size()) {
            return super.getPageTitle(position);
        } else {
            if (position == 1 && DownloadManagerFragment.count > 0) {
                return mTitleList.get(position) + "(" + DownloadManagerFragment.count + ")";
            } else if (position == 2 && DownloadUpdateFragment.count > 0) {
                return mTitleList.get(position) + "(" + DownloadUpdateFragment.count + ")";
            } else if (position == 3 && DownloadInstalledFragment.count > 0) {
                return mTitleList.get(position) + "(" + DownloadInstalledFragment.count + ")";
            }
            return mTitleList.get(position);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mTitleList.size();
    }

}
