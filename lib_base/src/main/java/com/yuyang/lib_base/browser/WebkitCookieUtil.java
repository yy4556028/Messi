package com.yuyang.lib_base.browser;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import com.yuyang.lib_base.BaseApp;

import java.net.MalformedURLException;
import java.net.URL;

public class WebkitCookieUtil {

    // 设置接收 第三方 Cookie
    public static void setAcceptThirdPartyCookies(WebView webView, boolean accept) {
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, accept);
    }

    // 获取指定url关联的所有Cookie
    // 返回值使用 "Cookie" 请求头格式： "name=value; name2=value2"
    public static String getCookie(String url) {
        return CookieManager.getInstance().getCookie(url);
    }

    public static void setCookie(String url, String value) {
        CookieManager.getInstance().setCookie(url, value);
    }

    public static void clearCookie(String url, String cookieName) {
        CookieManager.getInstance().setCookie(url, cookieName + "=");
    }

    public static void remove(String url) {
        CookieManager cm = CookieManager.getInstance();
        for (String cookie : cm.getCookie(url).split(";")) {
            cm.setCookie(url, cookie.split("=")[0] + "=");
        }
        flush();
    }

    /**
     * @param sessionOnly true:移除所有绘画cookie false:移除所有cookie
     */
    public static void remove(boolean sessionOnly) {
        CookieManager cm = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (sessionOnly) {
                cm.removeSessionCookies(null);
            } else {
                cm.removeAllCookies(null);
            }
        } else {
            if (sessionOnly) {
                cm.removeSessionCookie();
            } else {
                cm.removeAllCookie();
            }
        }
    }

    public static void flush() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager.getInstance().sync();
        }
    }

    public static void initCookie(Context context, String url) {
        final CookieManager cookieManager = CookieManager.getInstance();
//        CookieManager.setAcceptFileSchemeCookies(true);
        boolean noCookie = true;
        boolean needAddCookie = true;
        if (noCookie) {
            CookieSyncManager.createInstance(context);
            cookieManager.removeAllCookie();
            CookieSyncManager.getInstance().sync();
        } else if (needAddCookie) {
            CookieSyncManager.createInstance(context);
//            String token = AccountPreferenceHelper.getSessionId();
//            String did = DeviceIdUtil.getPhoneDeviceId(this);
//            cookieManager.setCookie(getDomainFromUrl(url), "ww_token=" + token);
//            cookieManager.setCookie(".ticstore.com", "ww_token=" + token);
//            cookieManager.setCookie(".ticstore.com", "device_id=" + did);
//            cookieManager.setCookie(".chumenwenwen.com", "ww_token=" + token);
//            cookieManager.setCookie(".chumenwenwen.com", "device_id=" + did);
//            cookieManager.setCookie(".ticwear.com", "ww_token=" + token);
//            cookieManager.setCookie(".ticwear.com", "device_id=" + did);
//            cookieManager.setCookie(".mobvoi.com", "ww_token=" + token);
//            cookieManager.setCookie(".mobvoi.com", "device_id=" + did);
            CookieSyncManager.getInstance().sync();
        }

        String cookie = BaseApp.getInstance().getSharedPreferences("cookie", Context.MODE_PRIVATE).getString("cookies", "");// 从SharedPreferences中获取整个Cookie串
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
                CookieManager.getInstance().setCookie(getDomainFromUrl(url), value);// 设置 Cookie
            }
        }
    }

    public static void saveCookie(String url) {
        CookieManager cookieManager = CookieManager.getInstance();
        String cookieStr = cookieManager.getCookie(getDomainFromUrl(url));
        SharedPreferences preferences = BaseApp.getInstance().getSharedPreferences("cookie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("cookies", cookieStr);
        editor.apply();
    }

    private static String getDomainFromUrl(String url) {
        try {
            String domain = new URL(url).getHost();
            int index = domain.indexOf('.');
            if (index != -1) {
                domain = domain.substring(index);
            }
            return domain;
        } catch (MalformedURLException e) {
        }
        return "";
    }

    private static String getDomain(String url) {
        url = url.replace("http://", "").replace("https://", "");
        if (url.contains("/")) {
            url = url.substring(0, url.indexOf('/'));
        }
        return url;
    }
}
