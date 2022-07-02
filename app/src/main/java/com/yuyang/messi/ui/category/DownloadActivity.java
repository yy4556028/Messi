package com.yuyang.messi.ui.category;

import android.content.pm.ApplicationInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.adapter.DownloadPagerAdapter;
import com.yuyang.messi.bean.AppBean;
import com.yuyang.messi.event.MyGameTabCountEvent;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.ui.category.download.DownloadInstalledFragment;
import com.yuyang.messi.ui.category.download.DownloadListFragment;
import com.yuyang.messi.ui.category.download.DownloadManagerFragment;
import com.yuyang.messi.ui.category.download.DownloadUpdateFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * https://github.com/lingochamp/FileDownloader
 */
public class DownloadActivity extends AppBaseActivity {

    public static final String PAGER_INDEX = "pagerIndex";
    private int pagerIndex;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static List<AppBean> gameBeanList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_download;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pagerIndex = getIntent().getIntExtra(PAGER_INDEX, 0);
        initViews();
        loadMyGame();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void initViews() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("下载");

        tabLayout = findViewById(R.id.activity_download_tabLayout);
        viewPager = findViewById(R.id.activity_download_viewpager);

        List<String> titles = new ArrayList<>();
        titles.add("下载列表");
        titles.add("下载管理");
        titles.add("更新");
        titles.add("已安装");
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new DownloadListFragment());
        fragmentList.add(new DownloadManagerFragment());
        fragmentList.add(new DownloadUpdateFragment());
        fragmentList.add(new DownloadInstalledFragment());
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TabLayout.TabView tabView = tab.view;
                for (int i = 0; i < tabView.getChildCount(); i++) {
                    if (tabView.getChildAt(i) instanceof TextView) {
                        TextView textView = (TextView) tabView.getChildAt(i);
                        textView.setTypeface(Typeface.DEFAULT_BOLD);
                        textView.setTextSize(17);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TabLayout.TabView tabView = tab.view;
                for (int i = 0; i < tabView.getChildCount(); i++) {
                    if (tabView.getChildAt(i) instanceof TextView) {
                        TextView textView = (TextView) tabView.getChildAt(i);
                        textView.setTypeface(Typeface.DEFAULT);
                        textView.setTextSize(15);
                    }
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setAdapter(new DownloadPagerAdapter(titles, fragmentList, getSupportFragmentManager()));
        viewPager.setCurrentItem(pagerIndex);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void loadMyGame() {
        List<ApplicationInfo> applicationInfoList = getActivity().getPackageManager().getInstalledApplications(0);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < applicationInfoList.size(); i++) {
            if (i == applicationInfoList.size() - 1) {
                sb.append(applicationInfoList.get(i).packageName);
            } else {
                sb.append(applicationInfoList.get(i).packageName + ",");
            }
        }

//        AsyncHttpUtil.loadMyGame(sb.toString(), new AsyncResponseHandler() {
//            @Override
//            public void handle(int responseCode, JSONObject error, JSONObject response) throws JSONException {
//                if (AsyncHttpErrorUtil.isErrorFree(responseCode, error, response)) {
//                    gameBeanList = FastJsonTool.getList(response.getString(NetworkConstants.BACKEND_RESPONSE_CODE), AppBean.class);
//                } else {
//                    AsyncHttpErrorUtil.showNetworkOrBackendError(responseCode, error, response);
//                }
//            }
//        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    public void onEvent(final MyGameTabCountEvent event) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setText(viewPager.getAdapter().getPageTitle(i));
            }
        }
    }
}
