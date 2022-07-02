package com.yuyang.messi.view.scroll.month_view;

import java.util.Calendar;

/**
 * Created by yuy on 2016/12/20.
 */
public class Day<T> {

    private Calendar calendar;

    private int pageMonth;
    private boolean focus;

    private T bean;

    public Day(Calendar calendar) {
        this.calendar = calendar;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public int getPageMonth() {
        return pageMonth;
    }

    public void setPageMonth(int pageMonth) {
        this.pageMonth = pageMonth;
    }

    public boolean isFocus() {
        return focus;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    public T getBean() {
        return bean;
    }

    public void setBean(T bean) {
        this.bean = bean;
    }
}
