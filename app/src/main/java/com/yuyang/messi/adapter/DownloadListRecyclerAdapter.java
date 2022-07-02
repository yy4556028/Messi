package com.yuyang.messi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.magicprogresswidget.MagicProgressBar;
import com.yuyang.lib_base.utils.AppInfoUtil;
import com.yuyang.lib_base.utils.FileUtil;
import com.yuyang.messi.MessiApp;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.AppBean;
import com.yuyang.messi.download.FileDownloadManager;
import com.yuyang.messi.download.FileDownloadModel;

import java.util.ArrayList;
import java.util.List;

public class DownloadListRecyclerAdapter extends RecyclerView.Adapter<DownloadListRecyclerAdapter.MyHolder> {

    private LayoutInflater mInflater;
    private Context context;

    public List<AppBean> beanList = new ArrayList<>();

    public DownloadListRecyclerAdapter(Context context) {
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void remove(int position) {
        if (this.beanList != null && position < this.beanList.size()) {
            this.beanList.remove(position);
            notifyDataSetChanged();
        }
    }

    public void remove(FileDownloadModel model) {
        if (beanList != null && beanList.contains(model)) {
            beanList.remove(model);
            notifyDataSetChanged();
        }
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int i) {
        return new MyHolder(mInflater.inflate(R.layout.fragment_download_recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {

        final AppBean appBean = beanList.get(position);

        Glide.with(context)
                .load(appBean.getAppIcon())
                .into(holder.icon);
        holder.title.setText(appBean.getAppName());

        // 如果已经安装 且 是最新版本
        if (AppInfoUtil.isApkInstalled(context, appBean.getAppPackageName()) &&
                !AppInfoUtil.isVersionOutdated(AppInfoUtil.getAppVersionName(), appBean.getAppVersion())) {
            // 显示 打开应用

            holder.notDownloadLyt.setVisibility(View.VISIBLE);
            holder.downloadLyt.setVisibility(View.INVISIBLE);

            holder.actionText.setEnabled(true);
            holder.actionText.setText(R.string.download_open);
            holder.statusText.setText("");
            holder.progressBar.setPercent(0);
            holder.speedText.setText("");
            holder.size.setText("");

            // 否则 需要判断是否初始化好 如果没有 显示loading
        } else if (FileDownloadManager.getInstance().isReady()) {

            holder.actionText.setEnabled(true);

            final int status = FileDownloadManager.getInstance().getStatus(appBean.getDownloadId(), appBean.getPath());

            if (!FileDownloadManager.getInstance().isExistModel(appBean.getDownloadUrl())) {

                holder.updateNotBeginDownload(appBean.getAppPackageName(), appBean.getAppVersion());

            } else if (status == FileDownloadStatus.pending ||
                    status == FileDownloadStatus.started ||
                    status == FileDownloadStatus.connected ||
                    status == FileDownloadStatus.progress) {

                holder.updateDownloading(status,
                        FileDownloadManager.getInstance().getSoFar(appBean.getDownloadId()),
                        FileDownloadManager.getInstance().getTotal(appBean.getDownloadId()), 0);

            } else if (status == FileDownloadStatus.completed) {
                holder.updateDownloaded();
            } else {
                // not start
                holder.updateNotDownloaded(status,
                        FileDownloadManager.getInstance().getSoFar(appBean.getDownloadId()),
                        FileDownloadManager.getInstance().getTotal(appBean.getDownloadId()));
            }
        } else {
            holder.statusText.setText(R.string.download_loading);
            holder.actionText.setEnabled(false);
        }

        holder.actionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onButtonClick(holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemLongClick(holder.getAdapterPosition());
                return true;
            }
        });
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return beanList == null ? 0 : beanList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        public void updateNotBeginDownload(String packageName, String version) {
            if (AppInfoUtil.isApkInstalled(MessiApp.getInstance(), packageName) &&
                    AppInfoUtil.isVersionOutdated(AppInfoUtil.getAppVersionName(), version)) {
                notDownloadLyt.setVisibility(View.VISIBLE);
                downloadLyt.setVisibility(View.INVISIBLE);
                actionText.setText(R.string.download_update);
            } else {
                notDownloadLyt.setVisibility(View.VISIBLE);
                downloadLyt.setVisibility(View.INVISIBLE);
                actionText.setText(R.string.download_start);
            }
        }

        public void updateDownloaded() {
            notDownloadLyt.setVisibility(View.VISIBLE);
            downloadLyt.setVisibility(View.INVISIBLE);
            progressBar.setPercent(1);
            statusText.setText(R.string.download_complete);
            actionText.setText(R.string.download_install);
            speedText.setText("");
            size.setText("");
        }

        public void updateNotDownloaded(final int status, final long sofar, final long total) {

            notDownloadLyt.setVisibility(View.INVISIBLE);
            downloadLyt.setVisibility(View.VISIBLE);
            final float percent = sofar / (float) total;
            progressBar.setPercent(percent);
            actionText.setText(R.string.download_continue);

            switch (status) {
                case FileDownloadStatus.error:
                    statusText.setText(R.string.download_error);
                    break;
                case FileDownloadStatus.paused:
                    statusText.setText(R.string.download_paused);
                    break;
                default:
                    statusText.setText(R.string.download_not_download);
                    break;
            }
            size.setText(FileUtil.formatSize(sofar, total));
            speedText.setText("");
        }

        public void updateDownloading(final int status, final long sofar, final long total, int speed) {

            notDownloadLyt.setVisibility(View.INVISIBLE);
            downloadLyt.setVisibility(View.VISIBLE);

            final float percent = sofar / (float) total;
            progressBar.setPercent(percent);

            switch (status) {
                case FileDownloadStatus.pending:
                    statusText.setText("队列中");
                    break;
                case FileDownloadStatus.started:
                    statusText.setText(R.string.download_started);
                    break;
                case FileDownloadStatus.connected:
                    statusText.setText(R.string.download_connected);
                    break;
                case FileDownloadStatus.progress:
                    statusText.setText(R.string.download_downloading);
                    break;
                default:
                    statusText.setText("status" + status);
                    break;
            }

            size.setText(FileUtil.formatSize(sofar, total));
            speedText.setText(speed == 0 ? "" : speed + "KB/s");
            actionText.setText(R.string.download_pause);
        }

        public ImageView icon;
        public TextView actionText;
        public TextView title;
        public TextView subtitle;

        public LinearLayout notDownloadLyt;
        public TextView tag0;
        public TextView tag1;

        public RelativeLayout downloadLyt;

        public TextView statusText;
        public MagicProgressBar progressBar;
        public TextView size;
        public TextView speedText;


        public MyHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.fragment_download_recycler_item_image);
            actionText = itemView.findViewById(R.id.fragment_download_recycler_item_action);
            title = itemView.findViewById(R.id.fragment_download_recycler_item_title);
            subtitle = itemView.findViewById(R.id.fragment_download_recycler_item_subtitle);

            notDownloadLyt = itemView.findViewById(R.id.fragment_download_recycler_item_not_download_lyt);
            tag0 = itemView.findViewById(R.id.fragment_download_recycler_item_tag0);
            tag1 = itemView.findViewById(R.id.fragment_download_recycler_item_tag1);

            downloadLyt = itemView.findViewById(R.id.fragment_download_recycler_item_download_lyt);
            statusText = itemView.findViewById(R.id.fragment_download_recycler_item_state);
            size = itemView.findViewById(R.id.fragment_download_recycler_item_size);
            speedText = itemView.findViewById(R.id.fragment_download_recycler_item_speed);
            progressBar =  itemView.findViewById(R.id.fragment_download_recycler_item_progress);
        }
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onButtonClick(int position);

        void onItemClick(int position);

        void onItemLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

}



