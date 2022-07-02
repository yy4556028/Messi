package com.yuyang.messi.download;

import android.content.ContentValues;

public class FileDownloadModel {

    public final static String ID = "id";
    public final static String ICON = "icon";
    public final static String NAME = "name";
    public final static String URL = "url";
    public final static String PACKAGE = "package";
    public final static String VERSION = "version";
    public final static String PATH = "path";

    private int id;
    private String icon;
    private String name;
    private String url;
    private String packageName;
    private String version;
    private String path;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(ICON, icon);
        cv.put(NAME, name);
        cv.put(URL, url);
        cv.put(PACKAGE, packageName);
        cv.put(VERSION, version);
        cv.put(PATH, path);
        return cv;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FileDownloadModel) {
            FileDownloadModel model = ((FileDownloadModel) o);
            return id == model.getId();
        }
        return false;
    }
}
