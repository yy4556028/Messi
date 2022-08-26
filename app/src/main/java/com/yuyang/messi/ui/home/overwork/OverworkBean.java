package com.yuyang.messi.ui.home.overwork;

public class OverworkBean {

    private Long onTimeMills;
    private Long offTimeMills;

    private String date;
    private int week;
    private boolean calcFlag;//true参与计算平均下班时间

    public Long getOnTimeMills() {
        return onTimeMills;
    }

    public void setOnTimeMills(Long onTimeMills) {
        this.onTimeMills = onTimeMills;
    }

    public Long getOffTimeMills() {
        return offTimeMills;
    }

    public void setOffTimeMills(Long offTimeMills) {
        this.offTimeMills = offTimeMills;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public boolean isCalcFlag() {
        return calcFlag;
    }

    public void setCalcFlag(boolean calcFlag) {
        this.calcFlag = calcFlag;
    }
}
