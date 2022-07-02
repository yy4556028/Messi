package com.yuyang.messi.bean;

import com.google.gson.annotations.SerializedName;
import com.yuyang.messi.download.DownloadStatus;

import java.io.Serializable;

public class AppBean implements Serializable {

    @SerializedName(value = "appName", alternate = {"app_name"})
    private String appName;//应用名称
    private String appVersion;//版本号
    private String appPackageName;//应用程序包名，iOS为BundleId，Android为包名
    private String appIcon;//应用的Icon图标key，访问地址为 http://o1wh05aeh.qnssl.com/image/view/app_icons/[应用的Icon图标key]
    private String downloadUrl;

    private int downloadId;
    private String path;
    private DownloadStatus status;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public DownloadStatus getStatus() {
        return status;
    }

    public void setStatus(DownloadStatus status) {
        this.status = status;
    }

    public String getStatusText() {
        if (status == null)
            return "Not Download";

        switch (status) {
            case STATUS_NOT_DOWNLOAD:
                return "Not Download";
            case STATUS_CONNECTING:
                return "Connecting";
            case STATUS_CONNECT_ERROR:
                return "Connect Error";
            case STATUS_DOWNLOADING:
                return "Downloading";
            case STATUS_PAUSED:
                return "Pause";
            case STATUS_DOWNLOAD_ERROR:
                return "Download Error";
            case STATUS_COMPLETE:
                return "Complete";
            case STATUS_INSTALLED:
                return "Installed";
            default:
                return "Not Download";
        }
    }

    public String getButtonText() {
        if (status == null)
            return "Download";

        switch (status) {
            case STATUS_NOT_DOWNLOAD:
                return "Download";
            case STATUS_CONNECTING:
                return "Cancel";
            case STATUS_CONNECT_ERROR:
                return "Try Again";
            case STATUS_DOWNLOADING:
                return "Pause";
            case STATUS_PAUSED:
                return "Resume";
            case STATUS_DOWNLOAD_ERROR:
                return "Try Again";
            case STATUS_COMPLETE:
                return "Install";
            case STATUS_INSTALLED:
                return "Open";
            default:
                return "Download";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AppBean) {
            AppBean bean = ((AppBean) o);
            return bean.getAppName().equalsIgnoreCase(getAppName()) && bean.getDownloadUrl().equalsIgnoreCase(getDownloadUrl());
        }
        return false;
    }
}
