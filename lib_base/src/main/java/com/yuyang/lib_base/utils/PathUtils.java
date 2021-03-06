package com.yuyang.lib_base.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by lzw on 15/4/26.
 */
public class PathUtils {

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * 有 sdcard 的时候，小米是 /storage/sdcard0/Android/data/com.avoscloud.chat/cache/
     * 无 sdcard 的时候，小米是 /data/data/com.avoscloud.chat/cache
     * 依赖于包名。所以不同应用使用该库也没问题，要有点理想。
     *
     * @return
     */
    private static File getAvailableCacheDir(Context context) {
        if (isExternalStorageWritable()) {
            return context.getExternalCacheDir();
        } else {
            // 只有此应用才能访问。拍照的时候有问题，因为拍照的应用写入不了该文件
            return context.getCacheDir();
        }
    }

    /**
     * 拍照保存的地址
     *
     * @return
     */
    public static String getPicturePathByCurrentTime(Context context) {
        String path = new File(getAvailableCacheDir(context), "picture_" + System.currentTimeMillis()).getAbsolutePath();
//    LogUtils.d("picture path ", path);
        return path + ".jpg";
    }
}
