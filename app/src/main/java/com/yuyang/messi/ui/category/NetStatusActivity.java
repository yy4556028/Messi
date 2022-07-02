package com.yuyang.messi.ui.category;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;

import com.yuyang.messi.MessiApp;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

import org.jetbrains.annotations.NotNull;

public class NetStatusActivity extends AppBaseActivity {

    private final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {

        /**
         * 网络可用的回调
         * */
        @Override
        public void onAvailable(@NotNull Network network) {
            super.onAvailable(network);
            Log.e("lzp", "onAvailable");
        }

        /**
         * 网络丢失的回调
         * */
        @Override
        public void onLost(@NotNull Network network) {
            super.onLost(network);
            Log.e("lzp", "onLost");
        }

        /**
         * 当建立网络连接时，回调连接的属性
         * */
        @Override
        public void onLinkPropertiesChanged(@NotNull Network network, @NotNull LinkProperties linkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties);
            Log.e("lzp", "onLinkPropertiesChanged");
        }

        /**
         *  按照官方的字面意思是，当我们的网络的某个能力发生了变化回调，那么也就是说可能会回调多次
         *
         *  之后在仔细的研究
         * */
        @Override
        public void onCapabilitiesChanged(@NotNull Network network, @NotNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            Log.e("lzp", "onCapabilitiesChanged");
            ConnectivityManager cm = (ConnectivityManager) MessiApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getNetworkInfo(network);
            Log.e("qqwwee", networkInfo == null ? "null" : networkInfo.getState().toString());
        }

        /**
         * 在网络失去连接的时候回调，但是如果是一个生硬的断开，他可能不回调
         * */
        @Override
        public void onLosing(@NotNull Network network, int maxMsToLive) {
            super.onLosing(network, maxMsToLive);
            Log.e("lzp", "onLosing");
        }

        /**
         * 按照官方注释的解释，是指如果在超时时间内都没有找到可用的网络时进行回调
         * */
        @Override
        public void onUnavailable() {
            super.onUnavailable();
            Log.e("lzp", "onUnavailable");
        }

    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_net_status;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // 请注意这里会有一个版本适配bug，所以请在这里添加非空判断
        if (connectivityManager != null) {
            connectivityManager.registerNetworkCallback(new NetworkRequest.Builder().build(), networkCallback);
            connectivityManager.registerNetworkCallback(new NetworkRequest.Builder().build(), networkCallback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }
}

