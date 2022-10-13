package com.yuyang.messi.ui.setting;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.yuyang.lib_base.bean.PopBean;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.ui.view.picker.BottomChooseDialog;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.lib_base.utils.LiveDataBus;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.MessiApp;
import com.yuyang.messi.R;
import com.yuyang.messi.service.FloatWindowService;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.utils.SharedPreferencesUtil;

import java.util.Arrays;
import java.util.List;

public class SettingActivity extends AppBaseActivity {

    private MaterialCardView lytOpenFloatWindow;
    private MaterialCheckBox cbOpenFloatWindow;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initEvents();

        LiveDataBus.get()
                .with(FloatWindowService.class.getSimpleName(), Boolean.class)
                .observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(@Nullable Boolean b) {
                        if (Boolean.FALSE.equals(b)) {
                            cbOpenFloatWindow.setChecked(CommonUtil.isServiceRunning(FloatWindowService.class.getName()));
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cbOpenFloatWindow.setChecked(CommonUtil.isServiceRunning(FloatWindowService.class.getName()));
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("设置");

        lytOpenFloatWindow = findViewById(R.id.lytOpenFloatWindow);
        cbOpenFloatWindow = findViewById(R.id.cbOpenFloatWindow);
    }

    public void initEvents() {
        findViewById(R.id.btnClearData).setOnClickListener(v -> startActivity(new Intent(getActivity(), DataCleanActivity.class)));
        findViewById(R.id.btnSelectDarkMode).setOnClickListener(v -> {
            int mode = AppCompatDelegate.getDefaultNightMode();
            int index = -1;
            switch (mode) {
                case AppCompatDelegate.MODE_NIGHT_NO:
                    index = 0;
                    break;
                case AppCompatDelegate.MODE_NIGHT_YES:
                    index = 1;
                    break;
                case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                    index = 2;
                    break;
                case AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY:
                    index = 3;
                    break;
                case AppCompatDelegate.MODE_NIGHT_UNSPECIFIED:
                    index = -1;
                    break;
                default:
                    ToastUtil.showToast("Unknown mode " + mode);
                    break;
            }
            List<PopBean> modeList = Arrays.asList(
                    new PopBean(null, "非深色模式"),
                    new PopBean(null, "深色模式"),
                    new PopBean(null, "跟随系统"),
                    new PopBean(null, "根据电量"));
            if (index >= 0) {
                modeList.get(index).setCheck(true);
            }

            BottomChooseDialog.showSingle(getActivity(),
                    null,
                    modeList,
                    new BottomChooseDialog.SingleChoiceListener() {
                        @Override
                        public void onItemClick(int index, PopBean popBean) {
                            switch (index) {
                                case 0:
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                    SharedPreferencesUtil.setNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                    break;
                                case 1:
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                    SharedPreferencesUtil.setNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                    break;
                                case 2:
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                                    SharedPreferencesUtil.setNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                                    break;
                                case 3:
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                                    SharedPreferencesUtil.setNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                                    break;
                            }
                        }
                    }, true);
        });
        findViewById(R.id.btnScore).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + getActivity().getPackageName()));
            startActivity(intent);
        });

        lytOpenFloatWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbOpenFloatWindow.toggle();
            }
        });

        cbOpenFloatWindow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!Settings.canDrawOverlays(MessiApp.getInstance())) {
                            //启动Activity让用户授权
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setData(Uri.parse("package:" + MessiApp.getInstance().getPackageName()));
                            startActivity(intent);
                            return;
                        }
                    }
                    FloatWindowService.startMyService(getActivity(), true, false);
                } else {
                    getActivity().stopService(new Intent(getActivity(), FloatWindowService.class));
                }
            }
        });
    }

    //检查当前系统是否已开启暗黑模式
    protected boolean getDarkModeStatus() {
        int mode = Resources.getSystem().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return mode == Configuration.UI_MODE_NIGHT_YES;
    }
}
