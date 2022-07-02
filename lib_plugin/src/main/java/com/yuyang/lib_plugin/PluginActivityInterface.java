package com.yuyang.lib_plugin;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

public interface PluginActivityInterface {

    /**
     * 绑定代理 Activity
     * @param proxyActivity
     */
    void attach(Activity proxyActivity);

    void onCreate(Bundle savedInstanceState);
    void onStart();
    void onResume();
    void onPause();
    void onStop();
    void onDestroy();
    void onSaveInstanceState(Bundle outState);
    boolean onTouchEvent(MotionEvent event);
    void onBackPressed();

}
