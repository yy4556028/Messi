package com.yuyang.messi.crash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.yuyang.lib_base.config.AppEnvironment;
import com.yuyang.lib_base.config.CommonConstant;
import com.yuyang.lib_base.utils.AppInfoUtil;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.lib_base.utils.FileUtil;
import com.yuyang.lib_base.utils.LogUtil;
import com.yuyang.lib_base.utils.StorageUtil;
import com.yuyang.messi.kotlinui.main.MainActivity;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 处理程序中未捕获的异常，将异常写入日志文件
 */
public class CrashHandler implements UncaughtExceptionHandler {

    public static final String TAG = CrashHandler.class.getSimpleName();

    private static CrashHandler instance = null;

    private Context mContext;

    private UncaughtExceptionHandler mDefaultHandler;

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());

    public static CrashHandler getInstance() {
        if (instance == null) {
            synchronized (CrashHandler.class) {
                if (instance == null) {
                    instance = new CrashHandler();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        if (!handleException(e) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(t, e);
        } else {
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                Log.e("error : ", e.getMessage());
//            }
//            System.exit(0);
        }
    }

    private boolean handleException(final Throwable ex) {

        {
            // 如果是调试状态则不生成异常文件，让系统默认的异常处理器来处理
//            if (Debug.isDebuggerConnected())
//                return false;
        }

        if (ex == null || mContext == null)
            return false;

        Log.e(TAG, "===================handleException========================");
        ex.printStackTrace();
        Log.e(TAG, "==========================================================");

        // 保存日志文件
        saveCrashInfo2File(ex, collectDeviceInfo());
        LogUtil.saveLastOpea();

        if (CommonConstant.environment == AppEnvironment.Development) {
            Activity activity = CommonUtil.getCurrentActivity();
            if (activity != null) {
                showCrashDialog(activity, ex);
                return true;
            }
        }
        restartApp(); //应用重启
        return true;
    }

    private void showCrashDialog(final Activity activity, final Throwable ex) {

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();

                new AlertDialog.Builder(activity)
                        .setTitle("亲，程序即将崩溃")
                        .setCancelable(false)
                        .setMessage(ex.toString())
                        .setNeutralButton("了解", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        })
                        .create()
                        .show();

                Looper.loop();
            }
        }.start();
    }

    private HashMap<String, String> collectDeviceInfo() {
        HashMap<String, String> info = new HashMap<>();
        info.put("Version", String.format("%s(%s)", AppInfoUtil.getAppVersionName(), AppInfoUtil.getAppVersionCode()));
        info.put("Android", String.format("%s(%s)", Build.VERSION.RELEASE, android.os.Build.MODEL));
        info.put("CrashTime", format.format(new Date()));
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                info.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                Log.e(TAG, "an error occurred when collect crash info", e);
            }
        }
        return info;
    }

    private void saveCrashInfo2File(Throwable ex, HashMap<String, String> info) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : info.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);

        String fileName = String.format("crash-%s.log", format.format(new Date()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            FileUtil.writeFileV29(StorageUtil.getPublicPath("/CrashLog/" + fileName), sb.toString());
        } else {
            File crashFile = new File(StorageUtil.getExternalFilesDir("CrashLog"), fileName);
            FileUtil.writeFile(crashFile.getAbsolutePath(), sb.toString());
        }
    }

    private void restartApp() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Log.e(TAG, "error : ", e);
        }
        Intent intent = new Intent(mContext.getApplicationContext(), MainActivity.class);
        @SuppressLint("WrongConstant") PendingIntent restartIntent = PendingIntent.getActivity(
                mContext.getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        //退出程序
        AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
    }

}
