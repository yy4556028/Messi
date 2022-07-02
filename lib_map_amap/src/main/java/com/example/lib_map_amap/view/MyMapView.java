package com.example.lib_map_amap.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.MapView;


public class MyMapView extends MapView {
    private Context context;

    public MyMapView(Context context) {
        super(context);
    }

    public MyMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public MyMapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    public MyMapView(Context context, AMapOptions aMapOptions) {
        super(context, aMapOptions);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        //view加载完成时回调
        this.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        ViewGroup child = (ViewGroup) getChildAt(0);//地图框架
                        // child.getChildAt(0).setVisibility(View.VISIBLE);//地图
//                        child.getChildAt(2).setVisibility(View.GONE);//logo
                        // child.getChildAt(5).setVisibility(View.VISIBLE);//缩放按钮
                        // child.getChildAt(6).setVisibility(View.VISIBLE);//定位按钮
                        // child.getChildAt(7).setVisibility(View.VISIBLE);//指南针
                    }
                });
    }
}
