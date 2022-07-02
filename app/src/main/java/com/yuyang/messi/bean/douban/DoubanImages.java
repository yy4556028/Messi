package com.yuyang.messi.bean.douban;

import android.os.Parcel;

public class DoubanImages implements android.os.Parcelable {
    private String small;
    private String medium;
    private String large;

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.small);
        dest.writeString(this.medium);
        dest.writeString(this.large);
    }

    public DoubanImages() {
    }

    protected DoubanImages(Parcel in) {
        this.small = in.readString();
        this.medium = in.readString();
        this.large = in.readString();
    }

    public static final Creator<DoubanImages> CREATOR = new Creator<DoubanImages>() {
        @Override
        public DoubanImages createFromParcel(Parcel source) {
            return new DoubanImages(source);
        }

        @Override
        public DoubanImages[] newArray(int size) {
            return new DoubanImages[size];
        }
    };
}
