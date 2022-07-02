package com.yuyang.messi.bean.douban;

import android.os.Parcel;
import com.google.gson.annotations.SerializedName;

public class DoubanRatingBean implements android.os.Parcelable {

    private float max;//最高评分
    private float average;//平均分
    private float min;//最低评分
    private int numRaters;
    private int stars;
    private Detail details;

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
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

    public int getNumRaters() {
        return numRaters;
    }

    public void setNumRaters(int numRaters) {
        this.numRaters = numRaters;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public Detail getDetails() {
        return details;
    }

    public void setDetails(Detail details) {
        this.details = details;
    }

    public static class Detail implements android.os.Parcelable {

        @SerializedName(value = "rating1", alternate = {"1"})
        private int rating1;
        @SerializedName(value = "rating2", alternate = {"2"})
        private int rating2;
        @SerializedName(value = "rating3", alternate = {"3"})
        private int rating3;
        @SerializedName(value = "rating4", alternate = {"4"})
        private int rating4;
        @SerializedName(value = "rating5", alternate = {"5"})
        private int rating5;

        public int getRating1() {
            return rating1;
        }

        public void setRating1(int rating1) {
            this.rating1 = rating1;
        }

        public int getRating2() {
            return rating2;
        }

        public void setRating2(int rating2) {
            this.rating2 = rating2;
        }

        public int getRating3() {
            return rating3;
        }

        public void setRating3(int rating3) {
            this.rating3 = rating3;
        }

        public int getRating4() {
            return rating4;
        }

        public void setRating4(int rating4) {
            this.rating4 = rating4;
        }

        public int getRating5() {
            return rating5;
        }

        public void setRating5(int rating5) {
            this.rating5 = rating5;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.rating1);
            dest.writeInt(this.rating2);
            dest.writeInt(this.rating3);
            dest.writeInt(this.rating4);
            dest.writeInt(this.rating5);
        }

        public Detail() {
        }

        protected Detail(Parcel in) {
            this.rating1 = in.readInt();
            this.rating2 = in.readInt();
            this.rating3 = in.readInt();
            this.rating4 = in.readInt();
            this.rating5 = in.readInt();
        }

        public static final Creator<Detail> CREATOR = new Creator<Detail>() {
            @Override
            public Detail createFromParcel(Parcel source) {
                return new Detail(source);
            }

            @Override
            public Detail[] newArray(int size) {
                return new Detail[size];
            }
        };
    }

    public DoubanRatingBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.max);
        dest.writeFloat(this.average);
        dest.writeFloat(this.min);
        dest.writeInt(this.numRaters);
        dest.writeInt(this.stars);
        dest.writeParcelable(this.details, flags);
    }

    protected DoubanRatingBean(Parcel in) {
        this.max = in.readFloat();
        this.average = in.readFloat();
        this.min = in.readFloat();
        this.numRaters = in.readInt();
        this.stars = in.readInt();
        this.details = in.readParcelable(Detail.class.getClassLoader());
    }

    public static final Creator<DoubanRatingBean> CREATOR = new Creator<DoubanRatingBean>() {
        @Override
        public DoubanRatingBean createFromParcel(Parcel source) {
            return new DoubanRatingBean(source);
        }

        @Override
        public DoubanRatingBean[] newArray(int size) {
            return new DoubanRatingBean[size];
        }
    };
}
