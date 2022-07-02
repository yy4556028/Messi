package com.yuyang.messi.ui.home.overwork;

public class OverworkBean {

    private String onTime;
    private String offTime;

    private String date;
    private int week;
    private boolean calcFlag;//true参与计算平均下班时间

    public String getOnTime() {
        return onTime;
    }

    public void setOnTime(String onTime) {
        this.onTime = onTime;
    }

    public String getOffTime() {
        return offTime;
    }

    public void setOffTime(String offTime) {
        this.offTime = offTime;
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
