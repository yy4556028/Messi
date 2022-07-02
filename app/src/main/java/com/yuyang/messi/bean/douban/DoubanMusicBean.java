package com.yuyang.messi.bean.douban;


import java.util.List;

/**
 * 音乐:
 */
public class DoubanMusicBean {

    private String id;
    private String title;
    private String alt;
    private List<DoubanPerson> author;
    private String image;
    private String mobile_link;
    private Rating rating;
    private DoubanMusicAttrs attrs;

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

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public List<DoubanPerson> getAuthor() {
        return author;
    }

    public void setAuthor(List<DoubanPerson> author) {
        this.author = author;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMobile_link() {
        return mobile_link;
    }

    public void setMobile_link(String mobile_link) {
        this.mobile_link = mobile_link;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public DoubanMusicAttrs getAttrs() {
        return attrs;
    }

    public void setAttrs(DoubanMusicAttrs attrs) {
        this.attrs = attrs;
    }

    public static class Rating {
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
    }
}
