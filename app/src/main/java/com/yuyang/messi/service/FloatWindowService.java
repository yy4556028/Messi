package com.yuyang.messi.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;

import com.yuyang.lib_base.BaseApp;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.lib_base.utils.FileUtil;
import com.yuyang.lib_base.utils.LiveDataBus;
import com.yuyang.lib_base.utils.MemoryUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.view.FloatWindow.FloatWindowBigView;
import com.yuyang.messi.view.FloatWindow.FloatWindowSmallView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FloatWindowService extends LifecycleService {

    public static final String KEY_SHOW_FOREGROUND = "keyShowForeground";
    public static final String KEY_SHOW_BACKGROUND = "keyShowBackground";

    private boolean isShowForeground;//前台是否显示
    private boolean isShowBackground;//后台是否显示

    /**
     * 小悬浮窗View的实例
     */
    private FloatWindowSmallView smallWindow;

    /**
     * 大悬浮窗View的实例
     */
    private FloatWindowBigView bigWindow;

    /**
     * 小悬浮窗View的参数
     */
    private WindowManager.LayoutParams smallWindowParams;

    /**
     * 大悬浮窗View的参数
     */
    private WindowManager.LayoutParams bigWindowParams;

    /**
     * 用于控制在屏幕上添加或移除悬浮窗
     */
    private WindowManager mWindowManager;

    /**
     * 定时器，定时进行检测当前应该创建还是移除悬浮窗。
     */
    private Disposable disposable;

    private WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    public static void startMyService(Context context, boolean isShowForeground, boolean isShowBackground) {
        Intent intent = new Intent(context, FloatWindowService.class);
        intent.putExtra(FloatWindowService.KEY_SHOW_FOREGROUND, isShowForeground);
        intent.putExtra(FloatWindowService.KEY_SHOW_BACKGROUND, isShowBackground);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createSmallWindow(getApplicationContext());
        createBigWindow(getApplicationContext());
        BaseApp.getInstance().isForegroundLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if ((aBoolean && isShowForeground) ||
                        (!aBoolean && isShowBackground)) {
                    smallWindow.setVisibility(View.VISIBLE);
                    bigWindow.setVisibility(View.GONE);
                } else {
                    smallWindow.setVisibility(View.GONE);
                    bigWindow.setVisibility(View.GONE);
                }
            }
        });
        // 开启定时器，每隔0.5秒刷新一次
        disposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (smallWindow != null) {
                            TextView tvPhonePercent = smallWindow.findViewById(R.id.tvPhonePercent);

                            long totalMemorySize = MemoryUtil.getPhoneMemoryTotal();
                            long availableSize = MemoryUtil.getPhoneAvailableMemory() / 1024;
                            float percent = (totalMemorySize - availableSize) / ((float) totalMemorySize * 100);

                            tvPhonePercent.setText(String.format("%s%%", (int) (percent * 100)));
                            tvPhonePercent.setTextColor(percent >= 0.9f ?
                                    ContextCompat.getColor(getApplicationContext(), R.color.red) :
                                    ContextCompat.getColor(getApplicationContext(), R.color.white));

                            TextView tvAppTotalMemory = smallWindow.findViewById(R.id.tvAppTotalMemory);
                            tvAppTotalMemory.setText(FileUtil.getFormatSize(Runtime.getRuntime().totalMemory()));
                        }
                    }
                });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isShowForeground = intent.getBooleanExtra(KEY_SHOW_FOREGROUND, true);
        isShowBackground = intent.getBooleanExtra(KEY_SHOW_BACKGROUND, true);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeSmallWindow(getApplicationContext());
        removeBigWindow(getApplicationContext());
        if (null != disposable) {
            disposable.dispose();
            disposable = null;
        }
    }

    /**
     * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。
     *
     * @param context 必须为应用程序的Context.
     */
    private void createSmallWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = CommonUtil.getScreenMetrics().x;
        int screenHeight = CommonUtil.getScreenMetrics().y;
        if (smallWindow == null) {
            smallWindow = new FloatWindowSmallView(context);

            smallWindow.setOnSmallWindowClickListener(new FloatWindowSmallView.OnSmallWindowClickListener() {
                @Override
                public void onClick() {
                    smallWindow.setVisibility(View.GONE);
                    bigWindow.setVisibility(View.VISIBLE);
                }
            });

            if (smallWindowParams == null) {
                smallWindowParams = new WindowManager.LayoutParams();
//                smallWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;//该Type描述的是形成的窗口的层级关系
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    smallWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    smallWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                }
                smallWindowParams.format = PixelFormat.TRANSLUCENT;
                smallWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                smallWindowParams.gravity = Gravity.START | Gravity.TOP;
                smallWindowParams.width = FloatWindowSmallView.viewWidth;
                smallWindowParams.height = FloatWindowSmallView.viewHeight;
                smallWindowParams.x = screenWidth;
                smallWindowParams.y = screenHeight / 2;
                smallWindowParams.windowAnimations = R.style.Anim_Window_Right;
            }
            smallWindow.setParams(smallWindowParams);
            windowManager.addView(smallWindow, smallWindowParams);
        }
    }

    /**
     * 将小悬浮窗从屏幕上移除。
     *
     * @param context 必须为应用程序的Context.
     */
    private void removeSmallWindow(Context context) {
        if (smallWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(smallWindow);
            smallWindow = null;
        }
    }

    /**
     * 创建一个大悬浮窗。位置为屏幕正中间。
     *
     * @param context 必须为应用程序的Context.
     */
    private void createBigWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);
        if (bigWindow == null) {
            bigWindow = new FloatWindowBigView(context);

            bigWindow.setOnBigWindowClickListener(new FloatWindowBigView.OnBigWindowClickListener() {
                @Override
                public void onClose() {
                    Intent intent = new Intent(getBaseContext(), FloatWindowService.class);
                    stopService(intent);
                    LiveDataBus.get().with(FloatWindowService.class.getSimpleName()).setValue(false);
                }

                @Override
                public void onBack() {
                    // 点击返回的时候，移除大悬浮窗，创建小悬浮窗
                    smallWindow.setVisibility(View.VISIBLE);
                    bigWindow.setVisibility(View.GONE);
                }
            });

            if (bigWindowParams == null) {
                bigWindowParams = new WindowManager.LayoutParams();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    bigWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    bigWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                }
                bigWindowParams.format = PixelFormat.TRANSLUCENT;
                bigWindowParams.gravity = Gravity.CENTER;
                bigWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                bigWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                bigWindowParams.windowAnimations = R.style.Anim_PopWin_Fade;
            }
            windowManager.addView(bigWindow, bigWindowParams);
        }
    }

    /**
     * 将大悬浮窗从屏幕上移除。
     *
     * @param context 必须为应用程序的Context.
     */
    private void removeBigWindow(Context context) {
        if (bigWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(bigWindow);
            bigWindow = null;
        }
    }
}
