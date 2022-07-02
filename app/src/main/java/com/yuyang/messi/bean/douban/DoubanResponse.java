package com.yuyang.messi.bean.douban;

import java.util.List;

public class DoubanResponse {

    private int count;//该次请求返回个数
    private int start;
    private int total;//总数
    private String title;
    private List<DoubanMovieBean> subjects;
    private List<DoubanMusicBean> musics;
    private List<DoubanBookBean> books;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<DoubanMovieBean> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<DoubanMovieBean> subjects) {
        this.subjects = subjects;
    }

    public List<DoubanMusicBean> getMusics() {
        return musics;
    }

    public void setMusics(List<DoubanMusicBean> musics) {
        this.musics = musics;
    }

    public List<DoubanBookBean> getBooks() {
        return books;
    }

    public void setBooks(List<DoubanBookBean> books) {
        this.books = books;
    }
}
