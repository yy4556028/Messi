package com.yuyang.messi.kotlinui.main.tools;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.yuyang.lib_base.ui.view.CommonDialog;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.room.database.ModuleDatabase;
import com.yuyang.messi.room.entity.ModuleEntity;
import com.yuyang.lib_base.browser.BrowserActivity;
import com.yuyang.messi.ui.suning.DouyaLoginActivity;
import com.yuyang.messi.ui.suning.DouyaWebActivity;
import com.yuyang.messi.utils.SharedPreferencesUtil;

public class ModuleTool {

    public static void dealModuleClick(Context context, ModuleEntity moduleEntity) {
        switch (moduleEntity.getName()) {
            case "豆芽登录": {
                String url = SharedPreferencesUtil.getString(
                        DouyaLoginActivity.class.getSimpleName(),
                        null);
                if (TextUtils.isEmpty(url)) {
                    context.startActivity(new Intent(context, DouyaLoginActivity.class));
                } else {
                    DouyaWebActivity.launchActivity(context, url);
                }
                return;
            }
            case "添加网址": {
                showAddNetDialog(context);
                return;
            }
            case "清空网址": {
                CommonDialog commonDialog = new CommonDialog(context);
                commonDialog.show();
                commonDialog.setTitle("清空网址");
                commonDialog.setSubtitle("清空后不可恢复，确定清空？");
                commonDialog.setBtnText("取消", "确定");
                commonDialog.setOnBtnRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        commonDialog.dismiss();
                        ModuleDatabase.getInstance().deleteTable();
//                        ModuleDatabase.getInstance().getModuleDao().deleteModule();
                    }
                });
                return;
            }
        }

        if (moduleEntity.getClazz() != null) {
            context.startActivity(new Intent(context, moduleEntity.getClazz()));
            return;
        }
        if (!TextUtils.isEmpty(moduleEntity.getNetUrl())) {
            BrowserActivity.launchActivity(context, null, moduleEntity.getNetUrl());
            return;
        }
        if (!TextUtils.isEmpty(moduleEntity.getName())) {
            ToastUtil.showToast(moduleEntity.getName());
        }

//        "https://m.pianku.li/py/lRmas12ZsZTN_1.html"
    }

    private static void showAddNetDialog(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        EditText etTitle = new EditText(context);
        etTitle.setTextSize(15);
        etTitle.setSingleLine(true);
        etTitle.setHint("请输入标题");
        linearLayout.addView(etTitle);
        EditText etNet = new EditText(context);
        etNet.setTextSize(15);
        etNet.setSingleLine(true);
        etNet.setHint("请输入网址");
        linearLayout.addView(etNet);

        CommonDialog commonDialog = new CommonDialog(context);
        commonDialog.show();
        commonDialog.setTitle("添加网址");
        commonDialog.setSubtitle(null);
        commonDialog.setBtnText(null, "确定");
        commonDialog.setCustomView(linearLayout);
        commonDialog.setOnBtnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonDialog.dismiss();
                String title = etTitle.getText().toString();
                String netUrl = etNet.getText().toString();
                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(netUrl)) {
                    ToastUtil.showToast("输入有误");
                    return;
                }
                ModuleEntity moduleEntity = new ModuleEntity();
                moduleEntity.setName(title);
                moduleEntity.setNetUrl(netUrl);
                moduleEntity.setIcon("icon_net_url");
                ModuleDatabase.getInstance().getModuleDao().addModule(moduleEntity);
            }
        });
    }
}
