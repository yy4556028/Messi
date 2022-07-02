package com.yuyang.messi.bean.douban;

import android.os.Parcel;
import java.util.List;

/**
 * 书籍实体类
 */
public class DoubanBookBean implements android.os.Parcelable {

    private DoubanRatingBean rating;
    private String subtitle;
    private List<String> author;
    private String pubdate;
    private String origin_title;
    private String image;
    private String catalog;
    private String pages;
    private DoubanPerson images;
    private String id;
    private String publisher;
    private String title;
    private String url;
    private String author_intro;
    private String summary;
    private String price;
    private String isbn10;
    private String isbn13;


    public DoubanRatingBean getRating() {
        return rating;
    }

    public void setRating(DoubanRatingBean rating) {
        this.rating = rating;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public List<String> getAuthor() {
        return author;
    }

    public void setAuthor(List<String> author) {
        this.author = author;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getOrigin_title() {
        return origin_title;
    }

    public void setOrigin_title(String origin_title) {
        this.origin_title = origin_title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public DoubanPerson getImages() {
        return images;
    }

    public void setImages(DoubanPerson images) {
        this.images = images;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor_intro() {
        return author_intro;
    }

    public void setAuthor_intro(String author_intro) {
        this.author_intro = author_intro;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getIsbn10() {
        return isbn10;
    }

    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.rating, flags);
        dest.writeString(this.subtitle);
        dest.writeStringList(this.author);
        dest.writeString(this.pubdate);
        dest.writeString(this.origin_title);
        dest.writeString(this.image);
        dest.writeString(this.catalog);
        dest.writeString(this.pages);
        dest.writeParcelable(this.images, flags);
        dest.writeString(this.id);
        dest.writeString(this.publisher);
        dest.writeString(this.title);
        dest.writeString(this.url);
        dest.writeString(this.author_intro);
        dest.writeString(this.summary);
        dest.writeString(this.price);
        dest.writeString(this.isbn10);
        dest.writeString(this.isbn13);
    }

    public DoubanBookBean() {
    }

    protected DoubanBookBean(Parcel in) {
        this.rating = in.readParcelable(DoubanRatingBean.class.getClassLoader());
        this.subtitle = in.readString();
        this.author = in.createStringArrayList();
        this.pubdate = in.readString();
        this.origin_title = in.readString();
        this.image = in.readString();
        this.catalog = in.readString();
        this.pages = in.readString();
        this.images = in.readParcelable(DoubanPerson.class.getClassLoader());
        this.id = in.readString();
        this.publisher = in.readString();
        this.title = in.readString();
        this.url = in.readString();
        this.author_intro = in.readString();
        this.summary = in.readString();
        this.price = in.readString();
        this.isbn10 = in.readString();
        this.isbn13 = in.readString();
    }

    public static final Creator<DoubanBookBean> CREATOR = new Creator<DoubanBookBean>() {
        @Override
        public DoubanBookBean createFromParcel(Parcel source) {
            return new DoubanBookBean(source);
        }

        @Override
        public DoubanBookBean[] newArray(int size) {
            return new DoubanBookBean[size];
        }
    };
}
