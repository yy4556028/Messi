package com.yuyang.messi.bean.douban;

import android.os.Parcel;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 电影实体类
 */
public class DoubanMovieBean implements android.os.Parcelable {

    private String id;//条目id
    private String title;//中文名
    @SerializedName(value = "origin_title", alternate = {"original_title"})
    private String origin_title;//原名
    private String alt;//条目页url
    private DoubanImages images;//电影海报图，分别提供288px x 465px(大)，96px x 155px(中) 64px x 103px(小)尺寸
    private DoubanRatingBean rating;
    private int ratings_count;
    private List<DoubanPerson> casts;//主演，最多可获得4个
    private List<DoubanPerson> directors;// 导演
    private String year;//年代
    private List<String> genres;//类型
    private List<String> countries;//国家
    private List<String> durations;//时常 102分钟
    private String mainland_pubdate;
    private String pubdate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrigin_title() {
        return origin_title;
    }

    public void setOrigin_title(String origin_title) {
        this.origin_title = origin_title;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public DoubanImages getImages() {
        return images;
    }

    public void setImages(DoubanImages images) {
        this.images = images;
    }

    public DoubanRatingBean getRating() {
        return rating;
    }

    public void setRating(DoubanRatingBean rating) {
        this.rating = rating;
    }

    public int getRatings_count() {
        return ratings_count;
    }

    public void setRatings_count(int ratings_count) {
        this.ratings_count = ratings_count;
    }

    public String getMainland_pubdate() {
        return mainland_pubdate;
    }

    public void setMainland_pubdate(String mainland_pubdate) {
        this.mainland_pubdate = mainland_pubdate;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getCasts() {
        if (casts == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < casts.size(); i++) {
            if (i == casts.size() - 1) {
                sb.append(casts.get(i).getName());
            } else {
                sb.append(casts.get(i).getName() + ",");
            }
        }
        return sb.toString();
    }

    public void setCasts(List<DoubanPerson> casts) {
        this.casts = casts;
    }

    public String getDirectors() {
        if (directors == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < directors.size(); i++) {
            if (i == directors.size() - 1) {
                sb.append(directors.get(i).getName());
            } else {
                sb.append(directors.get(i).getName() + ",");
            }
        }
        return sb.toString();
    }

    public void setDirectors(List<DoubanPerson> directors) {
        this.directors = directors;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }

    public List<String> getDurations() {
        return durations;
    }

    public void setDurations(List<String> durations) {
        this.durations = durations;
    }

    public DoubanMovieBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.origin_title);
        dest.writeString(this.alt);
        dest.writeParcelable(this.images, flags);
        dest.writeParcelable(this.rating, flags);
        dest.writeInt(this.ratings_count);
        dest.writeTypedList(this.casts);
        dest.writeTypedList(this.directors);
        dest.writeString(this.year);
        dest.writeStringList(this.genres);
        dest.writeStringList(this.countries);
        dest.writeStringList(this.durations);
        dest.writeString(this.mainland_pubdate);
        dest.writeString(this.pubdate);
    }

    protected DoubanMovieBean(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.origin_title = in.readString();
        this.alt = in.readString();
        this.images = in.readParcelable(DoubanImages.class.getClassLoader());
        this.rating = in.readParcelable(DoubanRatingBean.class.getClassLoader());
        this.ratings_count = in.readInt();
        this.casts = in.createTypedArrayList(DoubanPerson.CREATOR);
        this.directors = in.createTypedArrayList(DoubanPerson.CREATOR);
        this.year = in.readString();
        this.genres = in.createStringArrayList();
        this.countries = in.createStringArrayList();
        this.durations = in.createStringArrayList();
        this.mainland_pubdate = in.readString();
        this.pubdate = in.readString();
    }

    public static final Creator<DoubanMovieBean> CREATOR = new Creator<DoubanMovieBean>() {
        @Override
        public DoubanMovieBean createFromParcel(Parcel source) {
            return new DoubanMovieBean(source);
        }

        @Override
        public DoubanMovieBean[] newArray(int size) {
            return new DoubanMovieBean[size];
        }
    };
}
