package com.yuyang.messi.ui.suning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.Nullable;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

public class DouyaWebActivity extends AppBaseActivity {

    private static final String KEY_URL = "key_url";

    private WebView webView;
    private String webUrl;

    public static void launchActivity(Context context, String url) {
        Intent intent = new Intent(context, DouyaWebActivity.class);
        intent.putExtra(KEY_URL, url);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_douya_web;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webUrl = getIntent().getStringExtra(KEY_URL);
        webView = findViewById(R.id.activity_douya_web_webView);
//        setCookies(webUrl);
        loadUrl();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveCookies();
    }

    private void loadUrl() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

        });

        WebSettings webSettings = webView.getSettings();
        // 开启Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        // 启用localStorage 和 essionStorage
        webSettings.setDomStorageEnabled(true);
        // 开启应用程序缓存
        webSettings.setAppCacheEnabled(true);
        String appCacheDir = this.getApplicationContext()
            .getDir("cache", Context.MODE_PRIVATE).getPath();
        webSettings.setAppCachePath(appCacheDir);// /data/data/com.example.zk.android/app_cache
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAppCacheMaxSize(1024 * 1024 * 5);// 设置缓冲大小，我设的是10M

        // 缩放开关
        webView.getSettings().setSupportZoom(true);
        // 设置此属性，仅支持双击缩放，不支持触摸缩放（在android4.0是这样，其他平台没试过）
        // 设置是否可缩放
//        webView.getSettings().setBuiltInZoomControls(true);
        // 无限缩放
        webView.getSettings().setUseWideViewPort(true);

        webView.loadUrl(webUrl);
    }

    private void setCookies(String cookiesPath) {
        String cookie = getActivity().getSharedPreferences("cookie", Context.MODE_PRIVATE).getString("cookies", "");// 从SharedPreferences中获取整个Cookie串
//        cookie = "IMTGC=TGT7908532F777A8A02FF3E6D172D8B933A5ECA6EBF;path=/ids/";
        if (!TextUtils.isEmpty(cookie)) {
            String[] cookieArray = cookie.split(";");// 多个Cookie是使用分号分隔的
            for (int i = 0; i < cookieArray.length; i++) {
                int position = cookieArray[i].indexOf("=");// 在Cookie中键值使用等号分隔
                String cookieName = cookieArray[i].substring(0, position);// 获取键
                String cookieValue = cookieArray[i].substring(position + 1);// 获取值

                String value = cookieName + "=" + cookieValue;// 键值对拼接成 value
                Log.i("cookie", value);
//                cookieManager.setAcceptCookie(true);
//                cookieManager.removeSessionCookie();// 移除旧的[可以省略]
                CookieManager.getInstance().setCookie(getDomain(cookiesPath), value);// 设置 Cookie
            }
        }
    }

    private void saveCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        String cookieStr = cookieManager.getCookie(getDomain(webUrl));
        SharedPreferences preferences = getActivity().getSharedPreferences("cookie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("cookies", cookieStr);
        editor.commit();
    }

    /**
     * 获取URL的域名
     */
    private String getDomain(String url) {
        url = url.replace("http://", "").replace("https://", "");
        if (url.contains("/")) {
            url = url.substring(0, url.indexOf('/'));
        }
        return url;
    }
}
