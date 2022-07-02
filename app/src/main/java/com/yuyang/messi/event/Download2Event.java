package com.yuyang.messi.event;

import com.yuyang.messi.bean.AppBean;

public class Download2Event {

    public AppBean appBean;
    public int speed;

    public Download2Event(AppBean appBean) {
        this.appBean = appBean;
    }

    public Download2Event(AppBean appBean, int speed) {
        this.appBean = appBean;
        this.speed = speed;
    }
}
