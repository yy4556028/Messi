package com.yuyang.messi.bean.AddrPicker;

import java.util.List;

public class ProvinceBean {

    private String name;
    private List<CityBean> cityList;

    public List<CityBean> getCityList() {
        return cityList;
    }

    public void setCityList(List<CityBean> cityList) {
        this.cityList = cityList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
