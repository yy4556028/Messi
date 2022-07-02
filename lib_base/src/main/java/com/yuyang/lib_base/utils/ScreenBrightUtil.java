package com.yuyang.lib_base.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

public class ScreenBrightUtil {

    /**
     * 设置当前activity的屏幕亮度
     *
     * @param activity
     * @param paramFloat 亮度值范围为0-0.1f，如果为-1.0，则亮度与全局同步
     */
    public static void setActivityBrightness(Activity activity, float paramFloat) {
        Window localWindow = activity.getWindow();
        WindowManager.LayoutParams params = localWindow.getAttributes();
        params.screenBrightness = paramFloat;
        localWindow.setAttributes(params);
    }

    /**
     * 获取当前activity的屏幕亮度 
     *
     * @param activity
     * @return 亮度值范围为0-0.1f，如果为-1.0，则亮度与全局同步
     */
    public static float getActivityBrightness(Activity activity) {
        Window localWindow = activity.getWindow();
        WindowManager.LayoutParams params = localWindow.getAttributes();
        return params.screenBrightness;
    }

    /**
     * 停止自动亮度调节
     */
    public static boolean stopAutoBrightness(Activity activity) {
        try {
            Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /*
     * 判断是否开启了自动亮度调节
     */
    public static boolean isAutoBrightness(ContentResolver aContentResolver) {
        boolean automicBrightness = false;
        try {
            automicBrightness = Settings.System.getInt(aContentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException ignored) {
        }
        return automicBrightness;
    }

    // 设置系统锁屏时间,毫秒
    public static void setLockScreenTime(Context activity, int myLockTime) {
        Settings.System.putLong(activity.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, myLockTime);
    }

    // 设置系统锁屏时间,毫秒
    public static int getLockScreenTime(Context activity) {
        return Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, -1);
    }

    /*
     * 获取屏幕的亮度
     */
    public static int getScreenBrightness(Activity activity) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try {
            nowBrightnessValue = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException ignored) {
        }
        return nowBrightnessValue;
    }

    /*
     * 设置亮度
     */
    public static boolean setScreenBrightness(Activity activity, int brightness) {
        try {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.screenBrightness = (float) brightness * (1f / 255f);
            activity.getWindow().setAttributes(lp);

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /*
     * 保存亮度设置状态
     */
    public static boolean saveScreenBrightness(ContentResolver resolver, int brightness) {
        try {
            Uri uri = android.provider.Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
            android.provider.Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
            resolver.notifyChange(uri, null);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
