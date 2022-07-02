package com.yuyang.messi.ui.category.download;

import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.yuyang.lib_base.recyclerview.item_decoration.LinearItemDecoration;
import com.yuyang.lib_base.ui.base.BaseFragment;
import com.yuyang.lib_base.utils.AppUtils;
import com.yuyang.messi.R;
import com.yuyang.messi.adapter.DownloadListRecyclerAdapter;
import com.yuyang.messi.bean.AppBean;
import com.yuyang.messi.download.FileDownloadListener;
import com.yuyang.messi.download.FileDownloadManager;
import com.yuyang.messi.download.FileDownloadModel;
import com.yuyang.messi.event.DownloadConnectedEvent;
import com.yuyang.messi.event.DownloadEvent;
import com.yuyang.messi.event.DownloadUpdateUIEvent;
import com.yuyang.messi.utils.IntentUtil;
import com.yuyang.lib_base.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class DownloadListFragment extends BaseFragment {

    private XRecyclerView recyclerView;
    private DownloadListRecyclerAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_download;
    }

    @Override
    protected void doOnViewCreated() {
        initViews();
        initEvents();
        loadData();
        FileDownloadManager.getInstance().onCreate(new WeakReference<>(getContext()));
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        FileDownloadManager.getInstance().onDestroy();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void initViews() {
        recyclerView = $(R.id.fragment_download_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new LinearItemDecoration(getActivity()));
        recyclerView.setAdapter(adapter = new DownloadListRecyclerAdapter(getActivity()));
        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setLoadingMoreEnabled(false);
    }

    private void initEvents() {

        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
            }
        });

        adapter.setOnItemClickListener(new DownloadListRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onButtonClick(int position) {
                int dataPosition = position - 1;
                int viewPosition = position;

                DownloadListRecyclerAdapter.MyHolder holder = (DownloadListRecyclerAdapter.MyHolder) recyclerView.findViewHolderForLayoutPosition(viewPosition);
                String action = holder.actionText.getText().toString();

                if (action.equals(getContext().getString(R.string.download_open))) {
                    AppUtils.launchApp(getActivity(), adapter.beanList.get(dataPosition).getAppPackageName());
                } else if (action.equals(getContext().getString(R.string.download_start)) ||
                        action.equals(getContext().getString(R.string.download_continue))) {
                    // to start
                    FileDownloadModel model = new FileDownloadModel();
                    model.setId(adapter.beanList.get(dataPosition).getDownloadId());
                    model.setUrl(adapter.beanList.get(dataPosition).getDownloadUrl());
                    model.setIcon(adapter.beanList.get(dataPosition).getAppIcon());
                    model.setName(adapter.beanList.get(dataPosition).getAppName());
                    model.setPackageName(adapter.beanList.get(dataPosition).getAppPackageName());
                    model.setVersion(adapter.beanList.get(dataPosition).getAppVersion());
                    model.setPath(adapter.beanList.get(dataPosition).getPath());

                    model = FileDownloadManager.getInstance().addTask(model);
                    if (model != null) {
                        final BaseDownloadTask task = FileDownloader.getImpl().create(model.getUrl())
                                .setPath(model.getPath())
                                .setCallbackProgressTimes(1000)
                                .setMinIntervalUpdateSpeed(1000)
                                .setAutoRetryTimes(1)
                                .setListener(FileDownloadListener.getInstance());

                        task.start();
                        EventBus.getDefault().post(new DownloadUpdateUIEvent());
                    }

                } else if (action.equals(getContext().getString(R.string.download_pause))) {
                    // to pause
                    FileDownloader.getImpl().pause(adapter.beanList.get(dataPosition).getDownloadId());
                } else if (action.equals(getContext().getString(R.string.download_install))) {
                    IntentUtil.installApk(getActivity(), new File(adapter.beanList.get(dataPosition).getPath()));
                }
            }

            @Override
            public void onItemClick(int position) {
                int dataPosition = position - 1;
                int viewPosition = position;

                ToastUtil.showToast(position + " click");
            }

            @Override
            public void onItemLongClick(int position) {
                final int dataPosition = position - 1;
                final int viewPosition = position;

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Delete");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DownloadListRecyclerAdapter.MyHolder holder = (DownloadListRecyclerAdapter.MyHolder) recyclerView.findViewHolderForLayoutPosition(viewPosition);

                        FileDownloadModel model = new FileDownloadModel();
                        model.setId(adapter.beanList.get(dataPosition).getDownloadId());
                        model.setUrl(adapter.beanList.get(dataPosition).getDownloadUrl());
                        model.setIcon(adapter.beanList.get(dataPosition).getAppIcon());
                        model.setName(adapter.beanList.get(dataPosition).getAppName());
                        model.setPackageName(adapter.beanList.get(dataPosition).getAppPackageName());
                        model.setVersion(adapter.beanList.get(dataPosition).getAppVersion());
                        model.setPath(adapter.beanList.get(dataPosition).getPath());

                        FileDownloadManager.getInstance().delete(model);
                        holder.updateNotBeginDownload(model.getPackageName(), model.getVersion());
                        dialog.dismiss();
                        EventBus.getDefault().post(new DownloadUpdateUIEvent());
                    }
                });
                builder.setCancelable(true);
                builder.show();
            }
        });
    }

    private void loadData() {
        List<AppBean> appBeanList = new ArrayList<>();
        AppBean appBean;

        appBean = new AppBean();
        appBean.setDownloadUrl("http://gdown.baidu.com/data/wisegame/2d5bf81de4e0ca42/weixin_1041.apk");
        appBean.setAppName("微信");
        appBean.setAppVersion("6.5.7");
        appBean.setAppPackageName("com.tencent.mm");
        appBean.setAppIcon("https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=3088020551,4198140884&fm=96");
        appBeanList.add(appBean);

        appBean = new AppBean();
        appBean.setDownloadUrl("http://gdown.baidu.com/data/wisegame/85c24c6b91b59f5e/wangyiyunyinle_93.apk");
        appBean.setAppName("网易云音乐");
        appBean.setAppVersion("4.0.2");
        appBean.setAppPackageName("com.netease.cloudmusic");
        appBean.setAppIcon("http://static.bbs.nubia.cn/forum/201603/12/171540wjdpmd3e57d0eszp.png.thumb.jpg");
        appBeanList.add(appBean);

        convertData(appBeanList);
        adapter.beanList.addAll(appBeanList);
        adapter.notifyDataSetChanged();
    }

    private void convertData(List<AppBean> gameBeanList) {
        for (int i = 0; i < gameBeanList.size(); i++) {
            FileDownloadModel searchModel = FileDownloadManager.getInstance().getByUrl(gameBeanList.get(i).getDownloadUrl());
            if (searchModel != null) {
                gameBeanList.get(i).setDownloadId(searchModel.getId());
                gameBeanList.get(i).setPath(searchModel.getPath());
            } else {
                gameBeanList.get(i).setPath(FileDownloadManager.getInstance().createPath(gameBeanList.get(i).getAppName(), gameBeanList.get(i).getDownloadUrl()));
                gameBeanList.get(i).setDownloadId(FileDownloadUtils.generateId(gameBeanList.get(i).getDownloadUrl(), gameBeanList.get(i).getPath()));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    public void onDownloadEvent(DownloadConnectedEvent event) {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    public void onDownloadEvent(DownloadUpdateUIEvent event) {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    public void onDownloadEvent(DownloadEvent event) {

        DownloadListRecyclerAdapter.MyHolder holder = checkCurrentHolder(event.id);
        if (holder == null) return;

        switch (event.status) {
            case FileDownloadStatus.pending:
                holder.updateDownloading(FileDownloadStatus.pending, event.soFarBytes, event.totalBytes, 0);
                break;
            case FileDownloadStatus.started:
                holder.statusText.setText(R.string.download_started);
                break;
            case FileDownloadStatus.connected:
                holder.updateDownloading(FileDownloadStatus.connected, event.soFarBytes, event.totalBytes, 0);
                break;
            case FileDownloadStatus.progress:
                holder.updateDownloading(FileDownloadStatus.progress, event.soFarBytes, event.totalBytes, event.speed);
                break;
            case FileDownloadStatus.error:
                holder.updateNotDownloaded(FileDownloadStatus.error, event.soFarBytes, event.totalBytes);
                break;
            case FileDownloadStatus.paused:
                holder.updateNotDownloaded(FileDownloadStatus.paused, event.soFarBytes, event.totalBytes);
                break;
            case FileDownloadStatus.completed:
                holder.updateDownloaded();
                EventBus.getDefault().post(new DownloadUpdateUIEvent());
                break;
        }
    }

    private DownloadListRecyclerAdapter.MyHolder checkCurrentHolder(final int downloadId) {

        AppBean appBean = new AppBean();
        appBean.setDownloadId(downloadId);
        int position = adapter.beanList.indexOf(appBean);
        if (position == -1) return null;

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int first = layoutManager.findFirstVisibleItemPosition();
        int last = layoutManager.findLastVisibleItemPosition();
        if (first <= position && position <= last) {
            return (DownloadListRecyclerAdapter.MyHolder) recyclerView.findViewHolderForLayoutPosition(position);
        }
        return null;
    }
}
