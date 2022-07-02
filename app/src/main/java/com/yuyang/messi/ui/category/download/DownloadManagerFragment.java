package com.yuyang.messi.ui.category.download;

import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.yuyang.lib_base.ui.base.BaseFragment;
import com.yuyang.lib_base.utils.AppUtils;
import com.yuyang.messi.R;
import com.yuyang.messi.adapter.DownloadManagerRecyclerAdapter;
import com.yuyang.messi.bean.AppBean;
import com.yuyang.messi.download.FileDownloadManager;
import com.yuyang.messi.download.FileDownloadModel;
import com.yuyang.messi.event.DownloadConnectedEvent;
import com.yuyang.messi.event.DownloadEvent;
import com.yuyang.messi.event.DownloadUpdateUIEvent;
import com.yuyang.messi.event.MyGameTabCountEvent;
import com.yuyang.messi.ui.category.DownloadActivity;
import com.yuyang.messi.utils.IntentUtil;
import com.yuyang.lib_base.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DownloadManagerFragment extends BaseFragment {

    private XRecyclerView recyclerView;
    private DownloadManagerRecyclerAdapter adapter;

    private List<FileDownloadModel> modelList;

    public static int count;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_download;
    }

    @Override
    protected void doOnViewCreated() {
        initViews();
        initEvents();
        initData();
        FileDownloadManager.getInstance().onCreate(new WeakReference<>(getContext()));
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
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
        recyclerView.setAdapter(adapter = new DownloadManagerRecyclerAdapter(getActivity()));
        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setLoadingMoreEnabled(false);
    }

    private void initEvents() {

        adapter.setOnItemClickListener(new DownloadManagerRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onButtonClick(int position) {
                int dataPosition = position - 1;
                int viewPosition = position;

                DownloadManagerRecyclerAdapter.MyHolder holder = (DownloadManagerRecyclerAdapter.MyHolder) recyclerView.findViewHolderForLayoutPosition(viewPosition);
                CharSequence action = holder.button.getText();

                if (action.equals(getContext().getString(R.string.download_open))) {
                    AppUtils.launchApp(getActivity(), adapter.modelList.get(dataPosition).getPackageName());
                } else if (action.equals(getContext().getString(R.string.download_start)) ||
                        action.equals(getContext().getString(R.string.download_continue))) {
                    // to start
                    FileDownloadModel model = FileDownloadManager.getInstance().addTask(adapter.modelList.get(dataPosition));
                    if (model != null) {
//                        final BaseDownloadTask task = FileDownloader.getImpl().create(model.getUrl())
//                                .setPath(model.getPath())
//                                .setCallbackProgressTimes(1000)
//                                .setMinIntervalUpdateSpeed(1000)
//                                .setAutoRetryTimes(1)
//                                .setListener(FileDownloadListener.getAppContext())
//                                .setListener();

//                        task.start();
//                        EventBus.getDefault().post(new DownloadUpdateUIEvent());
                    }

                } else if (action.equals(getContext().getString(R.string.download_pause))) {
                    // to pause
                    FileDownloader.getImpl().pause(modelList.get(dataPosition).getId());
                } else if (action.equals(getContext().getString(R.string.download_install))) {
                    IntentUtil.installApk(getActivity(), new File(adapter.modelList.get(dataPosition).getPath()));

                    AppBean appBean = new AppBean();
                    appBean.setAppName(adapter.modelList.get(dataPosition).getName());
                    appBean.setAppIcon(adapter.modelList.get(dataPosition).getIcon());
                    appBean.setAppPackageName(adapter.modelList.get(dataPosition).getPackageName());
                    appBean.setPath(adapter.modelList.get(dataPosition).getPath());
                    appBean.setDownloadUrl(adapter.modelList.get(dataPosition).getUrl());
                    appBean.setDownloadId(adapter.modelList.get(dataPosition).getId());
                    appBean.setAppVersion(adapter.modelList.get(dataPosition).getVersion());

                    for (AppBean bean : DownloadActivity.gameBeanList) {
                        if (bean.getAppPackageName().equalsIgnoreCase(appBean.getAppPackageName())) {
                            return;
                        }
                    }
                    DownloadActivity.gameBeanList.add(appBean);
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
                        DownloadManagerRecyclerAdapter.MyHolder holder = (DownloadManagerRecyclerAdapter.MyHolder) recyclerView.findViewHolderForLayoutPosition(viewPosition);

                        FileDownloadManager.getInstance().delete(adapter.modelList.get(dataPosition));
                        holder.updateNotBeginDownload(adapter.modelList.get(dataPosition).getPackageName(), adapter.modelList.get(dataPosition).getVersion());
                        EventBus.getDefault().post(new DownloadUpdateUIEvent());
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(true);
                builder.show();
            }
        });
    }

    private void initData() {

        if (adapter == null) return;

        modelList = FileDownloadManager.getInstance().getAll();

        Collections.sort(modelList, new Comparator<FileDownloadModel>() {
            @Override
            public int compare(FileDownloadModel lhs, FileDownloadModel rhs) {
                final int lStatus = FileDownloadManager.getInstance().getStatus(lhs.getId(), lhs.getPath());
                final int rStatus = FileDownloadManager.getInstance().getStatus(rhs.getId(), rhs.getPath());
                if (lStatus != FileDownloadStatus.completed && rStatus == FileDownloadStatus.completed) {
                    return -1;
                } else if (lStatus == FileDownloadStatus.completed && rStatus != FileDownloadStatus.completed) {
                    return 1;
                }
                return lhs.getName().compareTo(rhs.getName());
            }
        });

        adapter.updateData(modelList);
        count = adapter.getItemCount();
        EventBus.getDefault().post(new MyGameTabCountEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    public void onDownloadEvent(DownloadConnectedEvent event) {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    public void onDownloadEvent(DownloadUpdateUIEvent event) {
        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    public void onDownloadEvent(DownloadEvent event) {

        DownloadManagerRecyclerAdapter.MyHolder holder = checkCurrentHolder(event.id);
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
                break;
        }
    }

    private DownloadManagerRecyclerAdapter.MyHolder checkCurrentHolder(final int downloadId) {

        FileDownloadModel fileDownloadModel = new FileDownloadModel();
        fileDownloadModel.setId(downloadId);
        int position = modelList.indexOf(fileDownloadModel);
        if (position == -1) return null;

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int first = layoutManager.findFirstVisibleItemPosition();
        int last = layoutManager.findLastVisibleItemPosition();
        if (first <= position && position <= last) {
            return (DownloadManagerRecyclerAdapter.MyHolder) recyclerView.findViewHolderForLayoutPosition(position);
        }
        return null;
    }
}
