package com.yuyang.lib_base.utils;

import android.os.Build;
import android.util.Log;

import com.yuyang.lib_base.BaseApp;
import com.yuyang.lib_base.R;
import com.yuyang.lib_base.config.Config;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;

public class LogUtil {

    public static String DIR_PATH = BaseApp.getInstance().getExternalFilesDir(null) + "/" + BaseApp.getInstance().getString(R.string.app_name) + "/LastInfo";

    private final static int INFO_LIMIT = 10;
    private final static LinkedBlockingQueue<String> lastInfoQueue = new LinkedBlockingQueue<>(INFO_LIMIT);

    public static void saveLastOpea() {
        String time = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault()).format(new Date());
        String fileName = "last-" + time + "-" + System.currentTimeMillis() + ".txt";

        StringBuilder sb = new StringBuilder();
        for (String str : lastInfoQueue) {
            if (sb.length() != 0) {
                sb.append("\r\n\r\n");
            }
            sb.append(str);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            FileUtil.writeFileV29(StorageUtil.getPublicPath("/LastInfo/" + fileName), sb.toString());
        } else {
            File crashFile = new File(StorageUtil.getExternalFilesDir("LastInfo"), fileName);
            FileUtil.writeFile(crashFile.getAbsolutePath(), sb.toString());
        }
    }

    private static String getDebugInfo() {  //Log.e(tag, getDebugInfo() + s); 之前的Logger中方法，未使用过
        Throwable stack = new Throwable().fillInStackTrace();
        StackTraceElement[] trace = stack.getStackTrace();
        int n = 2;
        return trace[n].getClassName() + " " + trace[n].getMethodName() + "()" + ":" + trace[n].getLineNumber() + " ";
    }

    public static void v(String tag, String s) {
        if (!Config.PRINT_LOG) return;
        Log.v(tag, s);
    }

    public static void i(String tag, String s) {
        if (!Config.PRINT_LOG) return;
        Log.i(tag, s);
    }

    public static void w(String tag, String s) {
        if (!Config.PRINT_LOG) return;
        Log.w(tag, s);
    }

    public static void e(String tag, String s) {
        while (lastInfoQueue.size() >= INFO_LIMIT) {
            lastInfoQueue.poll();
        }
        lastInfoQueue.offer(tag + ">>>>>" + s);
        if (!Config.PRINT_LOG) return;
        Log.e(tag, s);
    }

    public static void d(String tag, String s) {
        if (!Config.PRINT_LOG) return;
        Log.d(tag, s);
    }
}
