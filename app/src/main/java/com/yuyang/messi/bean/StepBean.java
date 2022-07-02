package com.yuyang.messi.bean;

import android.content.ContentValues;

/**
 * Created by Yamap on 2017/7/30.
 */

public class StepBean {
    public final static String TODAY = "today";
    public final static String STEP = "step";

    private String today;
    private int step;

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(TODAY, today);
        cv.put(STEP, step);
        return cv;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof StepBean) {
            StepBean model = ((StepBean) o);
            return today.equals(model.getToday());
        }
        return false;
    }
}
