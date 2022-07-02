package com.yuyang.messi.ui.category;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GpsActivity extends AppBaseActivity {

    private static final String TAG = GpsActivity.class.getSimpleName();

    private TextView showView;
    private TextView infoText;
    private LocationManager locationManager;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());

    private final ActivityResultLauncher<String[]> permissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    List<String> deniedAskList = new ArrayList<>();
                    List<String> deniedNoAskList = new ArrayList<>();
                    for (Map.Entry<String, Boolean> stringBooleanEntry : result.entrySet()) {
                        if (!stringBooleanEntry.getValue()) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), stringBooleanEntry.getKey())) {
                                deniedAskList.add(stringBooleanEntry.getKey());
                            } else {
                                deniedNoAskList.add(stringBooleanEntry.getKey());
                            }
                        }
                    }

                    if (deniedAskList.size() == 0 && deniedNoAskList.size() == 0) {//全通过
                        // 为获取地理位置信息时设置查询条件
                        String bestProvider = locationManager.getBestProvider(getCriteria(), true);
                        // 获取位置信息
                        // 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
                        Location location = locationManager.getLastKnownLocation(bestProvider);
                        updateView(location);
                        // 监听状态
                        locationManager.addGpsStatusListener(listener);
                        // 绑定监听，有4个参数
                        // 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
                        // 参数2，位置信息更新周期，单位毫秒
                        // 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
                        // 参数4，监听
                        // 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新

                        // 1秒更新一次，或最小位移变化超过1米更新一次；
                        // 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
                    } else if (deniedNoAskList.size() > 0) {
                        finish();
                    } else {
                        finish();
                    }
                }
            });

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gps;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("GPS");

        showView = findViewById(R.id.activity_gps_show);
        infoText = findViewById(R.id.activity_gps_info);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 判断GPS是否正常启动
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
            // 返回开启GPS导航设置界面
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), null)
                    .launch(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return;
        }

        permissionsLauncher.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION});
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    // 位置监听
    private final LocationListener locationListener = new LocationListener() {

        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
            updateView(location);
            Log.i(TAG, "时间：" + sdf.format(new Date(Long.parseLong(location.getTime() + ""))));
            Log.i(TAG, "经度：" + location.getLongitude());
            Log.i(TAG, "纬度：" + location.getLatitude());
            Log.i(TAG, "海拔：" + location.getAltitude());
            updateInfo("时间：" + sdf.format(new Date(Long.parseLong(location.getTime() + ""))));
            updateInfo("经度：" + location.getLongitude());
            updateInfo("纬度：" + location.getLatitude());
            updateInfo("海拔：" + location.getAltitude());
        }

        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                // GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    Log.i(TAG, "当前GPS状态为可见状态");
                    updateInfo("当前GPS状态为可见状态");
                    break;
                // GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i(TAG, "当前GPS状态为服务区外状态");
                    updateInfo("当前GPS状态为服务区外状态");
                    break;
                // GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i(TAG, "当前GPS状态为暂停服务状态");
                    updateInfo("当前GPS状态为暂停服务状态");
                    break;
            }
        }

        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
            Location location = locationManager.getLastKnownLocation(provider);
            updateView(location);
        }

        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
            updateView(null);
        }

    };

    // 状态监听
    private final GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                // 第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.i(TAG, "第一次定位");
                    updateInfo("第一次定位");
                    break;
                // 卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.i(TAG, "卫星状态改变");
                    updateInfo("卫星状态改变");
                    // 获取当前状态
                    GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                    // 获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    // 创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        count++;
                    }
                    updateInfo("搜索到：" + count + "颗卫星");
                    break;
                // 定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.i(TAG, "定位启动");
                    updateInfo("定位启动");
                    break;
                // 定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.i(TAG, "定位结束");
                    updateInfo("定位结束");
                    break;
            }
        }

        ;
    };

    /**
     * 实时更新文本内容
     *
     * @param location
     */
    private void updateView(Location location) {
        if (location != null) {
            showView.setText("经度：" + String.valueOf(location.getLongitude()));
            showView.append("\n纬度：" + String.valueOf(location.getLatitude()));
        } else {
            showView.setText("");
        }
    }

    /**
     * 实时更新文本内容
     */
    private void updateInfo(String info) {
        infoText.append("\n" + sdf.format(new Date(System.currentTimeMillis())) + "> " + info);
    }

    /**
     * 返回查询条件
     *
     * @return
     */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }
}