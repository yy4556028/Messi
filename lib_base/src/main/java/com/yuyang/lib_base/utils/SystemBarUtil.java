package com.yuyang.lib_base.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Insets;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;

import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.yuyang.lib_base.BaseApp;

import java.lang.reflect.Method;


//SystemUI Flag详解及使用情景：https://www.jianshu.com/p/e6656707f56c
public final class SystemBarUtil {

    private SystemBarUtil() {
    }

    public static void showSystemBar(Activity activity, @WindowInsetsCompat.Type.InsetsType int showType) {
        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(activity.getWindow().getDecorView());
        if (controller != null) {
            controller.show(showType);
        }
    }

    public static void hideSystemBar(Activity activity, @WindowInsetsCompat.Type.InsetsType int hideType) {
        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(activity.getWindow().getDecorView());
        if (controller != null) {
            controller.hide(hideType);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void fullScreen_immersive(Activity activity,
                                            boolean showStatusBar, boolean transStatusBar,
                                            boolean showNavBar, boolean transNavBar,
                                            boolean showBarsBySwipe) {
//        immersiveStatusBar(activity);
        Window window = activity.getWindow();
        View decorView = window.getDecorView();

        int uiOptions = decorView.getSystemUiVisibility()
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN         //Activity全屏显示，状态栏显示在Activity页面上面
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION    //在不隐藏导航栏的情况下，将Activity的显示范围扩展到导航栏底部
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;            //布局稳定

        decorView.setSystemUiVisibility(uiOptions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        if (showStatusBar) {
            showSystemBar(activity, WindowInsetsCompat.Type.statusBars());
        } else {
            hideSystemBar(activity, WindowInsetsCompat.Type.statusBars());
        }

        if (showNavBar) {
            showSystemBar(activity, WindowInsetsCompat.Type.navigationBars());
        } else {
            hideSystemBar(activity, WindowInsetsCompat.Type.navigationBars());
        }

        if (transStatusBar) {
            //设置了此flag,系统会自动设置View.SYSTEM_UI_FLAG_LAYOUT_STABLE和View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            //设置了此flag,系统会自动设置View.SYSTEM_UI_FLAG_LAYOUT_STABLE和View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (transNavBar) {
            //设置了此flag,系统会自动设置 View.SYSTEM_UI_FLAG_LAYOUT_STABLE 和 View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else {
            //设置了此flag,系统会自动设置 View.SYSTEM_UI_FLAG_LAYOUT_STABLE 和 View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(activity.getWindow().getDecorView());
        if (controller != null) {
            if (showBarsBySwipe) {
                controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            } else {
                controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_TOUCH);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//适配挖孔屏
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            activity.getWindow().setAttributes(lp);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static int getNavigationBarH() {
        WindowManager wm = (WindowManager) BaseApp.getInstance().getSystemService(Context.WINDOW_SERVICE);
        WindowMetrics windowMetrics = wm.getCurrentWindowMetrics();
        WindowInsets windowInsets = windowMetrics.getWindowInsets();
        Insets insets = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars() | WindowInsets.Type.displayCutout());
        return insets.bottom;
    }

    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    private static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * 判断虚拟按键栏是否重写
     *
     * @return
     */
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }


    /**
     * 获取虚拟按键的高度
     * 1. 全面屏下
     * 1.1 开启全面屏开关-返回0
     * 1.2 关闭全面屏开关-执行非全面屏下处理方式
     * 2. 非全面屏下
     * 2.1 没有虚拟键-返回0
     * 2.1 虚拟键隐藏-返回0
     * 2.2 虚拟键存在且未隐藏-返回虚拟键实际高度
     */
    public static int getNavigationBarHeightIfRoom(Context context) {
        if (navigationGestureEnabled(context)) {
            return 0;
        }
        return getCurrentNavigationBarHeight(((Activity) context));
    }

    /**
     * 全面屏（是否开启全面屏开关 0 关闭  1 开启）
     *
     * @param context
     * @return
     */
    private static boolean navigationGestureEnabled(Context context) {
        int val = Settings.Global.getInt(context.getContentResolver(), getDeviceInfo(), 0);
        return val != 0;
    }

    /**
     * 获取设备信息（目前支持几大主流的全面屏手机，亲测华为、小米、oppo、魅族、vivo都可以）
     *
     * @return
     */
    private static String getDeviceInfo() {
        String brand = Build.BRAND;
        if (TextUtils.isEmpty(brand)) return "navigationbar_is_min";

        if (brand.equalsIgnoreCase("HUAWEI")) {
            return "navigationbar_is_min";
        } else if (brand.equalsIgnoreCase("XIAOMI")) {
            return "force_fsg_nav_bar";
        } else if (brand.equalsIgnoreCase("VIVO")) {
            return "navigation_gesture_on";
        } else if (brand.equalsIgnoreCase("OPPO")) {
            return "navigation_gesture_on";
        } else {
            return "navigationbar_is_min";
        }
    }

    /**
     * 非全面屏下 虚拟键实际高度(隐藏后高度为0)
     *
     * @param activity
     * @return
     */
    private static int getCurrentNavigationBarHeight(Activity activity) {
        return getNavBarHeight(activity);
    }

    /**
     * 非全面屏下 虚拟键高度(无论是否隐藏)
     *
     * @param context
     * @return
     */
    private static int getNavBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}