package com.yuyang.messi.ui.football;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

import java.util.List;

public class FootballActivity extends AppBaseActivity {

    public static List<String> TitleList = CommonUtil.asList("中超", "英超", "西甲", "意甲", "德甲", "法甲");

    @Override
    protected int getLayoutId() {
        return R.layout.activity_football;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            Constraints myConstraints = new Constraints.Builder()
////                    .setRequiredNetworkType(NetworkType.CONNECTED)  // 网络状态
////                    .setRequiresBatteryNotLow(true)                 // 不在电量不足时执行
////                    .setRequiresCharging(true)                      // 在充电时执行
////                    .setRequiresStorageNotLow(true)                 // 不在存储容量不足时执行
////                    .setRequiresDeviceIdle(true)                    // 在待机状态下执行，需要 API 23
//                    .setTriggerContentMaxDelay(20, TimeUnit.SECONDS)
//                    .build();
//            OneTimeWorkRequest testWork = new OneTimeWorkRequest.Builder(TestWorker.class)
//                    .setConstraints(myConstraints)
//                    .build();
//            WorkManager.getInstance().enqueue(testWork);
//        }
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("懂球帝");

        TabLayout tabLayout = findViewById(R.id.activity_football_tabLayout);
        ViewPager viewPager = findViewById(R.id.activity_football_viewpager);
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView textView = (TextView) tab.getCustomView();
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                textView.setTextSize(17);
                textView.setText(textView.getText().toString());//重置spannel
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView textView = (TextView) tab.getCustomView();
                textView.setTypeface(Typeface.DEFAULT);
                textView.setTextSize(15);
                textView.setText(textView.getText().toString());//重置spannel
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.removeAllTabs();
        for (int i = 0; i < TitleList.size(); i++) {

            String title = TitleList.get(i);

            TabLayout.Tab newTab = tabLayout.newTab();

            TextView textView = new TextView(this);
            textView.setText(title);
            textView.setGravity(Gravity.CENTER);
            newTab.setCustomView(textView);

            tabLayout.addTab(newTab);
        }
    }

    private static class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putString(FootballFragment.TITLE, TitleList.get(position));
            FootballFragment footballFragment = new FootballFragment();
            footballFragment.setArguments(bundle);
            return footballFragment;
        }

        @Override
        public int getCount() {
            return TitleList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TitleList.get(position);
        }
    }

}
