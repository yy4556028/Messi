package com.yuyang.messi.bean.football;

import java.util.List;

/**
 * Created by Yamap on 2017/2/25.
 */

public class FootballViewsBean {

    private List<FootballScoreBean> jifenbang;
    private List<FootballGoalBean> sheshoubang;

    public List<FootballScoreBean> getJifenbang() {
        return jifenbang;
    }

    public void setJifenbang(List<FootballScoreBean> jifenbang) {
        this.jifenbang = jifenbang;
    }

    public List<FootballGoalBean> getSheshoubang() {
        return sheshoubang;
    }

    public void setSheshoubang(List<FootballGoalBean> sheshoubang) {
        this.sheshoubang = sheshoubang;
    }
}
