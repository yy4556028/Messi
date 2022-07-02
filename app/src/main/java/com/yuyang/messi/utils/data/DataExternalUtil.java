package com.yuyang.messi.utils.data;

import android.os.Environment;

import com.yuyang.messi.MessiApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
public class DataExternalUtil {

    /************************ 私有存储 *******************************/

    /**
     * @param type The type of files directory to return. May be {@code null}
     *             for the root of the files directory or one of the following
     *             constants for a subdirectory:
     *             {@link android.os.Environment#DIRECTORY_MUSIC},
     *             {@link android.os.Environment#DIRECTORY_PODCASTS},
     *             {@link android.os.Environment#DIRECTORY_RINGTONES},
     *             {@link android.os.Environment#DIRECTORY_ALARMS},
     *             {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},
     *             {@link android.os.Environment#DIRECTORY_PICTURES}, or
     *             {@link android.os.Environment#DIRECTORY_MOVIES}.
     * @return 路径为/data/data/package_name/files
     */
    public static File getExternalFilesDir(String type) {
        return MessiApp.getInstance().getExternalFilesDir(type);
    }

    /**
     * 如果我们想缓存图片等比较耗空间的文件，推荐放在getExternalCacheDir()所在的文件下面，
     * 这个文件和getCacheDir()很像，都可以放缓存文件，在APP被卸载的时候，都会被系统删除，
     * 而且缓存的内容对其他APP是相对私有的
     *
     * @return 路径为/data/data/package_name/files
     */
    public static File getExternalCacheDir() {
        return MessiApp.getInstance().getExternalCacheDir();
    }

    /************************ 私有存储 *******************************/

    /************************ 公有存储 *******************************/

    /**
     * 外部存储的根目录
     *
     * @return
     */
    public static File getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * @param type The type of storage directory to return. Should be one of
     *             {@link Environment#DIRECTORY_MUSIC}, {@link Environment#DIRECTORY_PODCASTS},
     *             {@link Environment#DIRECTORY_RINGTONES}, {@link Environment#DIRECTORY_ALARMS},
     *             {@link Environment#DIRECTORY_NOTIFICATIONS}, {@link Environment#DIRECTORY_PICTURES},
     *             {@link Environment#DIRECTORY_MOVIES}, {@link Environment#DIRECTORY_DOWNLOADS},
     *             {@link Environment#DIRECTORY_DCIM}, or {@link Environment#DIRECTORY_DOCUMENTS}. May not be null.
     *             外部存储的公共目录
     * @return
     */
    public static File getExternalStoragePublicDirectory(String type) {
        return Environment.getExternalStoragePublicDirectory(type);
    }
    /************************ 公有存储 *******************************/

    /***************** 获取外置 sd 卡路径 *******************************/
    // 返回值不带File seperater "/",如果没有外置第二个sd卡,返回null
    public static String getSecondExternalPath() {
        List<String> paths = getAllExternalSdcardPath();

        if (paths.size() == 2) {

            for (String path : paths) {
                if (path != null && !path.equals(Environment.getExternalStorageDirectory().getPath())) {
                    return path;
                }
            }
            return null;
        } else {
            return null;
        }
    }

    public static List<String> getAllExternalSdcardPath() {
        List<String> SdList = new ArrayList<String>();

        String firstPath = Environment.getExternalStorageDirectory().getPath();

        // 得到路径
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                // 将常见的linux分区过滤掉
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                if (line.contains("media"))
                    continue;
                if (line.contains("system") || line.contains("cache")
                        || line.contains("sys") || line.contains("data")
                        || line.contains("tmpfs") || line.contains("shell")
                        || line.contains("root") || line.contains("acct")
                        || line.contains("proc") || line.contains("misc")
                        || line.contains("obb")) {
                    continue;
                }

                if (line.contains("fat") || line.contains("fuse") || (line
                        .contains("ntfs"))) {

                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        String path = columns[1];
                        if (path != null && !SdList.contains(path) && path.contains("sd"))
                            SdList.add(columns[1]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!SdList.contains(firstPath)) {
            SdList.add(firstPath);
        }
        return SdList;
    }

    public static boolean isFirstSdcardMounted() {
        return (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));
    }

    public static boolean isSecondSDcardMounted() {
        String sd2 = getSecondExternalPath();
        if (sd2 == null) {
            return false;
        }
        return checkFsWritable(sd2 + File.separator);
    }

    // 测试外置sd卡是否卸载，不能直接判断外置sd卡是否为null，因为当外置sd卡拔出时，仍然能得到外置sd卡路径。我这种方法是按照android谷歌测试DICM的方法，
    // 创建一个文件，然后立即删除，看是否卸载外置sd卡
    // 注意这里有一个小bug，即使外置sd卡没有卸载，但是存储空间不够大，或者文件数已至最大数，此时，也不能创建新文件。此时，统一提示用户清理sd卡吧
    private static boolean checkFsWritable(String dir) {

        if (dir == null)
            return false;

        File directory = new File(dir);

        if (!directory.isDirectory()) {
            if (!directory.mkdirs()) {
                return false;
            }
        }

        File f = new File(directory, ".keysharetestgzc");
        try {
            if (f.exists()) {
                f.delete();
            }
            if (!f.createNewFile()) {
                return false;
            }
            f.delete();
            return true;

        } catch (Exception e) {
        }
        return false;
    }
}
