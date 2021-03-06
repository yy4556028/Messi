package com.yuyang.messi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;

import com.baidu.mapapi.SDKInitializer;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.liulishuo.filedownloader.services.DownloadMgrInitialParams;
import com.liulishuo.filedownloader.util.FileDownloadLog;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.tencent.mmkv.MMKV;
import com.yuyang.lib_base.BaseApp;
import com.yuyang.lib_base.net.common.SSLParams;
import com.yuyang.lib_base.utils.LogUtil;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.crash.CrashHandler;
import com.yuyang.messi.learn.DexHelper;
import com.yuyang.messi.net.okhttp.OkHttpUtil;
import com.yuyang.messi.net.okhttp.log.LoggerInterceptor;
import com.yuyang.messi.utils.AssetsUtil;
import com.yuyang.messi.utils.SharedPreferencesUtil;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;
import okhttp3.OkHttpClient;

/**
 * https://blankj.com/
 */
public class MessiApp extends BaseApp {

    static {
        //???????????????Header?????????
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {

            layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//????????????????????????
            return new MaterialHeader(context);//.setTimeFormat(new DynamicTimeFormat("????????? %s"));//???????????????Header???????????? ???????????????Header
        });
        //???????????????Footer?????????
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            //???????????????Footer???????????? BallPulseFooter
            return new ClassicsFooter(context).setDrawableSize(20);
        });
    }

    public static final String TAG = MessiApp.class.getSimpleName();

    public static MessiApp getInstance() {
        return (MessiApp) appContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        fixBug();
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        BlockCanary.install(this, new BlockCanaryContext()).start();

        CrashHandler.getInstance().init(this);

        // just for open the log in this demo project.
        FileDownloadLog.NEED_LOG = false;

        /**
         * just for cache Application's Context, and ':filedownloader' progress will NOT be launched
         * by below code, so please do not worry about performance.
         * @see FileDownloader#init(Context)
         */
        FileDownloader.init(getApplicationContext(), new DownloadMgrInitialParams.InitCustomMaker()
                .connectionCreator(new FileDownloadUrlConnection
                        .Creator(new FileDownloadUrlConnection.Configuration()
                        .connectTimeout(15_000) // set connection timeout.
                        .readTimeout(15_000) // set read timeout.
                        .proxy(Proxy.NO_PROXY) // set proxy
                )));

        initOkHttpUtil();
        initBaidu();

        String rootDir = MMKV.initialize(this);
        LogUtil.i("MMKV", "rootDir = " + rootDir);
//        ShareSDK.initSDK(this);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5915edbb");//?????????????????????

        initNightMode();

//        DynamicColors.applyToActivitiesIfAvailable(this);

//        AVOSCloud.useAVCloudUS();// ??????????????????, ????????? initialize ????????????
        // ???????????????????????? this, AppId, AppKey
//        AVOSCloud.initialize(this, ThirdKey.LEANCLOUD_APPID, ThirdKey.LEANCLOUD_APPKEY);
        // ?????? SDK ??????????????? AVOSCloud.initialize() ????????????????????????????????????
//        AVOSCloud.setDebugLogEnabled(CommonConstant.environment == AppEnvironment.Development);

//        'try {
//            hookAMS();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }'
    }

    /**
     * ?????????Android P?????????????????? ???Detected problems with API compatibility(visit g.co/dev/appcompat for more info)
     */
    private void closeAndroidPDialog() {
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initOkHttpUtil() {
        SSLParams sslParams = SSLParams.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .addInterceptor(new LoggerInterceptor("HttpLogger"))
//                .cookieJar(cookieJar1)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .build();
        OkHttpUtil.initClient(okHttpClient);
    }

    private void initBaidu() {
        //?????????SDK????????????????????????context???????????????ApplicationContext
        //?????????????????????setContentView??????????????????
        SDKInitializer.initialize(getApplicationContext());
    }

    public void showDialog(String title, String msg) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(MessiApp.getInstance())) {
                //??????Activity???????????????
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + getPackageName()));
                MessiApp.getInstance().startActivity(intent);
                return;
            }
        }
        /**
         * ?????????android.app.AlertDialog???????????????Dialog
         * ??????style??????android.view.ContextThemeWrapper
         */
//        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(App.getAppContext())
        AlertDialog dialog = new AlertDialog.Builder(
                new android.view.ContextThemeWrapper(MessiApp.getInstance(), R.style.AppTheme))
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        if (Build.VERSION.SDK_INT >= 26) {//8.0?????????
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        dialog.show();
    }

    private void initNightMode() {
        int nightMode = SharedPreferencesUtil.getNightMode();
        if (nightMode != AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.setDefaultNightMode(nightMode);
        }

//        if (getDarkModeStatus(this)) {
//            setTheme(R.style.Theme_App_Dark);
//        } else {
//            setTheme(R.style.Theme_App);
//        }
    }

    private String getMetaData(String metaDataKey) {
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(
                    getPackageName(), PackageManager.GET_META_DATA);

            Bundle metaData = applicationInfo.metaData;
            if (null != metaData && metaData.containsKey(metaDataKey)) {
                return metaData.getString(metaDataKey);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void fixBug() {
        String path = AssetsUtil.copyAssetToCache("fix.dex");
        File fixFile = new File(path);
        List<File> fileList = new ArrayList<>();
        fileList.add(fixFile);

        DexHelper.replaceDex(getClassLoader(), fileList, getCacheDir());

        BaseDexClassLoader baseDexClassLoader;
//        BootClassLoader               //??????framework??????class    ??????Application
        PathClassLoader pathClassLoader;//??????Android??????????????????class(????????????)  ?????? MainActivity AppCompatActivity(????????????)
        DexClassLoader dexClassLoader;  //???????????????
    }

    /**
     * Activity.startActivityForResult ->
     * mInstrumentation.execStartActivity ->
     * ActivityTaskManager.getService().startActivity -> (hook???,??????IActivityTaskManager?????????IActivityTaskManagerSingleton??????)
     * AMS.startActivity
     */
    private void hookAMS() throws Exception {
        boolean newVersion = false;
        String hookClass = newVersion ? "android.app.ActivityTaskManager" : "android.app.ActivityManagerNative";
        String hookGetSingletonMethod = newVersion ? "getService" : "getDefault";
        String hookSingletonField = newVersion ? "IActivityTaskManagerSingleton" : "IActivityManagerSingleton";


        Class<?> IActivityTaskManager = Class.forName(newVersion ? "android.app.IActivityTaskManager" : "android.app.IActivityManager");

        Class<?> mActivityTaskManager2 = Class.forName(hookClass);
        final Object mIActivityTaskManager = mActivityTaskManager2.getDeclaredMethod(hookGetSingletonMethod).invoke(null);//???????????????????????????

        Object proxy = java.lang.reflect.Proxy.newProxyInstance(
                IActivityTaskManager.getClassLoader(),
                new Class[]{IActivityTaskManager},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if ("startActivity".equals(method.getName())) {
                            ToastUtil.showToast("hood success");
                        }
                        return method.invoke(mIActivityTaskManager, args);
                    }
                });

        /**
         * ???????????? mIActivityTaskManagerSingleton
         */
        if (newVersion) {
            Class<?> mActivityManager = Class.forName(hookClass);
            Field mIActivityTaskManagerSingletonField = mActivityManager.getDeclaredField(hookSingletonField);
            mIActivityTaskManagerSingletonField.setAccessible(true);
            Object mIActivityTaskManagerSingleton = mIActivityTaskManagerSingletonField.get(null);//static?????? ??????????????????

            Class<?> mSingleTon = Class.forName("android.util.Singleton");
            Field mInstanceField = mSingleTon.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);//???????????????????????????????????????
            mInstanceField.set(mIActivityTaskManagerSingleton, proxy);
        } else {
            Class<?> mActivityManager = Class.forName("android.app.ActivityManager");
            Field gDefaultField = mActivityManager.getDeclaredField("IActivityManagerSingleton");
            gDefaultField.setAccessible(true);
            Object gDefault = gDefaultField.get(null);//static?????? ??????????????????

            Class<?> mSingleTon = Class.forName("android.util.Singleton");
            Field mInstanceField = mSingleTon.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);//???????????????????????????????????????
            mInstanceField.set(gDefault, proxy);
        }
    }
}