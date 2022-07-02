package com.yuyang.messi.ui.category;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

/**
 * https://www.jianshu.com/p/68b20ea18ba3
 */
public class FaceLocationActivity extends AppBaseActivity {

    private LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_blur;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvents();
        initFaceLocation();
    }

    private void ii() {
        LocationProvider provider = mLocationManager.getProvider(LocationManager.GPS_PROVIDER);


    }

    private void initFaceLocation() {

        mLocationManager.addTestProvider(
                LocationManager.GPS_PROVIDER,
                false,
                true,
                true,
                false,
                true,
                true,
                true,
                Criteria.NO_REQUIREMENT,
                Criteria.ACCURACY_COARSE);

        // 创建新的Location对象，并设定必要的属性值
        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(39.820015);
        newLocation.setLongitude(116.813752);
        newLocation.setAccuracy(500);
        newLocation.setAltitude(55.0D);
        newLocation.setBearing(1.0F);
        newLocation.setTime(System.currentTimeMillis());
        // 这里一定要设置nonasecond单位的值，否则是没法持续收到监听的
        newLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());

        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // 开启测试Provider
            mLocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
        }

        mLocationManager.setTestProviderStatus(
                LocationManager.GPS_PROVIDER,
                LocationProvider.AVAILABLE,
                null,
                System.currentTimeMillis());

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3000,
                10.0f,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {

                    }
                });

        // 设置最新位置，一定要在requestLocationUpdate完成后进行，才能收到监听
        mLocationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, newLocation);

    }

    private void initView() {
    }

    private void initEvents() {
    }

    //模拟位置权限是否开启
    public boolean isAllowMockLocation() {
        boolean canMockPosition = false;
        if (Build.VERSION.SDK_INT <= 22) {//6.0以下
            canMockPosition = Settings.Secure.getInt(getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0;
        } else {
            try {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);//获得LocationManager引用
                String providerStr = LocationManager.GPS_PROVIDER;
                LocationProvider provider = locationManager.getProvider(providerStr);
                if (provider != null) {
                    locationManager.addTestProvider(
                            provider.getName()
                            , provider.requiresNetwork()
                            , provider.requiresSatellite()
                            , provider.requiresCell()
                            , provider.hasMonetaryCost()
                            , provider.supportsAltitude()
                            , provider.supportsSpeed()
                            , provider.supportsBearing()
                            , provider.getPowerRequirement()
                            , provider.getAccuracy());
                } else {
                    locationManager.addTestProvider(
                            providerStr
                            , true, true, false, false, true, true, true
                            , Criteria.POWER_HIGH, Criteria.ACCURACY_FINE);
                }
                locationManager.setTestProviderEnabled(providerStr, true);
                locationManager.setTestProviderStatus(providerStr, LocationProvider.AVAILABLE, null, System.currentTimeMillis());
                // 模拟位置可用
                canMockPosition = true;
                locationManager.setTestProviderEnabled(providerStr, false);
                locationManager.removeTestProvider(providerStr);
            } catch (SecurityException e) {
                canMockPosition = false;
            }
        }
        return canMockPosition;
    }
}

