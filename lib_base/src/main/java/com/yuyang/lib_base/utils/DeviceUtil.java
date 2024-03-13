package com.yuyang.lib_base.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.github.gzuliyujiang.oaid.DeviceID;
import com.github.gzuliyujiang.oaid.DeviceIdentifier;
import com.yuyang.lib_base.BaseApp;

import java.util.UUID;

public class DeviceUtil {

    public static String getUniqueId() {
        String uniqueId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            uniqueId = Settings.System.getString(BaseApp.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
//            uniqueId = Build.getSerial();
        } else {
            uniqueId = UUID.randomUUID().toString();
//            uniqueId = DeviceUtil.getDeviceIMEINumber();
        }
        return uniqueId;
    }

    /**
     * 29以上，直接报错
     */
    @SuppressLint("MissingPermission")
    @Deprecated
    public static String getDeviceIMEINumber() {
        TelephonyManager manager = (TelephonyManager) BaseApp.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getDeviceId();
    }

    // 手机型号
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    // 手机制造商
    public static String getManuFacturer() {
        return Build.MANUFACTURER;
    }

    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    public static int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取本地语言
     */
    public static String getLocalLanguage() {
        return Resources.getSystem().getConfiguration().locale.getLanguage();
    }

    public static String getOAID() {
        String oaid = "";
        if (DeviceID.supportedOAID(BaseApp.getInstance())) {
            oaid = DeviceIdentifier.getOAID(BaseApp.getInstance());
        }
        return oaid;
    }
}
