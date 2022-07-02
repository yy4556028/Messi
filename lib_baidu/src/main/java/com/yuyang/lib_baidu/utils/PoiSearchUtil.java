package com.yuyang.lib_baidu.utils;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;

/**
 * Created by Yamap on 2017/5/24.
 */

public class PoiSearchUtil {

    private PoiSearch poiSearch;

    private static final PoiSearchUtil instance;

    static {
        instance = new PoiSearchUtil();
    }

    public static PoiSearchUtil getInstance() {
        return instance;
    }

    public void destory() {
        if (poiSearch != null) {
            poiSearch.destroy();
        }
    }

    private PoiSearchUtil() {
        poiSearch = PoiSearch.newInstance();
    }

    public void setOnGetPoiSearchResultListener(OnGetPoiSearchResultListener listener) {
        poiSearch.setOnGetPoiSearchResultListener(listener);
    }

    public void searchInCity(String city, String keyword, int pageNum) {
        poiSearch.searchInCity(
                new PoiCitySearchOption()
                        .city(city)
                        .keyword(keyword)
                        .pageNum(pageNum));
    }

    public void searchNearby(LatLng centerLocation, String keyword, int radius, int pageNum) {
        poiSearch.searchNearby(
                new PoiNearbySearchOption()
                        .keyword(keyword)
                        .sortType(PoiSortType.distance_from_near_to_far)
                        .location(centerLocation)
                        .radius(radius)
                        .pageNum(pageNum));
    }

}
