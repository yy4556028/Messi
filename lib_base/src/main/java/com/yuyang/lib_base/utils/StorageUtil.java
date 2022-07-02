package com.yuyang.lib_base.utils;

import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.yuyang.lib_base.BaseApp;
import com.yuyang.lib_base.R;

import java.io.File;

/**
 * 迁移：https://www.jianshu.com/p/af9903069ebe
 * Android Q 要来了，给你一份很"全面"的适配指南：https://blog.csdn.net/
 * https://blog.csdn.net/c6E5UlI1N/article/details/120540157plokmju88/article/details/103354372
 * <p>
 * Android10共享目录：
 * MediaStore.Images    -> Pictures, DCIM
 * MediaStore.Audio     -> Music, Alarms, Notifications, Podcasts, Ringtones
 * MediaStore.Video     -> Movies, DCIM
 * MediaStore.Download  -> Download, Documents
 * <p>
 * 调用ACTION_MANAGE_STORAGE intent 操作检查可用空间。
 * 调用ACTION_CLEAR_APP_CACHE intent 操作清除所有缓存。
 *
 * Android11 分区存储适配
 *
 * 沙盒目录，无需权限，File直接操作
 * 外部共有目录:  Android Q以下系统依旧使用旧的方式，直接使用文件方式保存
 *              Android Q以上使用MediaStore方式存储
 *
 *              读取媒体文件时 Android Q以下使用DATA字段，Android Q以上使用RELATIVE_PATH字段
 *
 *  删除公共目录文件:Android Q以下版本，删除文件需要申请WRITE_EXTERNAL_STORAGE权限，通过MediaStore的DATA字段获得媒体文件的绝对路径，然后使用File相关API删除
 *              Android Q及以上版本，DATA字段被弃用，应用也无法通过路径访问公共目录，此时需要用getContentProvider.delete()方法来删除，应用删除自己创建的媒体文件不需要READ_EXTERNAL_STORAGE权限，也不需要用户授权就可以直接删除
 *              如果应用卸载后又重新安装，删除卸载之前保存的文件就无法直接删除
 */


public class StorageUtil {

    public static final String DOWNLOAD = Environment.DIRECTORY_DOWNLOADS;// 下载路径
    public static final String VIDEO = Environment.DIRECTORY_MOVIES;
    public static final String AUDIO = Environment.DIRECTORY_MUSIC;
    public static final String PICTURE = Environment.DIRECTORY_PICTURES;

    private static final String PUBLIC_ROOT_DIR;

    static {
        // 如果AndroidQ 且 非兼容模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Environment.isExternalStorageLegacy()) {
            //相对路径，公共目录下的 以包名命名的根目录
            PUBLIC_ROOT_DIR = File.separator + BaseApp.getInstance().getPackageName();
        } else {
            PUBLIC_ROOT_DIR = Environment.getExternalStorageDirectory() + File.separator + BaseApp.getInstance().getString(R.string.app_name);
        }
    }

    /**
     * 应用内部缓存目录,可直接用File操作
     * @return
     */
    public static File getPrivateCache() {
        return BaseApp.getInstance().getExternalCacheDir();
    }

    /**
     * 应用内部沙盒目录,可直接用File操作
     */
    public static File getExternalFilesDir(@Nullable String type) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return BaseApp.getInstance().getExternalFilesDir(type);
        } else {
            return null;
        }
    }

    /**
     * 应用内部沙盒目录,可直接用File操作
     */
    public static File getExternalFile(@Nullable String childPath) {
        if (childPath == null) childPath = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            int lastSeparatorIndex = childPath.lastIndexOf(File.separator);
            String relativePath = lastSeparatorIndex == -1 ? null : childPath.substring(0, lastSeparatorIndex);
            String fileName = childPath.substring(lastSeparatorIndex + 1);

            return new File(BaseApp.getInstance().getExternalFilesDir(relativePath), fileName);
        } else {
            return null;
        }
    }

    public static File getPublicFile() {
        return new File(PUBLIC_ROOT_DIR);
    }

    public static String getPublicPath(String childPath) {
        if (TextUtils.isEmpty(childPath)) {
            return PUBLIC_ROOT_DIR;
        }
        if (!File.separator.equals(childPath.substring(0, 1))) {
            childPath = File.separator + childPath;
        }
        return PUBLIC_ROOT_DIR + childPath;
    }
}
