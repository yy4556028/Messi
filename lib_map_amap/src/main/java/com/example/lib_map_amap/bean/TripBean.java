package com.example.lib_map_amap.bean;

public class TripBean {

    private String natioinCN;
    private String arriveProvince;//省份名
    private String arrivecity;//城市名
    private String citylat;
    private String citylng;
    private String comeNationName;
    private int comeTimesCity;
    private int comeTimesNation;
    private int comeTimesProvince;
    private String date;
    private String dateTime;
    private String startDate;
    private String endDate;
    private String home;
    private String newsno;
    private String newstitle;
    private String newstype;
    private String typeno;
    private String sourceTag;
    private int tangKaocha;
    private int tangOverseaVisit;

    public String getNatioinCN() {
        return natioinCN;
    }

    public void setNatioinCN(String natioinCN) {
        this.natioinCN = natioinCN;
    }

    public String getArriveProvince() {
        return arriveProvince;
    }

    public void setArriveProvince(String arriveProvince) {
        this.arriveProvince = arriveProvince;
    }

    public String getArrivecity() {
        return arrivecity;
    }

    public void setArrivecity(String arrivecity) {
        this.arrivecity = arrivecity;
    }

    public double getCitylat() {
        try {
            return Double.parseDouble(citylat);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setCitylat(String citylat) {
        this.citylat = citylat;
    }

    public double getCitylng() {
        try {
            return Double.parseDouble(citylng);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setCitylng(String citylng) {
        this.citylng = citylng;
    }

    public String getComeNationName() {
        return comeNationName;
    }

    public void setComeNationName(String comeNationName) {
        this.comeNationName = comeNationName;
    }

    public int getComeTimesCity() {
        return comeTimesCity;
    }

    public void setComeTimesCity(int comeTimesCity) {
        this.comeTimesCity = comeTimesCity;
    }

    public int getComeTimesNation() {
        return comeTimesNation;
    }

    public void setComeTimesNation(int comeTimesNation) {
        this.comeTimesNation = comeTimesNation;
    }

    public int getComeTimesProvince() {
        return comeTimesProvince;
    }

    public void setComeTimesProvince(int comeTimesProvince) {
        this.comeTimesProvince = comeTimesProvince;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getNewsno() {
        return newsno;
    }

    public void setNewsno(String newsno) {
        this.newsno = newsno;
    }

    public String getNewstitle() {
        return newstitle;
    }

    public void setNewstitle(String newstitle) {
        this.newstitle = newstitle;
    }

    public String getNewstype() {
        return newstype;
    }

    public void setNewstype(String newstype) {
        this.newstype = newstype;
    }

    public String getTypeno() {
        return typeno;
    }

    public void setTypeno(String typeno) {
        this.typeno = typeno;
    }

    public String getSourceTag() {
        return sourceTag;
    }

    public void setSourceTag(String sourceTag) {
        this.sourceTag = sourceTag;
    }

    public int getTangKaocha() {
        return tangKaocha;
    }

    public void setTangKaocha(int tangKaocha) {
        this.tangKaocha = tangKaocha;
    }

    public int getTangOverseaVisit() {
        return tangOverseaVisit;
    }

    public void setTangOverseaVisit(int tangOverseaVisit) {
        this.tangOverseaVisit = tangOverseaVisit;
    }
}
