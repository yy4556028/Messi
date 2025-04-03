package com.yuyang.messi.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

/**
 * 创建者: yuyang
 * 创建日期: 2015-07-23
 * 创建时间: 11:14
 * TipActivity: 仿QQ锁屏弹窗
 *
 * @author yuyang
 * @version 1.0
 */
public class TipActivity extends AppBaseActivity {

    private TextView textView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tip;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        initViews();
        initEvents();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        if (!pm.isInteractive()) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP, "messi:bright");
            wl.acquire();
            wl.release();
        }
    }

    private void initViews() {
        textView = findViewById(R.id.activity_tip_text);
    }

    private void initEvents() {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast("tip");
            }
        });
    }
}
