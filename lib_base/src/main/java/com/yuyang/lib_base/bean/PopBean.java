package com.yuyang.lib_base.bean;

import java.io.Serializable;

public class PopBean implements Serializable {

    private String id;
    private String name;
    private boolean check;
    private Object object;

    public PopBean() {

    }

    public PopBean(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public PopBean(String id, String name, Object object) {
        this.id = id;
        this.name = name;
        this.object = object;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
