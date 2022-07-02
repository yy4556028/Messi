package com.yuyang.messi.bean;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;

public class ImageBean implements Parcelable {

    //local
    private Long imageId;
    private String imageName;
    private Uri imageUri;//本地图片
    private String path;

    //net
    private String url;//网络图片

    public ImageBean(Long imageId, String imageName, String path) {
        this.imageId = imageId;
        this.imageName = imageName;
        this.path = path;
    }

    public ImageBean(Uri imageUri, String path) {
        this.imageUri = imageUri;
        this.path = path;
    }

    protected ImageBean(Parcel in) {
        if (in.readByte() == 0) {
            imageId = null;
        } else {
            imageId = in.readLong();
        }
        imageName = in.readString();
        imageUri = in.readParcelable(Uri.class.getClassLoader());
        path = in.readString();
        url = in.readString();
    }

    public static final Creator<ImageBean> CREATOR = new Creator<ImageBean>() {
        @Override
        public ImageBean createFromParcel(Parcel in) {
            return new ImageBean(in);
        }

        @Override
        public ImageBean[] newArray(int size) {
            return new ImageBean[size];
        }
    };

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Uri getImageUri() {
        if (imageId != null && imageUri == null) {
            imageUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(imageId));
        }
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageBean)) return false;

        ImageBean imageBean = (ImageBean) o;

        if (!TextUtils.isEmpty(url)) {
            return url.equals(imageBean.url);
        } else {
            return getImageUri().equals(imageBean.getImageUri());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (imageId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(imageId);
        }
        dest.writeString(imageName);
        dest.writeParcelable(imageUri, flags);
        dest.writeString(path);
        dest.writeString(url);
    }
}
