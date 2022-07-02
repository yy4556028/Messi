package com.yuyang.lib_base.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.yuyang.lib_base.BaseApp;

public class SharedPreferencesBaseUtil {

    protected static SharedPreferences sharedPreferences;

    protected SharedPreferencesBaseUtil() {
        getSharedPreferences().registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

            }
        });
    }

    // 获取默认包名的 SharedPreferences
    protected static SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences = BaseApp.getInstance().getSharedPreferences("messi_sp", Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    // clear 默认 SharedPreferences
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public static void clearAll() {
        getSharedPreferences().edit().clear().apply();
    }

    public static boolean getBoolean(String key, boolean def) {
        return getSharedPreferences().getBoolean(key, def);
    }

    public static boolean setBoolean(String key, boolean value) {
        return getSharedPreferences().edit().putBoolean(key, value).commit();
    }

    public static String getString(String key, String def) {
        return getSharedPreferences().getString(key, def);
    }

    public static boolean setString(String key, String value) {
        return getSharedPreferences().edit().putString(key, value).commit();
    }

    public static int getInt(String key, int def) {
        return getSharedPreferences().getInt(key, def);
    }

    public static boolean setInt(String key, int value) {
        return getSharedPreferences().edit().putInt(key, value).commit();
    }

}
