package com.yuyang.messi.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

import com.yuyang.messi.MessiApp;

/**
 * 创建者: yuyang
 * 创建日期: 2015-06-30
 * 创建时间: 09:14
 * NetworkManager:网络状态管理类
 *
 * @author yuyang
 * @version 1.0
 */
public class NetworkManager {

    private NetworkManager() {
    }

    public static boolean hasNetwork() {
        try {
            ConnectivityManager cm = (ConnectivityManager) MessiApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo.State wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            NetworkInfo.State mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

            if (wifiState == NetworkInfo.State.CONNECTED || mobileState == NetworkInfo.State.CONNECTED) {
                return true;
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isNetworkAvailable() {
        try {
            ConnectivityManager cm = (ConnectivityManager) MessiApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo[] info = cm.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo networkInfo : info) {
                        if (networkInfo.getState() == State.CONNECTED || networkInfo.getState() == State.CONNECTING) {
                            return true;
                        }
//                        if (networkInfo.isAvailable() && networkInfo.isConnected()) {
//                            return true;
//                        }
                    }
                }
            }
            return false;
        } catch (Exception var4) {
            return true;
        }
    }

    // 判断移动数据是否可用
    public static boolean isMobileDataEnable() {
        ConnectivityManager cm = (ConnectivityManager) MessiApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            return cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        } else {
            return false;
        }
    }

    // 判断Wifi是否可用
    public static boolean isWifiDataEnable() {
        ConnectivityManager cm = (ConnectivityManager) MessiApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            return cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        }
        return false;
    }

    /**
     * 判断当前网络是否为Wifi
     */
    public boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager) MessiApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }
}
