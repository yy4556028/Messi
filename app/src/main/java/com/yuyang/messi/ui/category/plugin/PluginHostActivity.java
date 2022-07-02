package com.yuyang.messi.ui.category.plugin;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.yuyang.lib_plugin.PluginManager;
import com.yuyang.lib_plugin.ProxyActivity;
import com.yuyang.messi.ui.base.AppBaseActivity;

public class PluginHostActivity extends AppBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button button = new Button(this);
        button.setText("开启插件");
        setContentView(button);

        String path = getExternalFilesDir(null).getAbsolutePath() + "/plugin-debug.apk";
        PluginManager.getInstance().loadPlugin(this, path);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PluginHostActivity.this, ProxyActivity.class);

                // 获取  " 插件 " 模块中的 Activity 数组信息
                ActivityInfo[] activityInfos = PluginManager.getInstance().getmPackageInfo().activities;

                // 获取的插件包中的 Activity 不为空 , 才进行界面跳转
                if (activityInfos.length > 0) {
                    // 这里取插件包中的第 0 个 Activity
                    // 次序就是在 AndroidManifest.xml 清单文件中定义 Activity 组件的次序
                    // 必须将 Launcher Activity 定义在第一个位置
                    // 不能在 Launcher Activity 之前定义 Activity 组件
                    // 传入的是代理的目标组件的全类名
                    intent.putExtra("CLASS_NAME", activityInfos[0].name);
                    startActivity(intent);
                }
            }
        });
    }
}
