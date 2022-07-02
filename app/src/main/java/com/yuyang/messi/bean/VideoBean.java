package com.yuyang.messi.bean;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

public class VideoBean implements Parcelable {

    private Long videoId;
    private String title;
    private String displayName;
    private Uri videoUri;
    private String path;
    private long length;
    private Bitmap bitmap;
    private long size;
    private int width;
    private int height;

    public VideoBean() {
    }

    public VideoBean(Parcel in) {
        if (in.readByte() == 0) {
            videoId = null;
        } else {
            videoId = in.readLong();
        }
        title = in.readString();
        displayName = in.readString();
        videoUri = in.readParcelable(Uri.class.getClassLoader());
        path = in.readString();
        length = in.readLong();
        bitmap = in.readParcelable(VideoBean.class.getClassLoader());
        size = in.readInt();
        width = in.readInt();
        height = in.readInt();
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Uri getVideoUri() {
        if (videoId != null && videoUri == null) {
            videoUri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, String.valueOf(videoId));
        }
        return videoUri;
    }

    public void setVideoUri(Uri videoUri) {
        this.videoUri = videoUri;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        if (videoId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(videoId);
        }
        dest.writeString(title);
        dest.writeString(displayName);
        dest.writeParcelable(videoUri, flags);
        dest.writeString(path);
        dest.writeLong(length);
        dest.writeParcelable(bitmap, flags);
        dest.writeLong(size);
        dest.writeInt(width);
        dest.writeInt(height);
    }

    public static final Creator<VideoBean> CREATOR = new Creator<VideoBean>() {
        public VideoBean createFromParcel(Parcel in) {
            return new VideoBean(in);
        }

        public VideoBean[] newArray(int size) {
            return new VideoBean[size];
        }
    };
}
