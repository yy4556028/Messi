package com.yuyang.lib_base.utils.statusbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.yuyang.lib_base.BaseApp;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * DrawerLayout不侵入状态栏 根布局fitSystemWindows="true"
 * DrawerLayout侵入状态栏 根布局fitSystemWindows="true" + StatusBarUtil.setColorNoTranslucentForDrawerLayout(getActivity(), drawerLayout, getResources().getColor(R.color.white));
 */
public class MyStatusBarUtil {

    /**
     * 修改状态栏颜色，支持4.4以上版本
     *
     * @param color 颜色
     */
    public static void setStatusBarColor(Activity activity, @ColorInt int color) {
        Window window = activity.getWindow();
        window.setStatusBarColor(color);
    }

    /**
     * 代码实现android:fitsSystemWindows
     *
     * @param activity
     */
    public static void setRootViewFitsSystemWindows(Activity activity, boolean fitSystemWindows) {
        ViewGroup winContent = activity.findViewById(android.R.id.content);
        if (winContent.getChildCount() > 0) {
            ViewGroup rootView = (ViewGroup) winContent.getChildAt(0);
            if (rootView != null) {
                rootView.setFitsSystemWindows(fitSystemWindows);
            }
        }
    }

    /**
     * 设置状态栏深色浅色切换
     */
    public static boolean setStatusBarDarkTheme(Activity activity, boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setBarColor(activity, dark);
                return true;
            } else if (OSUtils.isMiui()) {
                setMiuiUI(activity, dark);
            } else if (OSUtils.isFlyme()) {
                setFlymeUI(activity, dark);
            } else {//其他情况
                return false;
            }
            return true;
        }
        return false;
    }

    //设置6.0 状态栏深色浅色切换
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void setBarColor(Activity activity, boolean dark) {
//        View decorView = activity.getWindow().getDecorView();
//        int vis = decorView.getSystemUiVisibility();
//        if (dark) {
//            vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
//        } else {
//            vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
//        }
//        if (decorView.getSystemUiVisibility() != vis) {
//            decorView.setSystemUiVisibility(vis);
//        }
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(activity.getWindow(), activity.getWindow().getDecorView());
        //状态栏文字颜色改为黑色：
        controller.setAppearanceLightStatusBars(dark);
        controller.setAppearanceLightNavigationBars(dark);
    }

    //设置Flyme 状态栏深色浅色切换
    public static boolean setFlymeUI(Activity activity, boolean dark) {
        try {
            Window window = activity.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (dark) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            window.setAttributes(lp);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //设置MIUI 状态栏深色浅色切换
    public static boolean setMiuiUI(Activity activity, boolean dark) {
        try {
            Window window = activity.getWindow();
            Class<?> clazz = activity.getWindow().getClass();
            @SuppressLint("PrivateApi") Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getDeclaredMethod("setExtraFlags", int.class, int.class);
            extraFlagField.setAccessible(true);
            if (dark) {    //状态栏亮色且黑色字体
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
            } else {
                extraFlagField.invoke(window, 0, darkModeFlag);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //获取状态栏高度
    public static int getStatusBarHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowManager wm = (WindowManager) BaseApp.getInstance().getSystemService(Context.WINDOW_SERVICE);
            WindowMetrics windowMetrics = wm.getCurrentWindowMetrics();
            WindowInsets windowInsets = windowMetrics.getWindowInsets();
            Insets insets = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.statusBars() | WindowInsets.Type.displayCutout());
            return insets.top;
        } else {
            int result = 0;
            int resourceId = Resources.getSystem().getIdentifier(
                    "status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = Resources.getSystem().getDimensionPixelSize(resourceId);
            }
            return result;
        }
    }

    public static int getStatusBarHeight2(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    public static int getStatusBarHeight3() {
        int statusBarHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = Resources.getSystem().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 获取TopBar的高度
     *
     * @param activity
     * @return
     */
    public static int getTopBarHeight(Activity activity) {
        return activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
    }
}