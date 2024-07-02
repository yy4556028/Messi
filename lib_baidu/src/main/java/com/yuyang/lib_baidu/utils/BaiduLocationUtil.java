package com.yuyang.lib_baidu.utils;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.yuyang.lib_base.BaseApp;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建者: yuyang
 * 创建日期: 2015-04-22
 * 创建时间: 11:09
 * LocationUtil:定位工具类
 * <p>
 * LocationUtil.getInstance().startLocation(1000);
 * LocationManager.getInstance().stopLocation();
 * LocationManager.getInstance().registerLocationListener(locationListener)
 * LocationManager.getInstance().unregisterLocationListener(locationListener)
 *
 * @author yuyang
 * @version 1.0
 */
public class BaiduLocationUtil {

    // 百度定位客户端
    private LocationClient mLocationClient;

    // 定位配置参数
    private LocationClientOption option;

    private final List<OnLocationListener> listeners;

    private static final BaiduLocationUtil instance;

    static {
        instance = new BaiduLocationUtil();
    }

    public static BaiduLocationUtil getInstance() {
        return instance;
    }

    private BaiduLocationUtil() {
        listeners = new ArrayList<>();
    }

    private final BDAbstractLocationListener locationListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            if (bdLocation == null) return;

            if (listeners.size() == 0) return;

            if (bdLocation.getLocType() == BDLocation.TypeCriteriaException) {
//            new AlertDialog.Builder(Messi)
//                    .setMessage("当前模块需要定位服务,请设置允许添加权限")
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent();
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            if (Build.VERSION.SDK_INT >= 9) {
//                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                intent.setData("package", getPackageName(), null);
//                            } else {
//                                intent.setAction(Intent.ACTION_VIEW);
//                                intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
//                                intent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
//                            }
//                            startActivity(intent);
//                        }
//                    }).create().show();
                return;
            }

            if (TextUtils.isEmpty(bdLocation.getCity())) {
                Log.e(BaiduLocationUtil.class.getSimpleName(), "错误码：" + bdLocation.getLocType());
                Log.e(BaiduLocationUtil.class.getSimpleName(), bdLocation.getLocTypeDescription());
                return;
            }

            for (OnLocationListener listener : listeners) {
                listener.onLocation(bdLocation, mLocationClient);
            }

            if (true) return;
            bdLocation.getTime();//获取定位时间
            bdLocation.getLocType();//获取类型类型
            bdLocation.getLatitude();//获取纬度信息
            bdLocation.getLongitude();//获取经度信息
            bdLocation.getRadius();//获取定位精准度
            String addr = bdLocation.getAddrStr();    //获取详细地址信息
            String country = bdLocation.getCountry();    //获取国家
            String province = bdLocation.getProvince();    //获取省份
            String city = bdLocation.getCity();    //获取城市
            String district = bdLocation.getDistrict();    //获取区县
            String street = bdLocation.getStreet();    //获取街道信息
            String adcode = bdLocation.getAdCode();    //获取adcode
            String town = bdLocation.getTown();    //获取乡镇信息
        }
    };

    /**
     * 初始化定位服务并开始
     *
     * @param span 定位间隔 如果小于1000ms 则只发起一次定位，如果大于等于1000ms 则定时定位
     */
    public void startLocation(int span) {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            int oldSpan = mLocationClient.getLocOption().getScanSpan();

            if (oldSpan < 1000) {
                mLocationClient.getLocOption().setScanSpan(span);
                mLocationClient.restart();
            } else if (span == 0) {
                mLocationClient.requestLocation();
            }
            return;
        }

        try {
            mLocationClient = new LocationClient(BaseApp.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        mLocationClient.registerLocationListener(locationListener);

        option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(false);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    /**
     * 停止定位服务
     */
    public void stopLocation() {
        if (mLocationClient == null)
            return;

        mLocationClient.stop();
        mLocationClient = null;
        option = null;

        for (OnLocationListener listener : listeners) {
            listener = null;
        }
        listeners.clear();
    }

    public BDLocation getBdLocation() {
        return mLocationClient == null ? null : mLocationClient.getLastKnownLocation();
    }

    public void registerLocationListener(OnLocationListener onLocationListener) {
        if (!listeners.contains(onLocationListener)) {
            listeners.add(onLocationListener);
        }
    }

    public void unregisterLocationListener(OnLocationListener onLocationListener) {
        listeners.remove(onLocationListener);
    }

    public interface OnLocationListener {
        void onLocation(BDLocation bdLocation, LocationClient mLocationClient);
    }

    private class MyObserver implements LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void onDestroy() {

        }
    }

}
