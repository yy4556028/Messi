package com.yuyang.messi.ui.finance.bean;

public class StockBean {

    private String gid;//股票编号
    private String name;//股票名称

    private float increPer;//涨跌百分比 -0.46
    private float increase;//涨跌额
    private float todayStartPri;//今日开盘价
    private float yestodEndPri;//昨日收盘价
    private float nowpri;//当前价格
    private float todayMax;//今日最高价
    private float todayMin;//今日最低价
    private float competitivePri;//竞买价
    private float reservePri;//竞卖价
    private int traNumber;//成交量
    private float traAmount;//成交金额

    private int dealNum;//成交量
    private float dealPri;//成交额
    private float highPri;//最高
    private float lowpri;//最低

    private float openPri;//今开
    private float yesPri;//昨收

    private String date;
    private String time;

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getIncrePer() {
        return increPer;
    }

    public void setIncrePer(float increPer) {
        this.increPer = increPer;
    }

    public float getIncrease() {
        return increase;
    }

    public void setIncrease(float increase) {
        this.increase = increase;
    }

    public float getTodayStartPri() {
        return todayStartPri;
    }

    public void setTodayStartPri(float todayStartPri) {
        this.todayStartPri = todayStartPri;
    }

    public float getYestodEndPri() {
        return yestodEndPri;
    }

    public void setYestodEndPri(float yestodEndPri) {
        this.yestodEndPri = yestodEndPri;
    }

    public float getNowpri() {
        return nowpri;
    }

    public void setNowpri(float nowpri) {
        this.nowpri = nowpri;
    }

    public float getTodayMax() {
        return todayMax;
    }

    public void setTodayMax(float todayMax) {
        this.todayMax = todayMax;
    }

    public float getTodayMin() {
        return todayMin;
    }

    public void setTodayMin(float todayMin) {
        this.todayMin = todayMin;
    }

    public float getCompetitivePri() {
        return competitivePri;
    }

    public void setCompetitivePri(float competitivePri) {
        this.competitivePri = competitivePri;
    }

    public float getReservePri() {
        return reservePri;
    }

    public void setReservePri(float reservePri) {
        this.reservePri = reservePri;
    }

    public int getTraNumber() {
        return traNumber;
    }

    public void setTraNumber(int traNumber) {
        this.traNumber = traNumber;
    }

    public float getTraAmount() {
        return traAmount;
    }

    public void setTraAmount(float traAmount) {
        this.traAmount = traAmount;
    }

    public int getDealNum() {
        return dealNum;
    }

    public void setDealNum(int dealNum) {
        this.dealNum = dealNum;
    }

    public float getDealPri() {
        return dealPri;
    }

    public void setDealPri(float dealPri) {
        this.dealPri = dealPri;
    }

    public float getHighPri() {
        return highPri;
    }

    public void setHighPri(float highPri) {
        this.highPri = highPri;
    }

    public float getLowpri() {
        return lowpri;
    }

    public void setLowpri(float lowpri) {
        this.lowpri = lowpri;
    }

    public float getOpenPri() {
        return openPri;
    }

    public void setOpenPri(float openPri) {
        this.openPri = openPri;
    }

    public float getYesPri() {
        return yesPri;
    }

    public void setYesPri(float yesPri) {
        this.yesPri = yesPri;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
