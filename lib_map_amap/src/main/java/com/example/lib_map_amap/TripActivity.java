package com.example.lib_map_amap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.example.lib_map_amap.bean.ProvinceBean;
import com.example.lib_map_amap.bean.TripBean;
import com.example.lib_map_amap.utils.AssetsUtil;
import com.example.lib_map_amap.utils.DatePickerView;
import com.example.lib_map_amap.view.MarqueeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public class TripActivity extends AppCompatActivity {

    private static final String TAG = TripActivity.class.getSimpleName();

    private static final String TRIP_HEIGHT_NAME = "bottom_height";
    private static final String TRIP_HEIGHT_KEY = "bottom_height_key";
    private static final String TRIP_TYPE = "trip_type";

    private String type;//0国内 1国外

    private View rootLyt;
    private Toolbar toolbar;
    private MapView mapView;
    private AMap aMap;

    private MarqueeView tipText;
    private View dragLyt;
    private View filterLyt;
    private ImageView anchorImage;
    private View filterView;

    private View provinceLyt;
    private TextView provinceText;
    private TextView timesText;
    private TextView enterExitText;

    private View playerLyt;
    private ImageView playImage;
    private ImageView stopImage;
    private SeekBar seekBar;

    private RecyclerView recyclerView;
    private View filterDateLyt;
    private TextView startDateText;
    private TextView endDateText;
    private TextView tvSure;

    private TripRecyclerAdapter adapter;

    private List<TripBean> tripList;
    private Map<String, List<TripBean>> tripMap = new HashMap<>();

    private List<Map.Entry<String, List<TripBean>>> playTripList = new ArrayList<>();

    private List<ProvinceBean> provinceBeanList;

    private boolean isTrackingTouch = false;

    private String currentProvinceName = null;

    private boolean cityMarkerClicked = false;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        if (TextUtils.isEmpty(type = getIntent().getStringExtra(TRIP_TYPE))) {
            finish();
            return;
        }
        initView(savedInstanceState);
        initEvent();
        if (type.equals("1")) {
            MapsInitializer.loadWorldGridMap(true);
        }
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

    public static void launcherActivity(Context context, String type) {
        Intent intent = new Intent(context, TripActivity.class);
        intent.putExtra(TripActivity.TRIP_TYPE, type);
        context.startActivity(intent);
    }

    private void initView(Bundle savedInstanceState) {
        rootLyt = findViewById(R.id.activity_trip_rootLyt);

        toolbar = findViewById(R.id.activity_trip_toolbar);
        toolbar.setTitle(type.equals("0") ? "国内访问" : "国外访问");

        mapView = findViewById(R.id.activity_trip_mapView);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }

        tipText = findViewById(R.id.activity_trip_tipText);
        tipText.setText(type.equals("0") ? "自2012年12月至目前，习近平共在国内考察44次" : "自2013年3月至目前，习近平共出访25次");

        dragLyt = findViewById(R.id.activity_trip_dragLyt);
        filterLyt = findViewById(R.id.activity_trip_filterLyt);
        anchorImage = findViewById(R.id.activity_trip_anchor);
        filterView = findViewById(R.id.activity_trip_filterView);
        provinceLyt = findViewById(R.id.activity_trip_provinceLyt);
        provinceText = findViewById(R.id.activity_trip_provinceText);
        timesText = findViewById(R.id.activity_trip_timesText);
        enterExitText = findViewById(R.id.activity_trip_enterExitText);
        playerLyt = findViewById(R.id.activity_trip_playerLyt);
        playImage = findViewById(R.id.activity_trip_player_play);
        stopImage = findViewById(R.id.activity_trip_player_stop);
        seekBar = findViewById(R.id.activity_trip_player_seekBar);
        recyclerView = findViewById(R.id.activity_trip_recyclerView);
        filterDateLyt = findViewById(R.id.activity_trip_filter_dateLyt);
        startDateText = findViewById(R.id.activity_trip_filter_startDateText);
        endDateText = findViewById(R.id.activity_trip_filter_endDateText);
        tvSure = findViewById(R.id.tvSure);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter = new TripRecyclerAdapter(this, type));

        startDateText.setText("2012-01-01");
        endDateText.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));

        playImage.setTag("play");
    }

    private int downY;

    private void initEvent() {
        filterLyt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float currentY = event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        downY = (int) event.getY();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP: {

                        int preHeight = dragLyt.getLayoutParams().height;
                        dragLyt.getLayoutParams().height -= (currentY - downY);

                        if (dragLyt.getLayoutParams().height < preHeight) {
                            anchorImage.setImageResource(R.drawable.icon_anchor_down);
                        } else if (dragLyt.getLayoutParams().height > preHeight) {
                            anchorImage.setImageResource(R.drawable.icon_anchor_up);
                        }

                        if (dragLyt.getLayoutParams().height < filterLyt.getMeasuredHeight()) {
                            anchorImage.setImageResource(R.drawable.icon_anchor_up);
                            dragLyt.getLayoutParams().height = filterLyt.getMeasuredHeight();
                        } else if (dragLyt.getLayoutParams().height > rootLyt.getMeasuredHeight() - toolbar.getMeasuredHeight() - tipText.getMeasuredHeight() + Resources.getSystem().getDisplayMetrics().density * 10) {
                            anchorImage.setImageResource(R.drawable.icon_anchor_down);
                            dragLyt.getLayoutParams().height = (int) (rootLyt.getMeasuredHeight() - toolbar.getMeasuredHeight() - tipText.getMeasuredHeight() + Resources.getSystem().getDisplayMetrics().density * 10);
                        }
                        dragLyt.requestLayout();
                        break;
                    }
                }
                return true;
            }
        });
        filterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filterDateLyt.getVisibility() == View.GONE) {
                    provinceLyt.setVisibility(View.GONE);
                    filterDateLyt.setVisibility(View.VISIBLE);
                } else {
                    provinceLyt.setVisibility(View.VISIBLE);
                    filterDateLyt.setVisibility(View.GONE);
                }
            }
        });
        enterExitText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enterExitText.getText().equals("进入")) {
                    if (adapter.getItemCount() == 0) return;

                    currentProvinceName = provinceText.getText().toString();
                    enterExitText.setText("退出");
                    enterExitText.setTextColor(Color.WHITE);
                    enterExitText.setBackgroundColor(getResources().getColor(R.color.theme));

                    List<TripBean> beanList = tripMap.get(currentProvinceName);
                    if (beanList != null && beanList.size() > 0) {
                        TripBean tripBean = beanList.get(0);//跳转到最新访问过的城市
                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(tripBean.getCitylat(), tripBean.getCitylng()), type.equals("0") ? 7 : 7));//比例尺:国内5 国外3
                    }
                    aMap.clear();

                    initMarkers_city(beanList, null);
                } else {

                    if (cityMarkerClicked) {
                        cityMarkerClicked = false;
//                        List<TripBean> beanList = tripMap.get(currentProvinceName);
//                        if (beanList != null && beanList.size() > 0) {
//                            TripBean tripBean = beanList.get(0);
//                            aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(tripBean.getCitylat(), tripBean.getCitylng())));//比例尺:国内5 国外3
//                        }
                        aMap.clear();

                        refreshUI(currentProvinceName, null, 1);
                        return;
                    }

                    enterExitText.setText("进入");
                    enterExitText.setTextColor(ContextCompat.getColor(TripActivity.this, R.color.theme));
                    enterExitText.setBackgroundColor(Color.WHITE);

                    List<TripBean> beanList = tripMap.get(currentProvinceName);
                    if (beanList != null && beanList.size() > 0) {
                        TripBean tripBean = beanList.get(0);
                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(tripBean.getCitylat(), tripBean.getCitylng()), type.equals("0") ? 5 : 3));//比例尺:国内5 国外3
                    }
                    refreshUI(currentProvinceName, null, 0);
                    currentProvinceName = null;
                }
            }
        });
        adapter.setOnItemClickListener(new TripRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TripBean tripBean) {
                Toast.makeText(TripActivity.this, tripBean.getNewstitle(), Toast.LENGTH_SHORT).show();
//                NewsBean newsBean = new NewsBean();
//                newsBean.setTypeno(tripBean.getTypeno());
//                newsBean.setNewstitle(tripBean.getNewstitle());
//                newsBean.setDate(tripBean.getDate());
//                newsBean.setNewsno(tripBean.getNewsno());
//                newsBean.setNewstype(tripBean.getNewstype());
//                NewsActivity.goMyActivity(getActivity(), newsBean, null, null);
            }
        });

        startDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerView.pickDate(
                        new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
                        startDateText,
                        null);
            }
        });
        endDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerView.pickDate(
                        new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
                        endDateText,
                        null);
            }
        });
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startDate = startDateText.getText().toString();
                String endDate = endDateText.getText().toString();
                if (startDate.compareTo(endDate) >= 0) {
                    Toast.makeText(TripActivity.this, "开始日期不能大于结束日期", Toast.LENGTH_SHORT).show();
                    return;
                }
                filterDateLyt.setVisibility(View.GONE);
                filterLyt.setVisibility(View.GONE);
                provinceLyt.setVisibility(View.GONE);
                playerLyt.setVisibility(View.VISIBLE);
                loadPlayData(startDate, endDate);
            }
        });

        playImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playImage.getTag().equals("play")) {
                    seekBarHandler.removeCallbacksAndMessages(null);
                    playImage.setTag("pause");
                    playImage.setImageResource(R.drawable.icon_play);
                } else {
                    seekBarHandler.sendEmptyMessageDelayed(0, 3000);
                    playImage.setTag("play");
                    playImage.setImageResource(R.drawable.icon_pause);
                }
            }
        });

        stopImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterLyt.setVisibility(View.VISIBLE);
                playerLyt.setVisibility(View.GONE);
                provinceLyt.setVisibility(View.VISIBLE);
                seekBarHandler.removeCallbacksAndMessages(null);
                playImage.setTag("play");
                playImage.setImageResource(R.drawable.icon_pause);

                if (enterExitText.getText().equals("进入")) {
                    List<TripBean> beanList = tripMap.get(provinceText.getText().toString());
                    if (beanList != null && beanList.size() > 0) {
                        TripBean tripBean = beanList.get(0);
                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(tripBean.getCitylat(), tripBean.getCitylng()), type.equals("0") ? 5 : 3));//比例尺:国内5 国外3
                    }
                    refreshUI(provinceText.getText().toString(), null, 0);
                } else {
                    List<TripBean> beanList = tripMap.get(currentProvinceName);
                    if (beanList != null && beanList.size() > 0) {
                        TripBean tripBean = beanList.get(0);
                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(tripBean.getCitylat(), tripBean.getCitylng()), type.equals("0") ? 7 : 7));//比例尺:国内5 国外3
                    }
                    refreshUI(currentProvinceName, null, 1);
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!isTrackingTouch) {

                    if (playTripList.size() == 0) {
                        adapter.setData(null, 0);
                        timesText.setText("0次");
                        aMap.clear();
                        return;
                    }

                    ProvinceBean bean = null;
                    for (ProvinceBean provinceBean : provinceBeanList) {
                        if (provinceBean.getArriveprovince().equals(playTripList.get(seekBar.getProgress()).getKey())) {
                            bean = provinceBean;
                            break;
                        }
                    }
                    LatLng latlng;
                    String provinceStr = null;
                    if (bean != null) {
                        latlng = new LatLng(bean.getProvincelat(), bean.getProvincelng());
                        provinceStr = bean.getArriveprovince();
                    } else {
                        TripBean tripBean = playTripList.get(seekBar.getProgress()).getValue().get(0);
                        latlng = new LatLng(tripBean.getCitylat(), tripBean.getCitylng());
                        provinceStr = type.equals("0") ? tripBean.getArriveProvince() : tripBean.getNatioinCN();
                    }
                    aMap.animateCamera(CameraUpdateFactory.changeLatLng(latlng), 1000, new AMap.CancelableCallback() {
                        @Override
                        public void onFinish() {

                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                    refreshUI(provinceStr, null, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTrackingTouch = true;
                seekBarHandler.removeCallbacksAndMessages(null);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTrackingTouch = false;
                seekBarHandler.sendEmptyMessageDelayed(0, 3000);

                if (playTripList.size() == 0) {
                    adapter.setData(null, 0);
                    timesText.setText("0次");
                    aMap.clear();
                    return;
                }

                ProvinceBean bean = null;
                for (ProvinceBean provinceBean : provinceBeanList) {
                    if (provinceBean.getArriveprovince().equals(playTripList.get(seekBar.getProgress()).getKey())) {
                        bean = provinceBean;
                        break;
                    }
                }
                LatLng latlng;
                String provinceStr = null;
                if (bean != null) {
                    latlng = new LatLng(bean.getProvincelat(), bean.getProvincelng());
                    provinceStr = bean.getArriveprovince();
                } else {
                    TripBean tripBean = playTripList.get(seekBar.getProgress()).getValue().get(0);
                    latlng = new LatLng(tripBean.getCitylat(), tripBean.getCitylng());
                    provinceStr = type.equals("0") ? tripBean.getArriveProvince() : tripBean.getNatioinCN();
                }
                aMap.animateCamera(CameraUpdateFactory.changeLatLng(latlng), 1000, new AMap.CancelableCallback() {
                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onCancel() {

                    }
                });
                refreshUI(provinceStr, null, 0);
            }
        });
    }

    private void setUpMap() {
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);// logo位置
        uiSettings.setScaleControlsEnabled(false);//显示比例尺
        uiSettings.setZoomControlsEnabled(false);//显示缩放按钮+-
        uiSettings.setCompassEnabled(false);//显示指南针
        uiSettings.setMyLocationButtonEnabled(false);//定位按钮
//        aMap.setLocationSource(this);// 设置定位监听
//        aMap.setMyLocationEnabled(false);// 是否可触发定位并显示定位层
        uiSettings.setScrollGesturesEnabled(true);//设置地图是否可以手势滑动
        uiSettings.setZoomGesturesEnabled(true);//设置地图是否可以手势缩放大小

        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (playerLyt.getVisibility() == View.VISIBLE) {//如果在轮播页
                    if (playImage.getTag().equals("play")) {//如果正在轮播 return
                        return true;
                    } else {
                        refreshUI(marker.getTitle(), null, 0);
                        return true;
                    }
                }

                if (enterExitText.getText().equals("退出")) {//如果已经进入某地区地图
                    refreshUI(currentProvinceName, marker.getTitle(), 1);
                    cityMarkerClicked = true;
                    return true;
                } else {
                    refreshUI(marker.getTitle(), null, 0);
                    return true;
                }
            }
        });
    }

    private void initMarkers_province(String provinceStr) {
        aMap.clear();
        if (provinceBeanList == null) {
            String string = AssetsUtil.readFromAssets(this, type.equals("0") ? "arriveProvince.json" : "arriveCountry.json");
            provinceBeanList = JSON.parseArray(string, ProvinceBean.class);
        }

        Set<String> provinceNameSet = new HashSet<>();
        if (playerLyt.getVisibility() == View.VISIBLE) {//如果在轮播页
            for (Map.Entry<String, List<TripBean>> stringListEntry : playTripList) {
                provinceNameSet.add(stringListEntry.getKey());
            }
        }

        ProvinceBean highlightBean = null;

        for (ProvinceBean provinceBean : provinceBeanList) {

            if (!TextUtils.isEmpty(provinceStr) && provinceStr.equals(provinceBean.getArriveprovince())) {
                highlightBean = provinceBean;
                continue;
            } else if (playerLyt.getVisibility() == View.VISIBLE && !provinceNameSet.contains(provinceBean.getArriveprovince())) {
                continue;
            }

            View markerView = getLayoutInflater().inflate(R.layout.custom_marker_province, null);
            TextView textView = (TextView) markerView.findViewById(R.id.custom_marker_province_text);
            textView.setText(provinceBean.getArriveprovince());

            aMap.addMarker(new MarkerOptions()
                    .position(new LatLng(provinceBean.getProvincelat(), provinceBean.getProvincelng()))
                    .title(provinceBean.getArriveprovince())
//                    .snippet("")
//                    .draggable(true));
                    .icon(BitmapDescriptorFactory.fromView(markerView)));
            // marker旋转90度
//            marker.setRotateAngle(90);
        }

        if (highlightBean != null) {
            View markerView = getLayoutInflater().inflate(R.layout.custom_marker_province, null);
            TextView textView = (TextView) markerView.findViewById(R.id.custom_marker_province_text);
            textView.setText(highlightBean.getArriveprovince());

            View rootLyt = markerView.findViewById(R.id.custom_marker_province_lyt);
            rootLyt.setBackgroundColor(Color.YELLOW);

            aMap.addMarker(new MarkerOptions()
                    .position(new LatLng(highlightBean.getProvincelat(), highlightBean.getProvincelng()))
                    .title(highlightBean.getArriveprovince())
//                    .snippet("")
//                    .draggable(true));
                    .icon(BitmapDescriptorFactory.fromView(markerView)));
        }
    }

    private void initMarkers_city(List<TripBean> beanList, String cityStr) {
        aMap.clear();

        Set<String> cityStrSet = new HashSet<>();

        if (beanList != null) {
            for (TripBean tripBean : beanList) {
                if (!cityStrSet.contains(tripBean.getArrivecity())) {
                    cityStrSet.add(tripBean.getArrivecity());
                    View markerView = getLayoutInflater().inflate(R.layout.custom_marker_city, null);

                    if (!TextUtils.isEmpty(cityStr) && cityStr.equals(tripBean.getArrivecity())) {
                        markerView.findViewById(R.id.custom_marker_city_flag).setVisibility(View.VISIBLE);
                    }

                    aMap.addMarker(new MarkerOptions()
                            .position(new LatLng(tripBean.getCitylat(), tripBean.getCitylng()))
                            .title(tripBean.getArrivecity())
                            .icon(BitmapDescriptorFactory.fromView(markerView)));
                }
            }
        }
    }

    private void loadData() {

//        if (progressDialog == null) {
//            progressDialog = new CustomProgressDialog(this);
//            progressDialog.setCancelable(false);
//            progressDialog.setCanceledOnTouchOutside(false);
//        }
//        progressDialog.show("");
//
//        HttpUtils httpUtils = new HttpUtils();
//        String url = Api.getTrip("2012-01-01", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()), type.equals("0") ? 0 : 1);
//        httpUtils.send(HttpRequest.HttpMethod.GET, url, null, new RequestCallBack<String>() {
//            @Override
//            public void onSuccess(ResponseInfo<String> responseInfo) {
//                progressDialog.dismiss();
//                parseResponse(responseInfo.result);
//            }
//
//            @Override
//            public void onFailure(HttpException e, String s) {
//                progressDialog.dismiss();
//                Utils.toast("请求服务器失败");
//                initMarkers_province(null);
//            }
//        });
    }

    private void parseResponse(String result) {
        if (TextUtils.isEmpty(result)) return;

        try {
            JSONObject jsonObject = new JSONObject(result);

            if (1 != jsonObject.optInt("code")) {
                if (!TextUtils.isEmpty(jsonObject.optString("msg"))) {
                    Toast.makeText(this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                }
                return;
            }

            tripList = JSON.parseArray(jsonObject.getString("data"), TripBean.class);

            TripBean newestTripBean = null;

            if (type.equals("0")) {
                for (TripBean bean : tripList) {
                    if (tripMap.get(bean.getArriveProvince()) == null) {
                        tripMap.put(bean.getArriveProvince(), new ArrayList<TripBean>());
                    }
                    tripMap.get(bean.getArriveProvince()).add(bean);
                }

                for (Map.Entry<String, List<TripBean>> entry : tripMap.entrySet()) {
                    List<TripBean> provinceNationList = entry.getValue();
                    Collections.sort(provinceNationList, new Comparator<TripBean>() {
                        @Override
                        public int compare(TripBean o1, TripBean o2) {
                            int comeTimesProvince = o1.getComeTimesProvince() - o2.getComeTimesProvince();
                            if (comeTimesProvince != 0) {
                                return -comeTimesProvince;
                            } else {
                                boolean weibo1 = o1.getSourceTag().equals("微博");
                                boolean weibo2 = o2.getSourceTag().equals("微博");
                                if (!weibo1 && weibo2) {
                                    return -1;
                                } else if (weibo1 && !weibo2) {
                                    return 1;
                                } else {
                                    return -o1.getDateTime().compareTo(o2.getDateTime());
                                }
                            }
                        }
                    });

                    if (newestTripBean == null || newestTripBean.getDateTime().compareTo(provinceNationList.get(0).getDateTime()) < 0) {
                        newestTripBean = provinceNationList.get(0);
                    }
                }
            } else {
                for (TripBean bean : tripList) {
                    if (tripMap.get(bean.getNatioinCN()) == null) {
                        tripMap.put(bean.getNatioinCN(), new ArrayList<TripBean>());
                    }
                    tripMap.get(bean.getNatioinCN()).add(bean);
                }

                for (Map.Entry<String, List<TripBean>> entry : tripMap.entrySet()) {
                    List<TripBean> provinceNationList = entry.getValue();
                    Collections.sort(provinceNationList, new Comparator<TripBean>() {
                        @Override
                        public int compare(TripBean o1, TripBean o2) {
                            int comeTimesNation = o1.getComeTimesNation() - o2.getComeTimesNation();
                            if (comeTimesNation != 0) {
                                return -comeTimesNation;
                            } else {
                                boolean weibo1 = o1.getSourceTag().equals("微博");
                                boolean weibo2 = o2.getSourceTag().equals("微博");
                                if (!weibo1 && weibo2) {
                                    return -1;
                                } else if (weibo1 && !weibo2) {
                                    return 1;
                                } else {
                                    return -o1.getDateTime().compareTo(o2.getDateTime());
                                }
                            }
                        }
                    });

                    if (newestTripBean == null || newestTripBean.getDateTime().compareTo(provinceNationList.get(0).getDateTime()) < 0) {
                        newestTripBean = provinceNationList.get(0);
                    }
                }
            }

            if (tripList.size() == 0 || newestTripBean == null) return;
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(newestTripBean.getCitylat(), newestTripBean.getCitylng()), type.equals("0") ? 5 : 3));//比例尺:国内5 国外3

            String provinceName = type.equals("0") ? newestTripBean.getArriveProvince() : newestTripBean.getNatioinCN();
            refreshUI(provinceName, null, 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadPlayData(String startDate, String endDate) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.show();


//        HttpUtils httpUtils = new HttpUtils();
//        String url = Api.getTrip(startDate, endDate, type.equals("0") ? 0 : 1);
//        httpUtils.send(HttpRequest.HttpMethod.GET, url, null, new RequestCallBack<String>() {
//            @Override
//            public void onSuccess(ResponseInfo<String> responseInfo) {
//                progressDialog.dismiss();
//                parsePlayResponse(responseInfo.result);
//            }
//
//            @Override
//            public void onFailure(HttpException e, String s) {
//                progressDialog.dismiss();
//                Utils.toast("请求服务器失败");
//            }
//        });
    }

    private void parsePlayResponse(String result) {
        if (TextUtils.isEmpty(result)) return;

        try {
            JSONObject jsonObject = new JSONObject(result);

            if (1 != jsonObject.optInt("code")) {
                if (!TextUtils.isEmpty(jsonObject.optString("msg"))) {
                    Toast.makeText(this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                }
                return;
            }

            List<TripBean> beanList = JSON.parseArray(jsonObject.getString("data"), TripBean.class);

            Map<String, List<TripBean>> playTripMap = new HashMap<>();

            if (type.equals("0")) {
                for (TripBean bean : beanList) {
                    if (playTripMap.get(bean.getArriveProvince()) == null) {
                        playTripMap.put(bean.getArriveProvince(), new ArrayList<TripBean>());
                    }
                    playTripMap.get(bean.getArriveProvince()).add(bean);
                }

                for (Map.Entry<String, List<TripBean>> entry : playTripMap.entrySet()) {
                    List<TripBean> provinceList = entry.getValue();
                    Collections.sort(provinceList, new Comparator<TripBean>() {
                        @Override
                        public int compare(TripBean o1, TripBean o2) {
                            int comeTimesProvince = o1.getComeTimesProvince() - o2.getComeTimesProvince();
                            if (comeTimesProvince != 0) {
                                return -comeTimesProvince;
                            } else {
                                boolean weibo1 = o1.getSourceTag().equals("微博");
                                boolean weibo2 = o2.getSourceTag().equals("微博");
                                if (!weibo1 && weibo2) {
                                    return -1;
                                } else if (weibo1 && !weibo2) {
                                    return 1;
                                } else {
                                    return -o1.getDateTime().compareTo(o2.getDateTime());
                                }
                            }
                        }
                    });
                }
            } else {
                for (TripBean bean : beanList) {
                    if (playTripMap.get(bean.getNatioinCN()) == null) {
                        playTripMap.put(bean.getNatioinCN(), new ArrayList<TripBean>());
                    }
                    playTripMap.get(bean.getNatioinCN()).add(bean);
                }

                for (Map.Entry<String, List<TripBean>> entry : playTripMap.entrySet()) {
                    List<TripBean> nationList = entry.getValue();
                    Collections.sort(nationList, new Comparator<TripBean>() {
                        @Override
                        public int compare(TripBean o1, TripBean o2) {
                            int comeTimesProvince = o1.getComeTimesProvince() - o2.getComeTimesProvince();
                            if (comeTimesProvince != 0) {
                                return -comeTimesProvince;
                            } else {
                                boolean weibo1 = o1.getSourceTag().equals("微博");
                                boolean weibo2 = o2.getSourceTag().equals("微博");
                                if (!weibo1 && weibo2) {
                                    return -1;
                                } else if (weibo1 && !weibo2) {
                                    return 1;
                                } else {
                                    return -o1.getDateTime().compareTo(o2.getDateTime());
                                }
                            }
                        }
                    });
                }
            }

            playTripList.clear();
            for (Map.Entry<String, List<TripBean>> entry : playTripMap.entrySet()) {
                playTripList.add(entry);
            }

            Collections.sort(playTripList, new Comparator<Map.Entry<String, List<TripBean>>>() {
                @Override
                public int compare(Map.Entry<String, List<TripBean>> o1, Map.Entry<String, List<TripBean>> o2) {
                    return -o1.getValue().get(0).getDateTime().compareTo(o2.getValue().get(0).getDateTime());
                }
            });

            seekBar.setMax(playTripMap.size() > 0 ? playTripMap.size() - 1 : 0);
            seekBar.setProgress(0);
            seekBarHandler.sendEmptyMessageDelayed(0, 3000);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param provinceName
     * @param cityName
     * @param markFlag     0 mark province 1 mark city
     */
    private void refreshUI(String provinceName, String cityName, int markFlag) {
        Log.e(TAG, "refreshUI : provinceName = " + provinceName + " cityName = " + cityName + " markFlag = " + markFlag);

        if (markFlag == 0) {
            provinceText.setText(provinceName);
            adapter.setData(tripMap.get(provinceName), markFlag);
            timesText.setText((type.equals("0") ?
                    ("考察" + tripMap.get(provinceName).get(0).getComeTimesProvince()) :
                    ("访问" + tripMap.get(provinceName).get(0).getComeTimesNation())) +
                    "次");
            initMarkers_province(provinceName);
        } else {
            if (TextUtils.isEmpty(cityName)) {
                provinceText.setText(provinceName);
                adapter.setData(tripMap.get(provinceName), markFlag);
                timesText.setText((type.equals("0") ?
                        ("考察" + tripMap.get(provinceName).get(0).getComeTimesProvince()) :
                        ("访问" + tripMap.get(provinceName).get(0).getComeTimesNation())) +
                        "次");
                initMarkers_city(tripMap.get(provinceName), null);
            } else {
                provinceText.setText(cityName);

                List<TripBean> targetCityList = new ArrayList<>();
                for (TripBean tripBean : tripMap.get(provinceName)) {
                    if (tripBean.getArrivecity().equals(cityName)) {
                        targetCityList.add(tripBean);
                    }
                }
                adapter.setData(targetCityList, markFlag);
                timesText.setText(type.equals("0") ? "考察" : "访问" + targetCityList.get(0).getComeTimesCity() + "次");
                initMarkers_city(tripMap.get(provinceName), cityName);
            }
        }
    }

    private Handler seekBarHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int progress = seekBar.getProgress();
            progress++;
            if (progress > seekBar.getMax()) {
                progress = 0;
            }
            seekBar.setProgress(progress);
            seekBarHandler.sendEmptyMessageDelayed(0, 3000);
        }
    };
}
