package com.yuyang.messi.view.Progress;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yuyang.messi.R;
import com.yuyang.messi.view.SwordView;

public class SwordLoadingDialog extends Dialog {

    private TextView textView;

    private Context context;

    public SwordLoadingDialog(Context context) {
        super(context, R.style.MyDialog);
        this.context = context;
        init();
    }

    private void init() {
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_sword_loading);
        SwordView swordView = findViewById(R.id.view_sword_loading_swordView);
        swordView.getAnim().start();
        swordView.setBgColor(Color.TRANSPARENT);
        textView = (TextView) findViewById(R.id.view_sword_loading_textView);
    }

    @Override
    public void show() {
        show(null);
    }

    public void show(String string) {

        if (!isShowing() && !((Activity) context).isFinishing()) {
            super.show();
        }

        if (TextUtils.isEmpty(string)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(string);
        }
    }

    @Override
    public void dismiss() {
        if (context != null && !((Activity) context).isFinishing()) {
            super.dismiss();
        }
    }
}
