package com.yuyang.lib_base.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import androidx.annotation.RequiresPermission;

import com.yuyang.lib_base.BaseApp;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class IPUtils {

    @SuppressLint("MissingPermission")
    public static String getIpAddress() {
        ConnectivityManager manager = (ConnectivityManager) BaseApp.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return getIp_Wifi();
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return getIp_Mobile();
            }
        }

        return "";
    }

    private static String getIp_Wifi() {
        // 获取WiFi服务
        WifiManager wifiManager = (WifiManager) BaseApp.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // 判断WiFi是否开启
        if (wifiManager.isWifiEnabled()) {
            // 已经开启了WiFi
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            try {
                return InetAddress.getByAddress(BigInteger.valueOf(ipAddress).toByteArray()).getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            return "";
        } else {
            // 未开启WiFi
            return "";
        }
    }

    private static String getIp_Mobile() {
        try {
            NetworkInterface networkInterface;
            InetAddress inetAddress;
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() &&  inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
            return "";
        } catch (SocketException ex) {
            ex.printStackTrace();
            return "";
        }
    }
}
