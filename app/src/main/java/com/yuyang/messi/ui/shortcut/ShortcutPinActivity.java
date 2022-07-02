package com.yuyang.messi.ui.shortcut;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import androidx.annotation.Nullable;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

public class ShortcutPinActivity extends AppBaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_shortcut_pin;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("PinShortcut");

        findViewById(R.id.activity_shortcut_addBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ShortcutUtil.addShortcut(getActivity(), "蓝牙Demo", R.drawable.ssdk_oks_classic_alipay, "BlueTooth");
            }
        });
        findViewById(R.id.activity_shortcut_deleteBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ShortcutUtil.delShortcut("支付宝YY");
            }
        });
        findViewById(R.id.activity_shortcut_checkBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast("" + ShortcutUtil.hasShortcut("支付宝YY"));
            }
        });
    }
}
