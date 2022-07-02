package com.yuyang.messi.utils.data;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.yuyang.messi.MessiApp;

import java.io.File;
import java.io.IOException;

/**
 * 内部存储
 * <p>
 * 总是可用的
 * 这里的文件默认是只能被你的app所访问的。
 * 当用户卸载你的app的时候，系统会把internal里面的相关文件都清除干净。
 * Internal是在你想确保不被用户与其他app所访问的最佳存储区域。
 * <p>
 * 外部存储
 * <p>
 * 并不总是可用的，因为用户可以选择把这部分作为USB存储模式，这样就不可以访问了。
 * 是大家都可以访问的，因此保存到这里的文件是失去访问控制权限的。
 * 当用户卸载你的app时，系统仅仅会删除external根目录（getExternalFilesDir()）下的相关文件。
 * External是在你不需要严格的访问权限并且你希望这些文件能够被其他app所共享或者是允许用户通过电脑访问时的最佳存储区域。
 */
public class DataInternalUtil {

    /**
     * @return 路径为/data/data/package_name/files
     */
    public static File getInternalFileDir() {
        return MessiApp.getInstance().getFilesDir();
    }

    /**
     * @return 路径为/data/data/package_name/cache
     */
    public static File getInternalCacheDir() {
        return MessiApp.getInstance().getCacheDir();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static File getInternalDataDir() {
        return MessiApp.getInstance().getDataDir();
    }

    /**
     * 清除本应用所有数据库
     * @return (/data/data/com.xxx.xxx/databases)
     */
    public static File getDatabaseFile() {
        return new File("/data/data/" + MessiApp.getInstance().getPackageName() + "/databases");
    }

    /**
     * * 按名字清除本应用数据库
     */
    public static void cleanDatabaseByName(String dbName) {
        MessiApp.getInstance().deleteDatabase(dbName);
    }

    /**
     * 清除本应用SharedPreference
     * @return (/data/data/com.xxx.xxx/shared_prefs)
     */
    public static File getSharedPreference() {
        return new File("/data/data/" + MessiApp.getInstance().getPackageName() + "/shared_prefs");
    }

    /**
     * 清除应用缓存的用户数据，同时停止所有服务和Alarm定时task
     * String cmd = "pm clear" + packageName;
     * String cnm = "pm clear" + packageName + "HERE";
     * Runtime.getRuntime().exec(cmd);
     * @param packageName
     * @return
     */
    public static Process clearAppUserData(String packageName) {
        Process p = execRuntimeProcess("pm clear" + packageName);
        if (p == null) {
//            ToastUtil.showToast("FAIL");
        } else {
//            ToastUtil.showToast("SUCCESS");
        }
        return p;
    }

    public static Process execRuntimeProcess(String command) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p;
    }


}
