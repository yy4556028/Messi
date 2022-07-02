package com.example.aidldemo;

import android.os.Parcel;
import android.os.Parcelable;

public class AidlBean implements Parcelable {

    private String name;
    private int age;
    private String tag;

    public AidlBean() {

    }

    protected AidlBean(Parcel in) {
        name = in.readString();
        age = in.readInt();
        tag = in.readString();
    }

    public static final Creator<AidlBean> CREATOR = new Creator<AidlBean>() {
        @Override
        public AidlBean createFromParcel(Parcel in) {
            return new AidlBean(in);
        }

        @Override
        public AidlBean[] newArray(int size) {
            return new AidlBean[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(age);
        parcel.writeString(tag);
    }

    public void readFromParcel(Parcel parcel) {
        name = parcel.readString();
        age = parcel.readInt();
        tag = parcel.readString();
    }
}
