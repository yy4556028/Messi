package com.yuyang.messi.ui.live;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.yuyang.lib_base.utils.SystemBarUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

public class LivePreActivity extends AppBaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_pre;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setStatusBar() {
        SystemBarUtil.fullScreen_immersive(this, true, true, true, false, false);
    }
}
