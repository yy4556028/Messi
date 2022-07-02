package com.yuyang.lib_base.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;

import com.yuyang.lib_base.R;

public class BottomDialog extends Dialog {

    private View view;

    public BottomDialog(Context context, @LayoutRes int layoutId) {
        this(context, View.inflate(context, layoutId, null));
    }

    public BottomDialog(Context context, View view) {
        super(context, R.style.CustomDialog);
        this.view = view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);

        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
        dialogWindow.setWindowAnimations(R.style.Anim_Pop_Bottom);
    }
}
