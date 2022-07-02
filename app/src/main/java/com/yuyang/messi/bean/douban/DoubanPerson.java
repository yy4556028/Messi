package com.yuyang.messi.bean.douban;

import android.os.Parcel;

public class DoubanPerson implements android.os.Parcelable {

    private String id;//影人条目id
    private String name;//中文名
    private String alt;//影人条目URL
    private DoubanImages avatars;//头像

    public DoubanImages getAvatars() {
        return avatars;
    }

    public void setAvatars(DoubanImages avatars) {
        this.avatars = avatars;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.alt);
        dest.writeParcelable(this.avatars, flags);
    }

    public DoubanPerson() {
    }

    protected DoubanPerson(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.alt = in.readString();
        this.avatars = in.readParcelable(DoubanImages.class.getClassLoader());
    }

    public static final Creator<DoubanPerson> CREATOR = new Creator<DoubanPerson>() {
        @Override
        public DoubanPerson createFromParcel(Parcel source) {
            return new DoubanPerson(source);
        }

        @Override
        public DoubanPerson[] newArray(int size) {
            return new DoubanPerson[size];
        }
    };
}
