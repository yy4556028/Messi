package com.yuyang.messi.view.Progress;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yuyang.messi.R;

/**
 * @author yuy 2016-08-02
 */
public class CustomProgressDialog extends Dialog {

    private TextView textView;

    private Context context;

    public CustomProgressDialog(Context context) {
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
        setContentView(R.layout.view_custom_progressdialog);
        textView = (TextView) findViewById(R.id.view_custom_pd_text);
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
