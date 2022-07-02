package com.yuyang.messi.ui.category.download;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.yuyang.lib_base.ui.base.BaseFragment;
import com.yuyang.messi.R;
import com.yuyang.messi.adapter.DownloadInstalledAdapter;
import com.yuyang.messi.bean.AppBean;
import com.yuyang.messi.event.MyGameTabCountEvent;
import com.yuyang.messi.ui.category.DownloadActivity;
import com.yuyang.lib_base.utils.AppInfoUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DownloadInstalledFragment extends BaseFragment {

    private XRecyclerView recyclerView;
    private DownloadInstalledAdapter adapter;
    public static int count;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_download;
    }

    @Override
    protected void doOnViewCreated() {
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            updateData();
        }
    }

    private void initView() {
        recyclerView = $(R.id.fragment_download_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter = new DownloadInstalledAdapter(getActivity()));
        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setLoadingMoreEnabled(false);
    }

    public void updateData() {

        if (adapter == null) return;

        List<AppBean> gameBeanList = new ArrayList<>();
        if (DownloadActivity.gameBeanList != null) {
            for (AppBean appBean : DownloadActivity.gameBeanList) {
                if (AppInfoUtil.isApkInstalled(getContext(), appBean.getAppPackageName())) {
                    gameBeanList.add(appBean);
                }
            }
        }

        Collections.sort(gameBeanList, new Comparator<AppBean>() {
            @Override
            public int compare(AppBean lhs, AppBean rhs) {
                return lhs.getAppName().compareTo(rhs.getAppName());
            }
        });

        adapter.updateData(gameBeanList);
        count = adapter.getItemCount();
        EventBus.getDefault().post(new MyGameTabCountEvent());
    }

}
