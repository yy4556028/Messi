package com.yuyang.messi.ui.media;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.util.List;

public class MyCookieManager {
    private CookieManager mCookieManager = null;

    MyCookieManager() {
        mCookieManager = new CookieManager();
        mCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(mCookieManager);
    }

    private List<HttpCookie> getCookies() {
        if (mCookieManager == null)
            return null;
        else
            return mCookieManager.getCookieStore().getCookies();
    }

    public void clearCookies() {
        if (mCookieManager != null)
            mCookieManager.getCookieStore().removeAll();
    }

    public boolean isCookieManagerEmpty() {
        if (mCookieManager == null)
            return true;
        else
            return mCookieManager.getCookieStore().getCookies().isEmpty();
    }


    public String getCookieValue() {
        String cookieValue = new String();

        if (!isCookieManagerEmpty()) {
            for (HttpCookie eachCookie : getCookies())
                cookieValue = cookieValue + String.format("%s=%s; ", eachCookie.getName(), eachCookie.getValue());
        }

        return cookieValue;
    }

}
