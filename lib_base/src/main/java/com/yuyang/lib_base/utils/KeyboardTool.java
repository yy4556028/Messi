package com.yuyang.lib_base.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.Lifecycle;

import com.yuyang.lib_base.BaseApp;

import java.lang.reflect.Method;

/**
 * https://juejin.cn/post/7150453629021847566
 */
public final class KeyboardTool {

    private static final String TAG = KeyboardTool.class.getSimpleName();

    private KeyboardTool() {
    }

    public static void showKeyboard(@NonNull View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//              imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);//应用退出后，键盘不会收起
        imm.showSoftInput(view, 0);
    }

    /**
     * 显示光标但不显示键盘
     * 初始化里
     *
     * @param activity
     * @param editText
     */
    public static void showCursorHideKeyBoard(Activity activity, EditText editText) {

        if (android.os.Build.VERSION.SDK_INT <= 10) {
            editText.setInputType(InputType.TYPE_NULL);
        } else {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setSoftInputShownOnFocus;
                setSoftInputShownOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setSoftInputShownOnFocus.setAccessible(true);
                setSoftInputShownOnFocus.invoke(editText, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void hideKeyboard(Context context) {
        if (!(context instanceof Activity) || ((Activity) context).getCurrentFocus() == null) {
            return;
        }
        try {
            View view = ((Activity) context).getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            view.clearFocus();
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setKeyboardChangeListener(AppCompatActivity activity, final OnKeyboardChangeListener listener) {

        Log.e(TAG, activity.getLifecycle().getCurrentState() + "");
        if (!activity.getLifecycle().getCurrentState().equals(Lifecycle.State.INITIALIZED)) {
            throw new IllegalStateException("需要在 onCreate 里调用此方法，以获取键盘未弹起时的高度");
        }

        final View decorView = activity.getWindow().getDecorView();

        //final View contentView = decorView.findViewById(android.R.id.content);//app展示内容大小，不包含状态栏，不导航栏，如果有ActionBar，也不包含ActionBar
        final int navigationHeight = SystemBarUtil.getNavigationBarHeightIfRoom(activity);
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private int originalBottom = 0;
            private int keyboardHeight = -1;

            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);//可视区域大小 不包含状态栏和导航栏

                if (originalBottom == 0) {
                    originalBottom = rect.bottom;
                }

                final int rootHeight = decorView.getRootView().getHeight();//屏幕高度 包含状态栏和导航栏

                int newHeight = originalBottom - rect.bottom;
                if (keyboardHeight != -1 && newHeight != keyboardHeight) {
                    if (newHeight > 0) {
                        if (listener != null) {
                            listener.onKeyboardChange(true, newHeight);
                        }
                    } else {
                        if (listener != null) {
                            listener.onKeyboardChange(false, newHeight);
                        }
                    }
                }
                keyboardHeight = newHeight;
            }
        });
    }

    public interface OnKeyboardChangeListener {
        void onKeyboardChange(boolean isShow, int keyboardHeight);
    }

    public static void showHideIme(View view, boolean show) {
        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(view);
        if (controller == null) return;
        if (show) {
            controller.show(WindowInsetsCompat.Type.ime());
        } else {
            controller.hide(WindowInsetsCompat.Type.ime());
        }
    }

    public static int getScreenHeight1() {
        WindowManager wm = (WindowManager) BaseApp.getInstance().getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) {
            return BaseApp.getInstance().getResources().getDisplayMetrics().heightPixels;
        }
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.y;
    }
}