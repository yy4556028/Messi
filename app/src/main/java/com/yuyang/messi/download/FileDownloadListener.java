package com.yuyang.messi.download;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.yuyang.messi.event.DownloadEvent;

import org.greenrobot.eventbus.EventBus;

public class FileDownloadListener extends FileDownloadSampleListener {

    private static FileDownloadListener instance;

    private FileDownloadListener(){}

    public static FileDownloadListener getInstance() {
        if (instance == null) {
            instance = new FileDownloadListener();
        }
        return instance;
    }

    @Override
    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        super.pending(task, soFarBytes, totalBytes);

        DownloadEvent event = new DownloadEvent();
        event.status = FileDownloadStatus.pending;
        event.id = task.getId();
        event.soFarBytes = soFarBytes;
        event.totalBytes = totalBytes;
        EventBus.getDefault().post(event);
    }

    @Override
    protected void started(BaseDownloadTask task) {
        super.started(task);

        DownloadEvent event = new DownloadEvent();
        event.status = FileDownloadStatus.started;
        event.id = task.getId();
        EventBus.getDefault().post(event);
    }

    @Override
    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
        super.connected(task, etag, isContinue, soFarBytes, totalBytes);

        DownloadEvent event = new DownloadEvent();
        event.status = FileDownloadStatus.connected;
        event.id = task.getId();
        event.soFarBytes = soFarBytes;
        event.totalBytes = totalBytes;
        EventBus.getDefault().post(event);
    }

    @Override
    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        super.progress(task, soFarBytes, totalBytes);

        DownloadEvent event = new DownloadEvent();
        event.status = FileDownloadStatus.progress;
        event.id = task.getId();
        event.soFarBytes = soFarBytes;
        event.totalBytes = totalBytes;
        event.speed = task.getSpeed();
        EventBus.getDefault().post(event);
    }

    @Override
    protected void error(BaseDownloadTask task, Throwable e) {
        super.error(task, e);

        DownloadEvent event = new DownloadEvent();
        event.status = FileDownloadStatus.error;
        event.id = task.getId();
        event.soFarBytes = task.getLargeFileSoFarBytes();
        event.totalBytes = task.getLargeFileTotalBytes();
        EventBus.getDefault().post(event);
    }

    @Override
    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        super.paused(task, soFarBytes, totalBytes);

        DownloadEvent event = new DownloadEvent();
        event.status = FileDownloadStatus.paused;
        event.id = task.getId();
        event.soFarBytes = soFarBytes;
        event.totalBytes = totalBytes;
        EventBus.getDefault().post(event);
    }

    @Override
    protected void completed(BaseDownloadTask task) {
        super.completed(task);

        DownloadEvent event = new DownloadEvent();
        event.status = FileDownloadStatus.completed;
        event.id = task.getId();
        EventBus.getDefault().post(event);
    }
}
