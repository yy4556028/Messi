package com.yuyang.messi.ui.finance.bean;

public class DapanBean {

    private String dot;//当前价格
    private String name;//股票名称
    private float nowPic;//涨量
    private float rate;//涨幅(%)
    private int traNumber;//成交量
    private float traAmount;//成交额(万)

    public String getDot() {
        return dot;
    }

    public void setDot(String dot) {
        this.dot = dot;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getNowPic() {
        return nowPic;
    }

    public void setNowPic(float nowPic) {
        this.nowPic = nowPic;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
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
}
