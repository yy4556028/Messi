package com.yuyang.messi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.magicprogresswidget.MagicProgressBar;
import com.yuyang.lib_base.utils.AppInfoUtil;
import com.yuyang.lib_base.utils.FileUtil;
import com.yuyang.messi.MessiApp;
import com.yuyang.messi.R;
import com.yuyang.messi.download.FileDownloadManager;
import com.yuyang.messi.download.FileDownloadModel;

import java.util.ArrayList;
import java.util.List;

public class DownloadManagerRecyclerAdapter extends RecyclerView.Adapter<DownloadManagerRecyclerAdapter.MyHolder> {

    private LayoutInflater mInflater;
    private Context context;

    public List<FileDownloadModel> modelList;

    public DownloadManagerRecyclerAdapter(Context context) {
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateData(List<FileDownloadModel> modelList) {
        if (this.modelList == null)
            this.modelList = new ArrayList<>();
        this.modelList.clear();
        this.modelList.addAll(modelList);
        notifyDataSetChanged();
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int i) {
        return new MyHolder(mInflater.inflate(R.layout.activity_download_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {

        FileDownloadModel model = modelList.get(position);

        Glide.with(context)
                .load(model.getIcon())
                .into(holder.icon);
        holder.title.setText(model.getName());

        // 如果已经安装 且 是最新版本
        if (AppInfoUtil.isApkInstalled(context, model.getPackageName()) &&
                !AppInfoUtil.isVersionOutdated(AppInfoUtil.getAppVersionName(), model.getVersion())) {
            // 显示 打开应用
            holder.button.setEnabled(true);
            holder.button.setText(R.string.download_open);
            holder.statusText.setText("");
            holder.progressBar.setPercent(0);
            holder.speedText.setText("");
            holder.size.setText("");

            // 否则 需要判断是否初始化好 如果没有 显示loading
        } else if (FileDownloadManager.getInstance().isReady()) {

            holder.button.setEnabled(true);

            final int status = FileDownloadManager.getInstance().getStatus(model.getId(), model.getPath());

            if (!FileDownloadManager.getInstance().isExistModel(model.getUrl())) {

                holder.updateNotBeginDownload(model.getPackageName(), model.getVersion());

            } else if (status == FileDownloadStatus.pending ||
                    status == FileDownloadStatus.started ||
                    status == FileDownloadStatus.connected ||
                    status == FileDownloadStatus.progress) {

                holder.updateDownloading(status, FileDownloadManager.getInstance().getSoFar(model.getId()), FileDownloadManager.getInstance().getTotal(model.getId()), 0);

            } else if (status == FileDownloadStatus.completed) {
                holder.updateDownloaded();
            } else {
                // not start
                holder.updateNotDownloaded(status, FileDownloadManager.getInstance().getSoFar(model.getId()), FileDownloadManager.getInstance().getTotal(model.getId()));
            }
        } else {
            holder.statusText.setText(R.string.download_loading);
            holder.button.setEnabled(false);
        }

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onButtonClick(holder.getAdapterPosition() - 1);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(holder.getAdapterPosition() - 1);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemLongClick(holder.getAdapterPosition() - 1);
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
        return modelList == null ? 0 : modelList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        public void updateNotBeginDownload(String packageName, String version) {

            if (AppInfoUtil.isApkInstalled(MessiApp.getInstance(), packageName) &&
                    AppInfoUtil.isVersionOutdated(AppInfoUtil.getAppVersionName(), version)) {
                button.setText(R.string.download_update);
            } else {
                button.setText(R.string.download_start);
            }
        }

        public void updateDownloaded() {
            progressBar.setPercent(1);
            statusText.setText(R.string.download_complete);
            button.setText(R.string.download_install);
            speedText.setText("");
            size.setText("");
        }

        public void updateNotDownloaded(final int status, final long sofar, final long total) {
            final float percent = sofar / (float) total;
            progressBar.setPercent(percent);
            button.setText(R.string.download_continue);

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

            final float percent = sofar / (float) total;
            progressBar.setPercent(percent);

            switch (status) {
                case FileDownloadStatus.pending:
                    statusText.setText(R.string.download_pending);
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
            button.setText(R.string.download_pause);
        }

        public ImageView icon;
        public TextView title;
        public TextView statusText;
        public MagicProgressBar progressBar;
        public TextView size;
        public TextView speedText;

        public TextView button;

        public MyHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.activity_download_item_icon);

            title = itemView.findViewById(R.id.activity_download_item_title);
            statusText = itemView.findViewById(R.id.activity_download_item_status);
            progressBar = itemView.findViewById(R.id.activity_download_item_progress);
            size = itemView.findViewById(R.id.activity_download_item_size);
            speedText = itemView.findViewById(R.id.activity_download_item_speed);

            button = itemView.findViewById(R.id.activity_download_item_button);
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



