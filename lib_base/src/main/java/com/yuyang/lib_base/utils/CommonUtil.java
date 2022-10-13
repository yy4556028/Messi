package com.yuyang.lib_base.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Insets;
import android.graphics.Point;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.AbsListView;
import android.widget.TextView;

import androidx.annotation.RequiresPermission;

import com.yuyang.lib_base.BaseApp;
import com.yuyang.lib_base.R;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

// Runtime.getRuntime().availableProcessors()
public class CommonUtil {

    /**
     * 判断当前应用程序处于前台还是后台
     * 需要在Android Manifest.xml文件中添加以下权限
     * <uses-permission android:name="android.permission.GET_TASKS" />
     */
    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * app 是否在后台运行
     *
     * @param ctx
     * @return
     */
    public static boolean isApplicationBroughtToBackgroundDiff(Context ctx) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();

        if (runningAppProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                if (runningAppProcessInfo.processName.startsWith(ctx.getPackageName())) {
                    return runningAppProcessInfo.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && runningAppProcessInfo.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE; //排除无界面的app
                }
            }
        }
        return false;
    }

    /**
     * 判断当前界面是否是桌面
     */
    public static boolean isHome(final Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = mActivityManager.getRunningTasks(1);

        /**
         * 获得属于桌面的应用的应用包名称
         */
        List<String> names = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }

        return names.contains(tasks.get(0).topActivity.getPackageName());
    }

    /**
     * 计算已使用内存的百分比，并返回。
     *
     * @param context 可传入应用程序上下文。
     * @return 已使用内存的百分比，以字符串形式返回。
     */
    public static String getUsedPercentValue(Context context) {
        String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
            long availableSize = getAvailableMemory(context) / 1024;
            int percent = (int) ((totalMemorySize - availableSize) / (float) totalMemorySize * 100);
            return percent + "%";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前可用内存，返回数据以字节为单位。
     *
     * @param context 可传入应用程序上下文。
     * @return 当前可用内存。
     */
    private static long getAvailableMemory(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryInfo(mi);
        return mi.availMem;
    }

    /**
     * 用来判断服务是否运行.
     *
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) BaseApp.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    @RequiresPermission(anyOf = {
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.READ_PHONE_NUMBERS
    })
    public static String getPhoneNumber() {
        TelephonyManager tm = (TelephonyManager) BaseApp.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
        String phoneId = tm.getLine1Number();
        return phoneId;
    }

    /**
     * 0dpi ~ 120dpi	ldpi
     * 120dpi ~ 160dpi	mdpi
     * 160dpi ~ 240dpi	hdpi
     * 240dpi ~ 320dpi	xhdpi
     * 320dpi ~ 480dpi	xxhdpi
     * 480dpi ~ 640dpi	xxxhdpi
     *
     * @return
     */
    public static float getXDpi() {
        return Resources.getSystem().getDisplayMetrics().xdpi;
    }

    public static float getYDpi() {
        return Resources.getSystem().getDisplayMetrics().ydpi;
    }

    /**
     * 获取屏幕长宽比
     *
     * @return
     */
    public static float getScreenRate() {
        Point P = getScreenMetrics();
        float H = P.y;
        float W = P.x;
        return (H / W);
    }

    /**
     * 获取屏幕宽度和高度，单位为px
     *
     * @return
     */
    public static Point getScreenMetrics() {
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        return new Point(w_screen, h_screen);

    }

    /**
     * 获取屏幕宽度 px
     */
    public static int getScreenWidth() {
        return getScreenMetrics().x;
    }

    /**
     * 获取屏幕宽度 px
     */
    public static int getScreenWidth(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().width() - insets.left - insets.right;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }
    }

    public static int getScreenHeight() {
        return getScreenMetrics().y;
    }

    /**
     * 获取屏幕高度 px
     */
    public static int getScreenHeight(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().height() - insets.top - insets.bottom;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.heightPixels;
        }
    }

    /**
     * 中文转换为Unicode码
     *
     * @param str
     * @return
     */
    public static String chineseToUnicode(String str) {
        String result = "";
        for (int i = 0; i < str.length(); i++) {
            int chr1 = (char) str.charAt(i);
            if (chr1 >= 19968 && chr1 <= 171941) {// 汉字范围 \u4e00-\u9fa5 (中文)
                result += "%u" + Integer.toHexString(chr1);
            } else {
                result += str.charAt(i);
            }
        }
        return result;
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }

    /**
     * Url 转码为 UTF-8
     *
     * @param url
     * @return
     */
    public static String enCodeUrl(String url) {
        if (url.length() < 7)
            throw new IndexOutOfBoundsException();

        String urlWithoutHttp = url.substring(7);
        String[] urlParts = urlWithoutHttp.split("/");
        StringBuilder urlBuilder = new StringBuilder();

        for (int i = 0; i < urlParts.length - 1; i++) {
            String tmpStr = "";
            if (0 == i)
                tmpStr = urlParts[i];
            else
                tmpStr = Uri.encode(urlParts[i], "UTF-8");
            urlBuilder.append(tmpStr);
            urlBuilder.append("/");
        }

        urlBuilder.append(Uri.encode(urlParts[urlParts.length - 1], "UTF-8"));

        url = "http://" + urlBuilder.toString();
        return url;
    }

    /**
     * 获取字符串长度（半角算1、全角算2）
     *
     * @param str
     * @return
     */
    public static int getLength(String str) {
        if (str == null || str.equals("")) {
            return 0;
        }
        int len = str.length();
        for (int i = 0; i < str.length(); i++) {
            if (isFullwidthCharacter(str.charAt(i))) {
                len++;
            }
        }
        return len;
    }

    /**
     * 判断字符是否为全角字符
     *
     * @param ch
     * @return
     */
    private static boolean isFullwidthCharacter(final char ch) {
        if (ch >= 32 && ch <= 127) {
            // 基本拉丁字母（即键盘上可见的，空格、数字、字母、符号）
            return false;
        } else if (ch >= 65377 && ch <= 65439) {
            // 日文半角片假名和符号
            return false;
        } else {
            return true;
        }
    }

    // 时间转换
    public static String convertTime(int time) {
        StringBuilder retStr = new StringBuilder();
        int tmpCountDown = time;

        int day = tmpCountDown / 86400;
        if (day > 0) {
            retStr.append(day + "天");
            tmpCountDown = tmpCountDown % 86400;
        }

        int hour = tmpCountDown / 3600;
        if (hour > 0) {
            retStr.append(hour + "小时");
            tmpCountDown = tmpCountDown % 3600;
        }

        int minute = tmpCountDown / 60;
        if (minute > 0) {
            retStr.append(minute + "分钟");
            tmpCountDown = tmpCountDown % 60;
        }

        retStr.append(tmpCountDown + "秒");

        return retStr.toString();
    }

    public static String getTopActivityName(Context var0) {
        ActivityManager activityManager = (ActivityManager) var0.getSystemService(Context.ACTIVITY_SERVICE);
        List list = activityManager.getRunningTasks(1);
        return ((ActivityManager.RunningTaskInfo) list.get(0)).topActivity.getClassName();
    }

    public static Activity getCurrentActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(
                    null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map activities = (Map) activitiesField.get(activityThread);
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    Activity activity = (Activity) activityField.get(activityRecord);
                    return activity;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //判断当前手机是否处于锁屏(睡眠)状态
    public static boolean isSleeping(Context context) {
        KeyguardManager kgMgr = (KeyguardManager) context
                .getSystemService(Context.KEYGUARD_SERVICE);
        boolean isSleeping = kgMgr.inKeyguardRestrictedInputMode();
        return isSleeping;
    }

    //获取当前设备的MAC地址
    public static String getMacAddress(Context context) {
        String macAddress;
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        macAddress = info.getMacAddress();
        if (null == macAddress) {
            return "";
        }
        macAddress = macAddress.replace(":", "");
        return macAddress;
    }

    public static boolean writeToZipFile(byte[] var0, String var1) {
        FileOutputStream var2 = null;
        GZIPOutputStream var3 = null;

        label109:
        {
            try {
                var2 = new FileOutputStream(var1);
                var3 = new GZIPOutputStream(new BufferedOutputStream(var2));
                var3.write(var0);
                break label109;
            } catch (Exception var20) {
                var20.printStackTrace();
            } finally {
                if (var3 != null) {
                    try {
                        var3.close();
                    } catch (IOException var19) {
                        var19.printStackTrace();
                    }
                }

                if (var2 != null) {
                    try {
                        var2.close();
                    } catch (IOException var18) {
                        var18.printStackTrace();
                    }
                }

            }

            return false;
        }

//        if(debugMode) {
//            File var4 = new File(var1);
//            DecimalFormat var5 = new DecimalFormat("#.##");
//            double var6 = (double)var4.length() / (double)var0.length * 100.0D;
//            double var8 = Double.valueOf(var5.format(var6)).doubleValue();
//            Log.d("zip", "data size:" + var0.length + " zip file size:" + var4.length() + "zip file ratio%: " + var8);
//        }

        return true;
    }

    public static String convertByteArrayToString(byte[] var0) {
        StringBuffer var1 = new StringBuffer();
        byte[] var5 = var0;
        int var4 = var0.length;

        for (int var3 = 0; var3 < var4; ++var3) {
            byte var2 = var5[var3];
            var1.append(String.format("0x%02X", new Object[]{Byte.valueOf(var2)}));
        }

        return var1.toString();
    }

    public static boolean isSdcardExist() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static Activity getActivityFromView(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        throw new IllegalStateException("The Context is not an Activity.");
    }

    public static View getEmptyViewForListView(Context context) {
        return getEmptyViewForListView(context, 0, null);
    }

    public static View getEmptyViewForListView(Context context, int emptyViewHeight, String tip) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_empty, null);
        ViewGroup.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
        params.width = getScreenWidth();
        params.height = emptyViewHeight > 0 ? emptyViewHeight : getScreenWidth();
        view.setLayoutParams(params);
        if (!TextUtils.isEmpty(tip)) {
            TextView tipText = view.findViewById(R.id.view_empty_textView);
            tipText.setText(tip);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing
            }
        });
        return view;
    }

    public static <T> List<T> asList(T... array) {
        return new ArrayList<>(Arrays.asList(array));
    }

    /**
     * 判断是否为乱码(未验证过)
     */
    public static boolean isMessyCode(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            //当从 Unicode 编码向某个字符集转换时，如果在该字符集中没有对应的编码，则得到0x3f(及问号字符)
            //当从其他字符集向 Unicode 编码转换时，如果这个二进制数在该字符集中没有标识任何的字符，则得到的结果是0xfffd
            if ((int) c == 0xfffd) {
                return true;
            }
//                if (!Character.isLetterOrDigit(c)) {
//                }
        }
        return false;
    }

    public static <T> T copyParcelable(Parcelable input) {
        Parcel parcel = null;

        try {
            parcel = Parcel.obtain();
            parcel.writeParcelable(input, 0);

            parcel.setDataPosition(0);
            return parcel.readParcelable(input.getClass().getClassLoader());
        } finally {
            parcel.recycle();
        }
    }
}