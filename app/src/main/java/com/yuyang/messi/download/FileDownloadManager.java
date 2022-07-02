package com.yuyang.messi.download;

import android.content.Context;
import android.text.TextUtils;

import com.liulishuo.filedownloader.FileDownloadConnectListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.yuyang.lib_base.utils.AppInfoUtil;
import com.yuyang.lib_base.utils.security.MD5Util;
import com.yuyang.messi.MessiApp;
import com.yuyang.messi.db.download.FileDownloadDBHelper;
import com.yuyang.messi.event.DownloadConnectedEvent;
import com.yuyang.lib_base.utils.StorageUtil;
import com.yuyang.messi.threadPool.ThreadPool;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class FileDownloadManager {

    private static FileDownloadManager manager;

    private FileDownloadManager() {
        dbHelper = new FileDownloadDBHelper();
        modelList = dbHelper.getAll();
    }

    public static FileDownloadManager getInstance() {
        if (manager == null) {
            manager = new FileDownloadManager();
        }
        return manager;
    }

    private FileDownloadDBHelper dbHelper;
    private List<FileDownloadModel> modelList;

    private FileDownloadConnectListener listener;

    public void onCreate(final WeakReference<Context> activityWeakReference) {
        FileDownloader.getImpl().bindService();

        if (listener != null) {
            FileDownloader.getImpl().removeServiceConnectListener(listener);
        }

        listener = new FileDownloadConnectListener() {

            @Override
            public void connected() {
                if (activityWeakReference == null
                        || activityWeakReference.get() == null) {
                    return;
                }

                EventBus.getDefault().post(new DownloadConnectedEvent(true));
            }

            @Override
            public void disconnected() {
                if (activityWeakReference == null
                        || activityWeakReference.get() == null) {
                    return;
                }
                EventBus.getDefault().post(new DownloadConnectedEvent(false));
            }
        };

        FileDownloader.getImpl().addServiceConnectListener(listener);
    }

    public void onDestroy() {
        FileDownloader.getImpl().removeServiceConnectListener(listener);
        listener = null;
    }

    public boolean isReady() {
        return FileDownloader.getImpl().isServiceConnected();
    }

    public List<FileDownloadModel> getDownloading() {
        List<FileDownloadModel> list = new ArrayList<>();

        for (FileDownloadModel model : modelList) {
            if (getStatus(model.getId(), model.getPath()) != FileDownloadStatus.completed) {
                list.add(model);
            }
        }
        return list;
    }

    public List<FileDownloadModel> getDownloaded() {
        List<FileDownloadModel> list = new ArrayList<>();

        for (FileDownloadModel model : modelList) {
            if (getStatus(model.getId(), model.getPath()) == FileDownloadStatus.completed) {
                if (!isExistModelAndFile(model.getUrl())) {
                    delete(model);
                } else {
                    list.add(model);
                }
            }
        }
        return list;
    }

    public List<FileDownloadModel> getAll() {
        List<FileDownloadModel> list = new ArrayList<>();
        final List<FileDownloadModel> toBeDeleteList = new ArrayList<>();

        for (FileDownloadModel model : modelList) {
            if (getStatus(model.getId(), model.getPath()) == FileDownloadStatus.completed && !isExistModelAndFile(model.getUrl())) {
                toBeDeleteList.add(model);
            } else {
                if (!AppInfoUtil.isApkInstalled(MessiApp.getInstance(), model.getPackageName())) {
                    list.add(model);
                } else {
                    if (AppInfoUtil.isVersionOutdated(
                            AppInfoUtil.getAppVersionName(),
                            model.getVersion())) {
                        list.add(model);
                    } else {
                        toBeDeleteList.add(model);
                    }
                }
            }
        }
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                for (FileDownloadModel model : toBeDeleteList) {
                    delete(model);
                }
            }
        });

        return list;
    }

    public FileDownloadModel get(final int position) {
        return modelList.get(position);
    }

    public FileDownloadModel getByUrl(final String url) {
        for (FileDownloadModel model : modelList) {
            if (model.getUrl().equals(url)) {
                return model;
            }
        }
        return null;
    }

//    public FileDownloadModel getById(final int id) {
//        for (FileDownloadModel model : modelList) {
//            if (model.getId() == id) {
//                return model;
//            }
//        }
//        return null;
//    }


    public boolean isExistModel(final String url) {
        FileDownloadModel model = getByUrl(url);
        return model != null;
    }

    public boolean isExistModelAndFile(final String url) {
        FileDownloadModel model = getByUrl(url);
        if (model == null) return false;
        return new File(model.getPath()).exists();
    }

    public int getStatus(final int id, String path) {
        return FileDownloader.getImpl().getStatus(id, path);
    }

    public long getTotal(final int id) {
        return FileDownloader.getImpl().getTotal(id);
    }

    public long getSoFar(final int id) {
        return FileDownloader.getImpl().getSoFar(id);
    }

    public int getTaskCounts() {
        return modelList.size();
    }

    public String createPath(String name, final String app_key) {
        if (TextUtils.isEmpty(app_key)) {
            return null;
        }

        if (TextUtils.isEmpty(name)) {
            name = MD5Util.md5(app_key);
        }
        return StorageUtil.getExternalFilesDir(StorageUtil.DOWNLOAD) + File.separator + name;
//        return FileDownloadUtils.getDefaultSaveFilePath(url);
    }

    public FileDownloadModel addTask(FileDownloadModel model) {

        if (TextUtils.isEmpty(model.getUrl())) {
            return null;
        }

        FileDownloadModel searchModel = getByUrl(model.getUrl());
        if (searchModel != null) {
            return searchModel;
        }

        model.setPath(createPath(model.getName(), model.getUrl()));

        if (dbHelper.insert(model)) {
            modelList.add(model);
        }

        return model;
    }

    public void delete(FileDownloadModel model) {

        FileDownloader.getImpl().pause(model.getId());

        if (isExistModelAndFile(model.getUrl())) {
            new File(FileDownloadManager.getInstance().getByUrl(model.getUrl()).getPath()).delete();
        }
        dbHelper.delete(model);
        modelList.remove(model);
    }
}
