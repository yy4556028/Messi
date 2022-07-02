package com.yuyang.messi.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;
import java.io.File;

public class IntentUtil {

    private static final String EXTRA_PREFS_SHOW_BUTTON_BAR = "extra_prefs_show_button_bar";//是否显示button bar,传递值为true的话是显示
    private static final String EXTRA_PREFS_SET_NEXT_TEXT = "extra_prefs_set_next_text";//自定义按钮的名字，不传递的话，默认为下一步
    private static final String EXTRA_PREFS_SET_BACK_TEXT = "extra_prefs_set_back_text";//自定义按钮的名字，不传递的话，默认为上一步
    private static final String EXTRA_ENABLE_NEXT_ON_CONNECT = "wifi_enable_next_on_connect";//是否打开网络连接检测功能（如果连上wifi，则下一步按钮可被点击）

    /**
     * 选择网络
     * @param context
     */
    public static void pickNetwork(Context context) {
        Intent intent = new Intent();
//        intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
        intent.putExtra(EXTRA_PREFS_SHOW_BUTTON_BAR, true);
        //intent.putExtra(EXTRA_PREFS_SET_NEXT_TEXT, "完成");
        //intent.putExtra(EXTRA_PREFS_SET_BACK_TEXT, "返回");
        intent.putExtra(EXTRA_ENABLE_NEXT_ON_CONNECT, true);
        context.startActivity(intent);
    }

    /**
     * 安装Apk
     * @param context
     * @param file
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri data;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            data = Uri.fromFile(file);
        } else {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String authority = context.getPackageName() + ".fileProvider";
            data = FileProvider.getUriForFile(context, authority, file);
        }

        intent.setDataAndType(data, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 卸载Apk
     */
    public static void uninstallApk(Activity activity, String packageName) {
        Uri packageURI = Uri.parse("package:" + packageName);
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(uninstallIntent);
    }
}