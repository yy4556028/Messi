package com.yuyang.lib_base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDex;

import com.yuyang.lib_base.utils.LogUtil;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

/**
 * https://blankj.com/
 */
public class BaseApp extends Application {

    protected static BaseApp appContext;

    private int count = 0;

    public WeakReference<Activity> currentActivity;

    public MutableLiveData<Boolean> isForegroundLiveData = new MutableLiveData<>();

    @Override
    public void onCreate() {
        super.onCreate();
        registerAppLifeCycle();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MonitorUtil.getInstance().onAttachBaseContext(this);
//        TraceCompat.beginSection("Messi");
//        Debug.startMethodTracing("Messi");
        appContext = this;
        //http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/1218/3789.html
        MultiDex.install(this);
    }

    public static BaseApp getInstance() {
        return appContext;
    }

    private void registerAppLifeCycle() {
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLifeCycle());
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NotNull Activity activity, Bundle bundle) {
                currentActivity = new WeakReference<>(activity);
            }

            @Override
            public void onActivityStarted(@NotNull Activity activity) {
                if (count == 0) {
                    isForegroundLiveData.setValue(true);
                    LogUtil.i("LifeCycle", "切到前台");
                }
                count++;
            }

            @Override
            public void onActivityResumed(@NotNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NotNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NotNull Activity activity) {
                count--;
                if (count == 0) {
                    isForegroundLiveData.setValue(false);
                    LogUtil.i("LifeCycle", "切到后台");
                }
            }

            @Override
            public void onActivitySaveInstanceState(@NotNull Activity activity, @NotNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NotNull Activity activity) {

            }
        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private static class AppLifeCycle implements DefaultLifecycleObserver {

        @Override
        public void onStart(@NonNull LifecycleOwner owner) {
            DefaultLifecycleObserver.super.onStart(owner);
        }

        @Override
        public void onStop(@NonNull LifecycleOwner owner) {
            DefaultLifecycleObserver.super.onStop(owner);
        }

        // 方法名随便取，注解才是重点
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        void onForeground() {
//        Log.i("LifecycleObserver", "应用回到前台");
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        void onBackground() {
//        Log.i("LifecycleObserver", "应用退到后台");
        }
    }
}