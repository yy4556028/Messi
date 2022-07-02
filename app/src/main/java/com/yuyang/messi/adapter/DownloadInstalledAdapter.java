package com.yuyang.messi.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.yuyang.lib_base.utils.AppInfoUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.AppBean;
import com.yuyang.messi.download.FileDownloadListener;
import com.yuyang.messi.download.FileDownloadManager;
import com.yuyang.messi.download.FileDownloadModel;
import com.yuyang.messi.event.DownloadUpdateUIEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class DownloadInstalledAdapter extends RecyclerView.Adapter<DownloadInstalledAdapter.MyHolder> {

    private Context context;
    public List<AppBean> beanList;

    private LayoutInflater inflater;

    public DownloadInstalledAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void updateData(List<AppBean> beanList) {
        this.beanList = beanList;
        notifyDataSetChanged();
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int i) {
        return new MyHolder(inflater.inflate(R.layout.fragment_download_update_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {
        final AppBean appBean = beanList.get(position);

        Glide.with(context)
                .load(appBean.getAppIcon())
                .into(holder.logoImage);
        holder.titleText.setText(appBean.getAppName());

        if (AppInfoUtil.isVersionOutdated(AppInfoUtil.getAppVersionName(), appBean.getAppVersion())) {
            holder.versionText.setText(AppInfoUtil.getAppVersionName() + " -> " + appBean.getAppVersion());

            if (FileDownloadManager.getInstance().getByUrl(appBean.getDownloadUrl()) == null) {
                holder.actionText.setText(context.getString(R.string.download_update));
                holder.actionText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FileDownloadModel model = new FileDownloadModel();
                        model.setPath(FileDownloadManager.getInstance().createPath(holder.titleText.getText().toString(), appBean.getDownloadUrl()));
                        model.setId(FileDownloadUtils.generateId(appBean.getDownloadUrl(), model.getPath()));
                        model.setUrl(appBean.getDownloadUrl());
                        model.setIcon(appBean.getAppIcon());
                        model.setName(appBean.getAppName());
                        model.setPackageName(appBean.getAppPackageName());
                        model.setVersion(appBean.getAppVersion());

                        model = FileDownloadManager.getInstance().addTask(model);
                        if (model != null) {
                            final BaseDownloadTask task = FileDownloader.getImpl().create(model.getUrl())
                                    .setPath(model.getPath())
                                    .setCallbackProgressTimes(1000)
                                    .setMinIntervalUpdateSpeed(1000)
                                    .setAutoRetryTimes(1)
                                    .setListener(FileDownloadListener.getInstance());

                            task.start();
                            notifyDataSetChanged();
                            EventBus.getDefault().post(new DownloadUpdateUIEvent());
                        }
                    }
                });
            } else {
                holder.actionText.setText(context.getString(R.string.download_updating));
                holder.actionText.setOnClickListener(null);
            }
        } else {
            holder.actionText.setText(context.getString(R.string.download_uninstall));
            holder.versionText.setText(appBean.getAppVersion());

            holder.actionText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DELETE);
                    intent.setData(Uri.fromParts("package", appBean.getAppPackageName(), null));
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return beanList == null ? 0 : beanList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        private ImageView logoImage;
        private TextView actionText;

        private TextView titleText;
        private TextView versionText;

        public MyHolder(View itemView) {
            super(itemView);
            logoImage = (ImageView) itemView.findViewById(R.id.fragment_download_update_item_image);
            actionText = (TextView) itemView.findViewById(R.id.fragment_download_update_item_action);

            titleText = (TextView) itemView.findViewById(R.id.fragment_download_update_item_title);
            versionText = (TextView) itemView.findViewById(R.id.fragment_download_update_item_version);
        }

    }
}
