package com.yuyang.lib_base.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.yuyang.lib_base.BaseApp;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/08/02
 *     desc  : utils about app
 * </pre>
 */
public final class AppUtils {

    private AppUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }


    /**
     * Install the app.
     * <p>Target APIs greater than 25 must hold
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
     *
     * @param filePath The path of file.
     */
    public static void installApp(Context activity, final String filePath) {

        installApp(activity, getFileByPath(filePath));
    }

    /**
     * Install the app.
     * <p>Target APIs greater than 25 must hold
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
     *
     * @param file The file.
     */
    public static void installApp(Context activity, final File file) {

        if (!isFileExists(file))
            return;
        activity.startActivity(getInstallAppIntent(activity, file, true));
    }

    /**
     * Install the app.
     * <p>Target APIs greater than 25 must hold
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
     */
    public static void installApp(final ComponentActivity activity,
                                  final String filePath,
                                  final ActivityResultCallback<ActivityResult> callback) {

        installApp(activity, getFileByPath(filePath), callback);
    }

    /**
     * Install the app.
     * <p>Target APIs greater than 25 must hold
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
     */
    public static void installApp(final ComponentActivity activity,
                                  final File file,
                                  final ActivityResultCallback<ActivityResult> callback) {

        if (!isFileExists(file))
            return;
        activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), callback)
                .launch(getInstallAppIntent(activity, file));
    }

    /**
     * Install the app silently.
     * <p>Without root permission must hold
     * {@code <uses-permission android:name="android.permission.INSTALL_PACKAGES" />}</p>
     *
     * @param filePath The path of file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean installAppSilent(final String filePath) {

        return installAppSilent(getFileByPath(filePath), null);
    }

    /**
     * Install the app silently.
     * <p>Without root permission must hold
     * {@code <uses-permission android:name="android.permission.INSTALL_PACKAGES" />}</p>
     *
     * @param file The file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean installAppSilent(final File file) {

        return installAppSilent(file, null);
    }


    /**
     * Install the app silently.
     * <p>Without root permission must hold
     * {@code <uses-permission android:name="android.permission.INSTALL_PACKAGES" />}</p>
     *
     * @param filePath The path of file.
     * @param params   The params of installation(e.g.,<code>-r</code>, <code>-s</code>).
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean installAppSilent(final String filePath, final String params) {

        return installAppSilent(getFileByPath(filePath), params);
    }

    /**
     * Install the app silently.
     * <p>Without root permission must hold
     * {@code <uses-permission android:name="android.permission.INSTALL_PACKAGES" />}</p>
     *
     * @param file   The file.
     * @param params The params of installation(e.g.,<code>-r</code>, <code>-s</code>).
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean installAppSilent(final File file, final String params) {

        return installAppSilent(file, params, isDeviceRooted());
    }

    /**
     * Install the app silently.
     * <p>Without root permission must hold
     * {@code <uses-permission android:name="android.permission.INSTALL_PACKAGES" />}</p>
     *
     * @param file     The file.
     * @param params   The params of installation(e.g.,<code>-r</code>, <code>-s</code>).
     * @param isRooted True to use root, false otherwise.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean installAppSilent(final File file,
                                           final String params,
                                           final boolean isRooted) {

        if (!isFileExists(file))
            return false;
        String filePath = '"' + file.getAbsolutePath() + '"';
        String command = "LD_LIBRARY_PATH=/vendor/lib*:/system/lib* pm install " +
                (params == null ? "" : params + " ")
                + filePath;
        ShellUtils.CommandResult commandResult = ShellUtils.execCmd(command, isRooted);
        if (commandResult.successMsg != null
                && commandResult.successMsg.toLowerCase().contains("success")) {
            return true;
        } else {
            Log.e("AppUtils", "installAppSilent successMsg: " + commandResult.successMsg +
                    ", errorMsg: " + commandResult.errorMsg);
            return false;
        }
    }

    /**
     * Uninstall the app.
     *
     * @param packageName The name of the package.
     */
    public static void uninstallApp(final Activity activity, final String packageName) {

        if (isSpace(packageName))
            return;
        activity.startActivity(getUninstallAppIntent(packageName, true));
    }

    /**
     * Uninstall the app.
     */
    public static void uninstallApp(final ComponentActivity activity,
                                    final String packageName,
                                    final ActivityResultCallback<ActivityResult> callback) {

        if (isSpace(packageName))
            return;
        activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), callback)
                .launch(getUninstallAppIntent(packageName));
    }

    /**
     * Uninstall the app silently.
     * <p>Without root permission must hold
     * {@code <uses-permission android:name="android.permission.DELETE_PACKAGES" />}</p>
     *
     * @param packageName The name of the package.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean uninstallAppSilent(final String packageName) {

        return uninstallAppSilent(packageName, false);
    }

    /**
     * Uninstall the app silently.
     * <p>Without root permission must hold
     * {@code <uses-permission android:name="android.permission.DELETE_PACKAGES" />}</p>
     *
     * @param packageName The name of the package.
     * @param isKeepData  Is keep the data.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean uninstallAppSilent(final String packageName, final boolean isKeepData) {

        return uninstallAppSilent(packageName, isKeepData, isDeviceRooted());
    }

    /**
     * Uninstall the app silently.
     * <p>Without root permission must hold
     * {@code <uses-permission android:name="android.permission.DELETE_PACKAGES" />}</p>
     *
     * @param packageName The name of the package.
     * @param isKeepData  Is keep the data.
     * @param isRooted    True to use root, false otherwise.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean uninstallAppSilent(final String packageName,
                                             final boolean isKeepData,
                                             final boolean isRooted) {

        if (isSpace(packageName))
            return false;
        String command = "LD_LIBRARY_PATH=/vendor/lib*:/system/lib* pm uninstall "
                + (isKeepData ? "-k " : "")
                + packageName;
        ShellUtils.CommandResult commandResult = ShellUtils.execCmd(command, isRooted);
        if (commandResult.successMsg != null
                && commandResult.successMsg.toLowerCase().contains("success")) {
            return true;
        } else {
            Log.e("AppUtils", "uninstallAppSilent successMsg: " + commandResult.successMsg +
                    ", errorMsg: " + commandResult.errorMsg);
            return false;
        }
    }

    /**
     * Return whether the app is installed.
     *
     * @param packageName The name of the package.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAppInstalled(Context context, @NonNull final String packageName) {

        return !isSpace(packageName)
                && context.getPackageManager().getLaunchIntentForPackage(packageName) != null;
    }

    /**
     * Return whether the app is installed.
     *
     * @param action   The Intent action, such as ACTION_VIEW.
     * @param category The desired category.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAppInstalled(Context context, @NonNull final String action,
                                         @NonNull final String category) {

        Intent intent = new Intent(action);
        intent.addCategory(category);
        PackageManager pm = context.getPackageManager();
        ResolveInfo info = pm.resolveActivity(intent, 0);
        return info != null;
    }

    /**
     * Return whether the application with root permission.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAppRoot() {

        ShellUtils.CommandResult result = ShellUtils.execCmd("echo root", true);
        if (result.result == 0)
            return true;
        if (result.errorMsg != null) {
            Log.d("AppUtils", "isAppRoot() called" + result.errorMsg);
        }
        return false;
    }


    /**
     * Launch the application.
     *
     * @param packageName The name of the package.
     */
    public static boolean launchApp(Context context, final String packageName) {

        if (isSpace(packageName))
            return false;
        context.startActivity(getLaunchAppIntent(packageName, true));
        return true;
    }

    /**
     * Launch the application.
     *
     * @param activity    The activity.
     * @param packageName The name of the package.
     * @param requestCode If &gt;= 0, this code will be returned in
     *                    onActivityResult() when the activity exits.
     */
    public static void launchApp(final ComponentActivity activity,
                                 final String packageName,
                                 final ActivityResultCallback<ActivityResult> callback) {

        if (isSpace(packageName))
            return;
        activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), callback)
                .launch(getLaunchAppIntent(packageName));
    }


    /**
     * Launch the application's details settings.
     */
    public static void launchAppDetailsSettings(Context context) {

        launchAppDetailsSettings(context, context.getPackageName());
    }

    /**
     * Launch the application's details settings.
     *
     * @param packageName The name of the package.
     */
    public static void launchAppDetailsSettings(Context context, final String packageName) {

        if (isSpace(packageName))
            return;
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + packageName));
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    /**
     * 方法描述：判断某一应用是否正在运行
     * Created by 18044075 on 2018/04/21.
     *
     * @param context     上下文
     * @param packageName 应用的包名
     * @return true 表示正在运行，false 表示没有运行
     */
    public static boolean isAppRunning(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        if (list.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.baseActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getRunningApps(Context var0) {
        ArrayList var1 = new ArrayList();
        ActivityManager var2 = (ActivityManager) var0.getSystemService(Context.ACTIVITY_SERVICE);
        List var3 = var2.getRunningAppProcesses();
        Iterator var5 = var3.iterator();

        while (var5.hasNext()) {
            ActivityManager.RunningAppProcessInfo var4 = (ActivityManager.RunningAppProcessInfo) var5.next();
            String var6 = var4.processName;
            if (var6.contains(":")) {
                var6 = var6.substring(0, var6.indexOf(":"));
            }

            if (!var1.contains(var6)) {
                var1.add(var6);
            }
        }

        return var1;
    }

    public static boolean isAppRunningForeground(Context context) {
        ActivityManager var1 = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = var1.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            ComponentName baseActivity = tasks.get(0).baseActivity;
            if (context.getPackageName().equals(baseActivity.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAppRunningForeground() {
        ActivityManager activityManager = (ActivityManager) BaseApp.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = BaseApp.getInstance().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * Exit the application.
     */
    public static void exitApp() {
        System.exit(0);
    }

    ///////////////////////////////////////////////////////////////////////////
    // other utils methods
    ///////////////////////////////////////////////////////////////////////////

    private static boolean isFileExists(final File file) {

        return file != null && file.exists();
    }

    private static File getFileByPath(final String filePath) {

        return isSpace(filePath) ? null : new File(filePath);
    }

    private static boolean isSpace(final String s) {

        if (s == null)
            return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isDeviceRooted() {

        String su = "su";
        String[] locations = {"/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
                "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/"};
        for (String location : locations) {
            if (new File(location + su).exists()) {
                return true;
            }
        }
        return false;
    }

    private static Intent getInstallAppIntent(Activity activity, final File file) {

        return getInstallAppIntent(activity, file, false);
    }

    private static Intent getInstallAppIntent(Context activity, final File file, final boolean isNewTask) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            data = Uri.fromFile(file);
        } else {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String authority = activity.getPackageName() + ".fileProvider";
            data = FileProvider.getUriForFile(activity, authority, file);
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        return isNewTask ? intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) : intent;
    }

    private static Intent getUninstallAppIntent(final String packageName) {

        return getUninstallAppIntent(packageName, false);
    }

    private static Intent getUninstallAppIntent(final String packageName, final boolean isNewTask) {

        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        return isNewTask ? intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) : intent;
    }

    public static Intent getLaunchAppIntent(final String packageName) {

        return getLaunchAppIntent(packageName, false);
    }

    public static Intent getLaunchAppIntent(final String packageName, final boolean isNewTask) {

        Intent intent = BaseApp.getInstance().getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null)
            return null;
        return isNewTask ? intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) : intent;
    }

    /**
     * 会黑屏一段时间
     */
    public static void restartApp() {
        final Intent intent = BaseApp.getInstance().getPackageManager().getLaunchIntentForPackage(BaseApp.getInstance().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        BaseApp.getInstance().startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static void restartApp(Class restartActivity) {
        Intent intent = new Intent(BaseApp.getInstance(), restartActivity);
        PendingIntent restartIntent = PendingIntent.getActivity(
                BaseApp.getInstance(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        //退出程序
        AlarmManager mgr = (AlarmManager) BaseApp.getInstance().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
    }

    public static void hideAppWindow(boolean isHide) {
        try {
            ActivityManager activityManager = (ActivityManager) BaseApp.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.getAppTasks().get(0).setExcludeFromRecents(true);
        } catch (Exception ignore) {
        }
    }
}
