package com.yuyang.lib_baidu.ui.map;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yuyang.lib_baidu.R;
import com.yuyang.lib_baidu.utils.BaiduLocationUtil;
import com.yuyang.lib_baidu.utils.PoiSearchUtil;
import com.yuyang.lib_base.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * http://blog.csdn.net/qq_26787115/article/details/50358037#comments
 */
public class BaiduPoiSearchActivity extends BaseActivity {

    private MapView mapView;
    private BaiduMap baiduMap = null;

    private EditText editText;
    private Button searchBtn;
    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;

    private PoiSearchRecyclerAdapter recyclerAdapter;

    private int pageNum;
    private String keyword = "美食";
    private boolean isSearchInCity = false;

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
                        BaiduLocationUtil.getInstance().registerLocationListener(new BaiduLocationUtil.OnLocationListener() {
                            @Override
                            public void onLocation(BDLocation bdLocation, LocationClient mLocationClient) {
                                updateLocation(bdLocation);
                                PoiSearchUtil.getInstance().searchNearby(
                                        new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()),
                                        keyword,
                                        1000,
                                        pageNum);
                            }
                        });
                        BaiduLocationUtil.getInstance().startLocation(0);
                    } else if (deniedNoAskList.size() > 0) {
                        finish();
                    } else {
                        finish();
                    }
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu_poi_search);
        initView();
        initEvent();
        initBaidu();
        permissionsLauncher.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        BaiduLocationUtil.getInstance().stopLocation();
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
        mapView = findViewById(R.id.activity_baidu_poi_search_mapView);
        editText = findViewById(R.id.activity_baidu_poi_search_edit);
        searchBtn = findViewById(R.id.activity_baidu_poi_search_search);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        smartRefreshLayout.setEnableRefresh(false);
        smartRefreshLayout.setEnableLoadMore(true);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageNum = 0;
                if (isSearchInCity) {
                    PoiSearchUtil.getInstance().searchNearby(
                            new LatLng(BaiduLocationUtil.getInstance().getBdLocation().getLatitude(), BaiduLocationUtil.getInstance().getBdLocation().getLongitude()),
                            keyword,
                            1000,
                            pageNum);
                } else {
                    PoiSearchUtil.getInstance().searchInCity(
                            BaiduLocationUtil.getInstance().getBdLocation().getCity(), keyword, pageNum);
                }
            }
        });
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                pageNum++;
                if (isSearchInCity) {
                    PoiSearchUtil.getInstance().searchNearby(
                            new LatLng(BaiduLocationUtil.getInstance().getBdLocation().getLatitude(), BaiduLocationUtil.getInstance().getBdLocation().getLongitude()),
                            keyword,
                            1000,
                            pageNum);
                } else {
                    PoiSearchUtil.getInstance().searchInCity(
                            BaiduLocationUtil.getInstance().getBdLocation().getCity(), keyword, pageNum);
                }
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter = new PoiSearchRecyclerAdapter(this));
        baiduMap = mapView.getMap();
    }

    private void initEvent() {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSearchInCity = true;
                pageNum = 0;
                keyword = editText.getText().toString();
                PoiSearchUtil.getInstance().searchInCity(
                        BaiduLocationUtil.getInstance().getBdLocation().getCity(), keyword, pageNum);
            }
        });
        recyclerAdapter.setOnItemClickListener(new PoiSearchRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(int position) {
                PoiInfo poiInfo = recyclerAdapter.beanList.get(position);
                updateLocation(poiInfo.location, poiInfo.name);
            }
        });
        PoiSearchUtil.getInstance().setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
//                List<PoiAddrInfo> poiInfoList = poiResult.getAllAddr();
//                List<CityInfo> cityInfoList = poiResult.getSuggestCityList();
//                List<PoiInfo> poiList = poiResult.getAllPoi();
                int totalPageNum = poiResult.getTotalPageNum();
                int totalPoiNum = poiResult.getTotalPoiNum();
                int currentPageNum = poiResult.getCurrentPageNum();

                smartRefreshLayout.finishRefresh();
                smartRefreshLayout.finishLoadMore();
                smartRefreshLayout.setEnableLoadMore(currentPageNum + 1 < totalPageNum);
                if (pageNum == 0) {
                    recyclerAdapter.beanList.clear();
                }
                recyclerAdapter.addData(poiResult.getAllPoi());
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                poiDetailResult.describeContents();
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
                poiIndoorResult.getmArrayPoiInfo();
            }
        });
    }

    private void initBaidu() {
        //描述地图将要发生的变化，使用工厂类MapStatusUpdateFactory创建，设置级别
        //为5，进去就是5了，默认是12
//                baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(5).build()));
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

    private void updateLocation(BDLocation bdLocation) {
        updateLocation(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()), bdLocation.getAddrStr());
    }

    private void updateLocation(LatLng latLng, String addressStr) {
        if (baiduMap == null || latLng == null) return;
        baiduMap.clear();
        //设置位置marker
        baiduMap.addOverlay(
                new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka)));
        if (!TextUtils.isEmpty(addressStr)) {
            //显示位置名称
            baiduMap.addOverlay(
                    new TextOptions()
                            .position(latLng)
                            .text(addressStr)
                            .fontSize(24));
        }

        baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLng, 16));
    }
}
