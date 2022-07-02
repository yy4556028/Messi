package com.example.lib_map_amap.utils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.yuyang.lib_base.BaseApp;
import com.yuyang.lib_base.utils.ToastUtil;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class AmapLocService {

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    //定位获取成功回调
    private static final int GET_LOCATION_SUCCESS = 1;
    public final String TAG = "AmapLocService";

    private Timer mTimer;

    //GPS定位上传间隔
    private static final int UPLOAD_LONG = 60 * 1000;
    private static final int UPLOAD_SHORT = 15 * 1000;


    public AmapLocService() {
        initLocation();
    }

    /**
     * 初始化定位组件
     */
    private void initLocation() {
        try {
            //初始化client
            locationClient = new AMapLocationClient(BaseApp.getInstance());
            locationOption = getOption();
            //设置定位参数
            locationClient.setLocationOption(locationOption);
            // 设置定位监听
            locationClient.setLocationListener(locationListener);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取定位组件配置
     */
    private AMapLocationClientOption getOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(true);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(10000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(60000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    /**
     * 定位监听
     */
    private final AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    Message locMsg = new Message();
                    locMsg.what = GET_LOCATION_SUCCESS;
                    locMsg.obj = location;
                    mHandler.sendMessage(locMsg);
                } else {
                    //定位失败
                    Log.d(TAG, "定位失败");
                }
            } else {
                //定位失败
                Log.d(TAG, "定位失败");
            }
        }
    };

    /**
     * rrCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
     * sb.append("定位成功" + "\n");
     * sb.append("定位类型: " + location.getLocationType() + "\n");
     * sb.append("经    度    : " + location.getLongitude() + "\n");
     * sb.append("纬    度    : " + location.getLatitude() + "\n");
     * sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
     * sb.append("提供者    : " + location.getProvider() + "\n");
     */
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what) {
                case GET_LOCATION_SUCCESS:
                    try {
                        AMapLocation info = (AMapLocation) msg.obj;
                        if (info != null && info.getLatitude() > 0) {
                            Log.i(TAG, "高德地图获取经纬度:" + info.getLongitude() + ","
                                    + info.getLatitude());
                            final String longitude = String.format(Locale.CHINA, "%.6f", info.getLongitude());
                            final String latitude = String.format(Locale.CHINA, "%.6f", info.getLatitude());
                            if (TextUtils.isEmpty(longitude)
                                    || longitude
                                    .toUpperCase().contains("E")) {
                                Log.i(TAG, "经度或纬度错误,不上传");
                            } else {
                                Log.i(TAG, "定位成功");
                                Log.i(TAG, "定位结果来源 " + info.getLocationType());
                                Log.i(TAG, "定位精度信息 " + info.getAccuracy());
                                Log.i(TAG, "定位的经度位置是：" + latitude);
                                Log.i(TAG, "定位的维度位置是：" + longitude);

//                                mLocationInfo.setLng(longitude);
//                                mLocationInfo.setLat(latitude);
//                                //刷新缓存中的经纬度信息
//                                SNTTEMAApplication.getInstance().setLocation(
//                                        longitude,
//                                        latitude
//                                );
//                                mLocationInfo.setSpeed(String.valueOf(info.getSpeed()));
//                                mLocationInfo.setBearing(String.valueOf(info.getBearing()));

                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 开始定位
     */
    public void startLocation() {
        if (locationClient != null && locationOption != null) {
            // 设置定位参数
            locationClient.setLocationOption(locationOption);
            // 启动定位
            locationClient.startLocation();

            mTimer = new Timer();
            TimerTask task = new TimerTask() {

                @Override
                public void run() {
                    //发送Handler中的GET_LOCATION_SUCCESS下获取的信息
                }
            };
            mTimer.scheduleAtFixedRate(task, 0, UPLOAD_SHORT);
        } else {
            ToastUtil.showToast("定位开启失败，请退出重试");
        }
    }

    /**
     * 停止定位
     */
    public void stopLocation() {
        if (locationClient != null) {
            // 停止定位
            locationClient.stopLocation();
        }
    }

    /**
     * 销毁定位
     */
    public void destroyLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.stopLocation();
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
        if (null != mTimer) {
            mTimer.cancel();
        }
    }
}