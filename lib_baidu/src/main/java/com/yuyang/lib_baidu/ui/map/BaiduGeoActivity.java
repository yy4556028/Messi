package com.yuyang.lib_baidu.ui.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.yuyang.lib_baidu.R;
import com.yuyang.lib_baidu.utils.GeoCoderUtil;


/**
 * http://blog.csdn.net/qq_26787115/article/details/50358037#comments
 */
public class BaiduGeoActivity extends AppCompatActivity {

    private MapView mapView;
    private BaiduMap baiduMap = null;

    private EditText cityEdit;
    private EditText addressEdit;
    private Button geoCodeBtn;

    private MyBroadcastReceiver receiver;

    private final  OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
        @Override
        public void onGetGeoCodeResult(GeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(BaiduGeoActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                        .show();
                return;
            }
            baiduMap.clear();
            baiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka)));
            baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));

            String strInfo = String.format("纬度：%f 经度：%f", result.getLocation().latitude, result.getLocation().longitude);
            Toast.makeText(BaiduGeoActivity.this, strInfo, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(BaiduGeoActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                        .show();
                return;
            }
            baiduMap.clear();
            baiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_marka)));
            baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));

            Toast.makeText(BaiduGeoActivity.this, result.getAddress(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu_geo);
        initView();
        initEvent();
        initBaidu();
        registerReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    private void initView() {
        mapView = (MapView) findViewById(R.id.activity_baidu_mapView);
        cityEdit = (EditText) findViewById(R.id.activity_baidu_city);
        addressEdit = (EditText) findViewById(R.id.activity_baidu_address);
        geoCodeBtn = (Button) findViewById(R.id.activity_baidu_geoCode);

        baiduMap = mapView.getMap();
    }

    private void initEvent() {
        geoCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeoCoderUtil.geoCode(cityEdit.getText().toString(), addressEdit.getText().toString(), listener);
            }
        });

        geoCodeBtn.postDelayed(new Runnable() {
            @Override
            public void run() {
                GeoCoderUtil.geoCode("中国", "中国", listener);

            }
        }, 5000);
    }

    private void initBaidu() {
        //描述地图将要发生的变化，使用工厂类MapStatusUpdateFactory创建，设置级别
        //为5，进去就是5了，默认是12
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(new LatLng(37.550334, 104.114121), 5);
        baiduMap.setMapStatus(msu);
        //        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(5).build()));
//        baiduMap.setMaxAndMinZoomLevel(5, 5);

        //是否显示缩放按钮
        mapView.showZoomControls(false);
        //显示指南针
        baiduMap.getUiSettings().setCompassEnabled(true);
        //显示位置
        //baiduMap.getUiSettings().setCompassPosition(new Point(x, y));

        //设置地图单击监听
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }

            @Override
            public void onMapPoiClick(MapPoi mapPoi) {

            }
        });

        //覆盖物点击事件
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });

        //设置地图双击监听
        baiduMap.setOnMapDoubleClickListener(new BaiduMap.OnMapDoubleClickListener() {
            @Override
            public void onMapDoubleClick(LatLng latLng) {

            }
        });

        //发起截图请求
        baiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {

            }
        });
    }

    private void registerReceiver() {
        receiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        // 网络错误
        filter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        // 效验key失败
        filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        registerReceiver(receiver, filter);
    }

    class MyBroadcastReceiver extends BroadcastReceiver {
        //实现一个广播
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 网络错误
            if (action.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                Toast.makeText(BaiduGeoActivity.this, "无法连接网络", Toast.LENGTH_SHORT).show();
                // key效验失败
            } else if (action.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                Toast.makeText(BaiduGeoActivity.this, "百度地图key效验失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
