package com.yuyang.messi.bean.riffsy;


public class RiffsyResponseBean {

    private String next;

    private RiffsyResultBean[] results;

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public RiffsyResultBean[] getResults() {
        return results;
    }

    public void setResults(RiffsyResultBean[] results) {
        this.results = results;
    }
}
