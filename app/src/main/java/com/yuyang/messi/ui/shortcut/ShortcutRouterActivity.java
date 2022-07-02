package com.yuyang.messi.ui.shortcut;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.WindowManager.LayoutParams;

import androidx.annotation.Nullable;

import com.yuyang.lib_base.ui.base.BaseActivity;
import com.yuyang.messi.kotlinui.main.MainActivity;
import com.yuyang.messi.ui.category.sensor.SensorActivity;

/**
 * https://blog.csdn.net/lanfei1027/article/details/48297409
 * https://cloud.tencent.com/developer/article/1444202
 */

/**
 * 点击快捷方式跳转时，会短暂出现app上一个页面的停留
 * 如果想不展示上一个页面，直接展示要跳转页面
 * 可以配置 ShortcutRouterActivity的xml android:taskAffinity="xxx.xxx" (如shortcut.pin)
 * 同时要跳转的页面配置 android:launchMode="singleTask"
 */
public class ShortcutRouterActivity extends BaseActivity {

    public final static String KEY_SHORTCUT_ID = "key_shortcut_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().addFlags(LayoutParams.FLAG_NOT_TOUCHABLE | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        super.onCreate(savedInstanceState);
        launcherActivity();
        finish();
    }

    private void launcherActivity() {
        String shortcutId = getIntent().getStringExtra(KEY_SHORTCUT_ID);
        if (TextUtils.isEmpty(shortcutId)) {
            startActivity(new Intent(getActivity(), MainActivity.class));
            return;
        }
        switch (shortcutId) {
            case "BlueTooth": {
                startActivity(new Intent(getActivity(), SensorActivity.class));
                break;
            }
        }
    }

    public boolean selectPeople(Context context, String title, int type) {
        try {
            Intent intent = new Intent();
            Class clazz = context.getClassLoader().loadClass("net.luculent.unify.ui.evnet.SelectPersonActivity");
            intent.setClass(context, clazz);
            intent.putExtra("key_type", type);
            intent.putExtra("key_title", title);
            context.startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        finish();
        return true;
    }
}
