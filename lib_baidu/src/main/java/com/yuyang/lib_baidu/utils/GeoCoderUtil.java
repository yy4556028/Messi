package com.yuyang.lib_baidu.utils;


import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;

/**
 * 地理编码
 */
public class GeoCoderUtil {

    private static GeoCoder geoCoder = GeoCoder.newInstance();

    /**
     * 发起地理编码检索
     * @param city
     * @param address
     * @param listener
     */
    public static void geoCode(String city, String address, OnGetGeoCoderResultListener listener) {
        geoCoder.setOnGetGeoCodeResultListener(listener);
        GeoCodeOption option = new GeoCodeOption().city(city);
        option.address(address);
        geoCoder.geocode(option);
    }

    /**
     * 发起反地理编码检索
     * @param latLng
     * @param listener
     */
    public static void reverseGeoCode(LatLng latLng, OnGetGeoCoderResultListener listener) {
        geoCoder.setOnGetGeoCodeResultListener(listener);
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
    }
}
