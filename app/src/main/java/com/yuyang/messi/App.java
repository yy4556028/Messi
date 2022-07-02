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
import android.util.Log;
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
import com.yuyang.lib_base.utils.LogUtil;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.crash.CrashHandler;
import com.yuyang.messi.learn.DexHelper;
import com.yuyang.messi.net.okhttp.OkHttpUtil;
import com.yuyang.messi.net.okhttp.https.HttpsUtils;
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
public class App extends BaseApp {

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {

            layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
            return new MaterialHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            //指定为经典Footer，默认是 BallPulseFooter
            return new ClassicsFooter(context).setDrawableSize(20);
        });
    }

    public static final String TAG = App.class.getSimpleName();

    public static App getInstance() {
        return (App) appContext;
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
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5915edbb");//讯飞语音初始化

        initNightMode();

//        AVOSCloud.useAVCloudUS();// 启用北美节点, 需要在 initialize 之前调用
        // 初始化参数依次为 this, AppId, AppKey
//        AVOSCloud.initialize(this, ThirdKey.LEANCLOUD_APPID, ThirdKey.LEANCLOUD_APPKEY);
        // 放在 SDK 初始化语句 AVOSCloud.initialize() 后面，只需要调用一次即可
//        AVOSCloud.setDebugLogEnabled(CommonConstant.environment == AppEnvironment.Development);

//        'try {
//            hookAMS();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }'
    }

    /**
     * 去掉在Android P上的提醒弹窗 （Detected problems with API compatibility(visit g.co/dev/appcompat for more info)
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
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .addInterceptor(new LoggerInterceptor("TAG"))
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
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
    }

    public void showDialog(String title, String msg) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(App.getInstance())) {
                //启动Activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + getPackageName()));
                App.getInstance().startActivity(intent);
                return;
            }
        }
        /**
         * 一定要android.app.AlertDialog才能显示出Dialog
         * 搭配style需要android.view.ContextThemeWrapper
         */
//        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(App.getAppContext())
        AlertDialog dialog = new AlertDialog.Builder(
                new android.view.ContextThemeWrapper(App.getInstance(), R.style.AppTheme))
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("了解", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        if (Build.VERSION.SDK_INT >= 26) {//8.0新特性
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
//        BootClassLoader               //加载framework里的class    比如Application
        PathClassLoader pathClassLoader;//加载Android应用程序里的class(自己写的)  比如 MainActivity AppCompatActivity(三方框架)
        DexClassLoader dexClassLoader;  //额外提供的
    }

    /**
     * Activity.startActivityForResult ->
     * mInstrumentation.execStartActivity ->
     * ActivityTaskManager.getService().startActivity -> (hook点,替换IActivityTaskManager类型的IActivityTaskManagerSingleton变量)
     * AMS.startActivity
     */
    private void hookAMS() throws Exception {
        boolean newVersion = false;
        String hookClass = newVersion ? "android.app.ActivityTaskManager" : "android.app.ActivityManagerNative";
        String hookGetSingletonMethod = newVersion ? "getService" : "getDefault";
        String hookSingletonField = newVersion ? "IActivityTaskManagerSingleton" : "IActivityManagerSingleton";


        Class<?> IActivityTaskManager = Class.forName(newVersion ? "android.app.IActivityTaskManager" : "android.app.IActivityManager");

        Class<?> mActivityTaskManager2 = Class.forName(hookClass);
        final Object mIActivityTaskManager = mActivityTaskManager2.getDeclaredMethod(hookGetSingletonMethod).invoke(null);//静态方法不用传对象

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
         * 为了拿到 mIActivityTaskManagerSingleton
         */
        if (newVersion) {
            Class<?> mActivityManager = Class.forName(hookClass);
            Field mIActivityTaskManagerSingletonField = mActivityManager.getDeclaredField(hookSingletonField);
            mIActivityTaskManagerSingletonField.setAccessible(true);
            Object mIActivityTaskManagerSingleton = mIActivityTaskManagerSingletonField.get(null);//static静态 不需要传对象

            Class<?> mSingleTon = Class.forName("android.util.Singleton");
            Field mInstanceField = mSingleTon.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);//让虚拟机不要检测权限校验符
            mInstanceField.set(mIActivityTaskManagerSingleton, proxy);
        } else {
            Class<?> mActivityManager = Class.forName("android.app.ActivityManager");
            Field gDefaultField = mActivityManager.getDeclaredField("IActivityManagerSingleton");
            gDefaultField.setAccessible(true);
            Object gDefault = gDefaultField.get(null);//static静态 不需要传对象

            Class<?> mSingleTon = Class.forName("android.util.Singleton");
            Field mInstanceField = mSingleTon.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);//让虚拟机不要检测权限校验符
            mInstanceField.set(gDefault, proxy);
        }
    }
}