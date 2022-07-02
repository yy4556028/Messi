package com.yuyang.messi.bean.football;

/**
 * Created by Yamap on 2017/2/25.
 */

public class FootballBean {

    /**
     * 联赛名
     */
    private String key;

    private FootballViewsBean views;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public FootballViewsBean getViews() {
        return views;
    }

    public void setViews(FootballViewsBean views) {
        this.views = views;
    }
}
