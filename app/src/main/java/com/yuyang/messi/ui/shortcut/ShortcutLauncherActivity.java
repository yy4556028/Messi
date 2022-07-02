package com.yuyang.messi.ui.shortcut;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

public class ShortcutLauncherActivity extends AppBaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_shortcut_launcher;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("独立Task");
    }
}
