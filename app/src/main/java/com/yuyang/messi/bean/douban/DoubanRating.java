package com.yuyang.messi.bean.douban;

import android.os.Parcel;

public class DoubanRating implements android.os.Parcelable {
    private float max;
    private int numRaters;
    private float average;
    private float min;

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public int getNumRaters() {
        return numRaters;
    }

    public void setNumRaters(int numRaters) {
        this.numRaters = numRaters;
    }

    public float getAverage() {
        return average;
    }

    public void setAverage(float average) {
        this.average = average;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.max);
        dest.writeInt(this.numRaters);
        dest.writeFloat(this.average);
        dest.writeFloat(this.min);
    }

    public DoubanRating() {
    }

    protected DoubanRating(Parcel in) {
        this.max = in.readFloat();
        this.numRaters = in.readInt();
        this.average = in.readFloat();
        this.min = in.readFloat();
    }

    public static final Creator<DoubanRating> CREATOR = new Creator<DoubanRating>() {
        @Override
        public DoubanRating createFromParcel(Parcel source) {
            return new DoubanRating(source);
        }

        @Override
        public DoubanRating[] newArray(int size) {
            return new DoubanRating[size];
        }
    };
}
