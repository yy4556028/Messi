package com.yuyang.messi.ui.finance;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

public class FinanceHomeActivity extends AppBaseActivity {

    public static final String APP_KEY = "7fd2028b4beb93be30ee73472434fab8";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_finance_home;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showTitle("Finance");

        findViewById(R.id.activity_finance_home_singleSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = new EditText(getActivity());
                AlertDialog alertDialog =
                        new AlertDialog.Builder(getActivity())
                                .setTitle("请输入股票代码")
                                .setView(editText)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String gid = editText.getText().toString();
                                        if (!TextUtils.isEmpty(gid)) {
                                            StockAnalysisActivity.launchActivity(getActivity(), gid);
                                        }
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .create();
                alertDialog.show();
                //键盘把dialog推上去
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            }
        });
    }
}
