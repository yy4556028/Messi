package com.yuyang.messi.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatDelegate;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yuyang.aidl_audioplayer.AudioBean;
import com.yuyang.lib_base.gsonSerializer.ClassSerializer;
import com.yuyang.lib_base.utils.AppInfoUtil;
import com.yuyang.lib_base.utils.SharedPreferencesBaseUtil;
import com.yuyang.messi.MessiApp;
import com.yuyang.messi.room.entity.ModuleEntity;

import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesUtil extends SharedPreferencesBaseUtil {

    private static final String NAME_CODE_TIME = "name_code_time";

    private static final String KEY_VERSION = "key_version";
    private static final String KEY_NIGHT_MODE = "key_night_mode";
    private static final String KEY_CUSTOM_ORDER = "key_custom_order";

    private static final String KEY_LAST_AUDIO_LIST = "key_last_audio_list";
    private static final String KEY_LAST_AUDIO_BEAN = "key_last_audio_bean";
    private static final String KEY_LAST_AUDIO_DURATION = "key_last_audio_duration";

    private SharedPreferencesUtil() {
        super();
    }

    // clear 默认 SharedPreferences
    public static void clearAll() {
        boolean isFirstStart = getFirstStart();
        getSharedPreferences().edit().clear().apply();
        if (!isFirstStart) {
            setFirstStart();
        }
    }

//    private static String getUserKey() {
//        return App.getAppContext().getu().getOrgNo() + "-" + App.ctx.getLoginUser().getUserId();
//    }

    /*********************************************************************************************/

    public static boolean getFirstStart() {
        return getSharedPreferences().getInt(KEY_VERSION, -1) != AppInfoUtil.getAppVersionCode();
    }

    public static void setFirstStart() {
        getSharedPreferences().edit().putInt(KEY_VERSION, AppInfoUtil.getAppVersionCode()).apply();
    }

    /**
     * 获取验证码倒计时
     */
    public static long getCodeTimeInMills(String key) {
        SharedPreferences sharedPreferences = MessiApp.getInstance().getSharedPreferences(NAME_CODE_TIME, Activity.MODE_PRIVATE);
        return sharedPreferences.getLong(key, 0);
    }

    /**
     * 设置验证码倒计时
     */
    public static void setCodeTimeInMills(String key, long timeInMills) {
        SharedPreferences sharedPreferences = MessiApp.getInstance().getSharedPreferences(NAME_CODE_TIME, Activity.MODE_PRIVATE);
        sharedPreferences.edit()
                .putLong(key, timeInMills)
                .apply();
    }

    public static int getNightMode() {
        return getSharedPreferences().getInt(KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_UNSPECIFIED);
    }

    public static void setNightMode(int nightMode) {
        getSharedPreferences().edit().putInt(KEY_NIGHT_MODE, nightMode).apply();
    }

    public static List<AudioBean> getLastAudioList() {
        String userStr = getSharedPreferences().getString(KEY_LAST_AUDIO_LIST, null);
        if (TextUtils.isEmpty(userStr)) {
            return null;
        } else {
            return JSON.parseArray(userStr, AudioBean.class);
        }
    }

    public static void setLastAudioList(List<AudioBean> audioBeanList) {
        String str = new Gson().toJson(audioBeanList);
        getSharedPreferences().edit().putString(KEY_LAST_AUDIO_LIST, str).apply();
    }

    public static AudioBean getLastAudioBean() {
        String userStr = getSharedPreferences().getString(KEY_LAST_AUDIO_BEAN, null);
        if (TextUtils.isEmpty(userStr)) {
            return null;
        } else {
            return JSON.parseObject(userStr, AudioBean.class);
        }
    }

    public static void setLastAudioBean(AudioBean audioBean) {
        String str = new Gson().toJson(audioBean);
        getSharedPreferences().edit().putString(KEY_LAST_AUDIO_BEAN, str).apply();
    }

    public static int getLastAudioDuration() {
        return getSharedPreferences().getInt(KEY_LAST_AUDIO_DURATION, -1);
    }

    public static void setLastAudioDuration(int duration) {
        getSharedPreferences().edit().putInt(KEY_LAST_AUDIO_DURATION, duration).apply();
    }

    public static List<ModuleEntity> getModuleCustomOrder() {
        String data = getSharedPreferences().getString(KEY_CUSTOM_ORDER, null);
        if (TextUtils.isEmpty(data)) {
            return new ArrayList<>();
        } else {
            return JSON.parseArray(data, ModuleEntity.class);
        }
    }

    public static void setModuleCustomOrder(List<ModuleEntity> beanList) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassSerializer()).create();
        editor.putString(KEY_CUSTOM_ORDER, gson.toJson(beanList));
        editor.apply();
    }
}
