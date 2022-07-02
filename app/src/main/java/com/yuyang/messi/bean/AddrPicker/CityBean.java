package com.yuyang.messi.bean.AddrPicker;

import java.util.List;

public class CityBean {

    private String name;
    private List<DistrictBean> districtList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DistrictBean> getDistrictList() {
        return districtList;
    }

    public void setDistrictList(List<DistrictBean> districtList) {
        this.districtList = districtList;
    }
}
