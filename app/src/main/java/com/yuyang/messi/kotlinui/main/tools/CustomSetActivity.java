package com.yuyang.messi.kotlinui.main.tools;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.SuperscriptSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.yuyang.lib_base.gsonSerializer.ClassSerializer;
import com.yuyang.lib_base.recyclerview.item_decoration.GridItemDecoration;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.ui.header.HeaderRightBean;
import com.yuyang.lib_base.ui.header.HeaderRightItem;
import com.yuyang.lib_base.utils.CollectionUtil;
import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.lib_base.utils.RevealUtil;
import com.yuyang.messi.MessiApp;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.ModuleGroupBean;
import com.yuyang.messi.room.database.ModuleDatabase;
import com.yuyang.messi.room.entity.ModuleEntity;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.utils.SharedPreferencesUtil;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomSetActivity extends AppBaseActivity {

    public static final int CUSTOM_MAX = 15;

    private HeaderRightItem headerRightItem;

    private AppBarLayout appBarLayout;

    private CustomRecyclerAdapter topRecyclerAdapter;
    private CustomTouchHelper topTouchHelper;
    private TabLayout tabLayout;
    private RecyclerView bottomRecyclerView;
    private LinearLayoutManager bottomLayoutManager;
    private CustomSetBottomRecyclerAdapter bottomRecyclerAdapter;

    private boolean isUserScroll = false;

    private List<ModuleGroupBean> groupBeanList;
    private List<ModuleEntity> customBeanList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_custom_set;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        customBeanList = SharedPreferencesUtil.getModuleCustomOrder();
        initView();
        initEvent();
        initData();
        initTabLayout();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RevealUtil.onCreate(getActivity());
        }
    }

    @Override
    public void finish() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RevealUtil.finish(getActivity(), new RevealUtil.OnRevealListener() {
                @Override
                public boolean onRevealEnd() {
                    getWindow().getDecorView().setVisibility(View.INVISIBLE);
                    CustomSetActivity.super.finish();
                    overridePendingTransition(0, 0);
                    return true;
                }
            });
        } else {
            super.finish();
        }
    }

    protected void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle(null);
        List<HeaderRightBean> rightBeanList = new ArrayList<>();
        rightBeanList.add(new HeaderRightBean("编辑", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.equals(headerRightItem.getText(), "编辑")) {
                    headerRightItem.setText("完成");
                    topRecyclerAdapter.setEdit(true);
                    bottomRecyclerAdapter.setEdit(true);
                } else {
                    headerRightItem.setText("编辑");
                    topRecyclerAdapter.setEdit(false);
                    bottomRecyclerAdapter.setEdit(false);
                    SharedPreferencesUtil.setModuleCustomOrder(customBeanList);
                    setResult(RESULT_OK);
                }
            }
        }));
        headerRightItem = headerLayout.setRight(rightBeanList).get(0);

        appBarLayout = findViewById(R.id.activity_custom_set_appBarLyt);
        RecyclerView topRecyclerView = findViewById(R.id.activity_custom_set_topRecycler);
        tabLayout = findViewById(R.id.activity_custom_set_tabLayout);
        bottomRecyclerView = findViewById(R.id.activity_custom_set_bottomRecycler);

        topRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        topRecyclerView.addItemDecoration(new GridItemDecoration(ContextCompat.getDrawable(getActivity(), R.color.transparent),
                PixelUtils.dp2px(4),
                PixelUtils.dp2px(4),
                false));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(topTouchHelper = new CustomTouchHelper(true));
        topRecyclerView.setAdapter(topRecyclerAdapter = new CustomRecyclerAdapter(getActivity(), itemTouchHelper, true));
        itemTouchHelper.attachToRecyclerView(topRecyclerView);

        bottomRecyclerView.setLayoutManager(bottomLayoutManager = new LinearLayoutManager(getActivity()){
            @Override
            protected void calculateExtraLayoutSpace(@NonNull RecyclerView.State state, @NonNull int[] extraLayoutSpace) {
                super.calculateExtraLayoutSpace(state, extraLayoutSpace);
            }
        });
//        bottomRecyclerAdapter = new CustomSetBottomRecyclerAdapter(this, null);
        bottomRecyclerAdapter = new CustomSetBottomRecyclerAdapter(null);
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(bottomRecyclerAdapter);
        bottomRecyclerView.addItemDecoration(headersDecor);
        bottomRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });
        bottomRecyclerView.setAdapter(bottomRecyclerAdapter);
    }

    private void initEvent() {
        bottomRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                isUserScroll = newState != RecyclerView.SCROLL_STATE_IDLE;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int pos = bottomLayoutManager.findFirstVisibleItemPosition();
                if (pos != tabLayout.getSelectedTabPosition() && pos < tabLayout.getTabCount()) {
                    tabLayout.getTabAt(pos).select();
                }
            }
        });
        topRecyclerAdapter.setOnItemClickListener(new CustomRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ModuleEntity moduleEntity) {

            }

            @Override
            public void onDelete(ModuleEntity moduleEntity) {
                bottomRecyclerAdapter.notifyDataSetChanged();
            }
        });
        bottomRecyclerAdapter.setOnItemListener(new CustomSetBottomRecyclerAdapter.OnItemListener() {
            @Override
            public void onAdd(ModuleEntity moduleEntity) {
                customBeanList.add(moduleEntity);
                topRecyclerAdapter.notifyItemInserted(customBeanList.size() - 1);
            }

            @Override
            public void onDelete(ModuleEntity moduleEntity) {
                int index = customBeanList.indexOf(moduleEntity);
                customBeanList.remove(moduleEntity);
                topRecyclerAdapter.notifyItemRemoved(index);
            }
        });
    }

    private void initData() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassSerializer()).create();
        groupBeanList = gson.fromJson(new InputStreamReader(MessiApp.getInstance().getResources().openRawResource(R.raw.menu), StandardCharsets.UTF_8),
                new TypeToken<List<ModuleGroupBean>>() {
                }.getType());

        List<ModuleEntity> netModuleList = ModuleDatabase.getInstance().getModuleDao().getAllModule();
        if (!CollectionUtil.isEmpty(netModuleList)) {
            for (ModuleGroupBean moduleGroupBean : groupBeanList) {
                if ("网址".equals(moduleGroupBean.getGroupName())) {
                    moduleGroupBean.setItemList(moduleGroupBean.getItemList().subList(0, 2));
                    moduleGroupBean.getItemList().addAll(netModuleList);
                }
            }
        }

        ModuleDatabase.getInstance().getModuleDao().getAllModuleLive().observe(this, new Observer<List<ModuleEntity>>() {
            @Override
            public void onChanged(List<ModuleEntity> moduleEntities) {
                for (int i = 0; i < groupBeanList.size(); i++) {
                    ModuleGroupBean groupBean = groupBeanList.get(i);
                    if ("网址".equals(groupBean.getGroupName())) {
                        groupBean.setItemList(groupBean.getItemList().subList(0, 2));
                        groupBean.getItemList().addAll(moduleEntities);
                        bottomRecyclerAdapter.notifyItemChanged(i);
                    }
                }
            }
        });

        List<ModuleEntity> tempList = new ArrayList<>(customBeanList);

        for (ModuleGroupBean moduleGroupBean : groupBeanList) {
            for (com.yuyang.messi.room.entity.ModuleEntity moduleEntity : moduleGroupBean.getItemList()) {
                tempList.remove(moduleEntity);
            }
        }

        if (tempList.size() > 0) {
            customBeanList.removeAll(tempList);
            SharedPreferencesUtil.setModuleCustomOrder(customBeanList);
            setResult(RESULT_OK);
        }

        topRecyclerAdapter.setData(customBeanList);
        topTouchHelper.beanList = customBeanList;
        bottomRecyclerAdapter.setData(customBeanList, groupBeanList);
    }

    private void initTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView textView = (TextView) tab.getCustomView();
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                textView.setTextSize(17);

                if (isUserScroll) return;
                appBarLayout.setExpanded(false, true);
                bottomLayoutManager.scrollToPositionWithOffset(tab.getPosition(), 0);
                updateTabText(tabLayout.getSelectedTabPosition(), new Random().nextInt(200));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView textView = (TextView) tab.getCustomView();
                textView.setTypeface(Typeface.DEFAULT);
                textView.setTextSize(15);
//                        textView.setText(textView.getText().toString());//重置spannel
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.removeAllTabs();
        for (int i = 0; i < bottomRecyclerAdapter.getItemCount(); i++) {
            String tabTitle = bottomRecyclerAdapter.getItem(i).getGroupName();

            TabLayout.Tab newTab = tabLayout.newTab();
            TextView textView = new TextView(this);
            textView.setText(tabTitle);
            textView.setTextSize(15);
            textView.setGravity(Gravity.CENTER);
            textView.setSingleLine(true);
            newTab.setCustomView(textView);
            tabLayout.addTab(newTab);
        }
    }

    private void updateTabText(int index, int count) {
        TabLayout.Tab tab = tabLayout.getTabAt(index);
        if (tab == null) return;
        TabLayout.TabView tabView = tab.view;
        for (int i = 0; i < tabView.getChildCount(); i++) {
            if (tabView.getChildAt(i) instanceof TextView) {
                TextView textView = (TextView) tabView.getChildAt(i);
                if (count > 99) {
                    SpannableString spannableString = new SpannableString(String.format("%s(99+)", groupBeanList.get(index).getGroupName()));
                    SuperscriptSpan superscriptSpan = new SuperscriptSpan();
                    spannableString.setSpan(superscriptSpan, spannableString.length() - 2, spannableString.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new AbsoluteSizeSpan(12, true), spannableString.length() - 2, spannableString.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new ForegroundColorSpan(Color.RED), spannableString.length() - 2, spannableString.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textView.setText(spannableString);
                } else {
                    textView.setText(String.format("%s(%s)", groupBeanList.get(index).getGroupName(), count));
                }
            }
        }
    }
}
