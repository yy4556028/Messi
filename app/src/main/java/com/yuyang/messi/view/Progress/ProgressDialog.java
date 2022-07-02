package com.yuyang.messi.view.Progress;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.yuyang.messi.R;

/**
 * 创建者: yuyang
 * 创建日期: 2015-07-24
 * 创建时间: 21:14
 * ProgressDialog : progress dialog 设置show arrow 有时会没有箭头 bug
 *
 * @author yuyang
 * @version 1.0
 */

public class ProgressDialog extends Dialog {

    private CircleProgressBar progressBar;

    public ProgressDialog(Context context, boolean withText) {
        super(context, R.style.MyDialog);

        setContentView(R.layout.view_progress_dialog);
        setCanceledOnTouchOutside(false);
        getWindow().getAttributes().dimAmount = 0f;

        if (withText) {
            progressBar = (CircleProgressBar) findViewById(R.id.progress_with_text);
        } else {
            progressBar = (CircleProgressBar) findViewById(R.id.progress_without_text);
        }

        progressBar.setVisibility(View.VISIBLE);
    }

    public void showArrow(boolean show) {
        progressBar.setShowArrow(show);
    }

    public void setColorSchemeResources(int... colorResIds) {
        progressBar.setColorSchemeResources(colorResIds);
    }

    public void setProgress(int progress) {
        progressBar.setProgress(progress);
    }

    public void setDimAmount(float dimAmount) {
        getWindow().getAttributes().dimAmount = dimAmount;
    }

    public void setCanceledOnTouchOutside(boolean b) {
        super.setCanceledOnTouchOutside(b);
    }

    @Override
    public void show() {
        if (!isShowing()) {
            progressBar.setVisibility(View.VISIBLE);
            super.show();
        }
    }

    public void hide() {
        if (isShowing()) {
            progressBar.setVisibility(View.INVISIBLE);
        }
        dismiss();
    }

}
