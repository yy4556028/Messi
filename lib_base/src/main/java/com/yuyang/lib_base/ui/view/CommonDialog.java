package com.yuyang.lib_base.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.yuyang.lib_base.R;

/**
 * 自定义Dialog
 */
public class CommonDialog extends Dialog {

    private Context context;

    private TextView tvTitle;
    private TextView tvSubtitle;
    private FrameLayout extraContainer;
    private MaterialButton btnLeft;
    private MaterialButton btnRight;

    private View customView;

    public CommonDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_common);
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        extraContainer = findViewById(R.id.frameLytCustom);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);

        if (customView != null) {
            extraContainer.addView(customView);
            extraContainer.setVisibility(View.VISIBLE);
        } else {
            extraContainer.setVisibility(View.GONE);
        }

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
    }

    public CommonDialog setCustomView(View customView) {
        this.customView = customView;
        if (isShowing()) {
            if (customView != null && extraContainer != null) {
                this.extraContainer.addView(customView);
                this.extraContainer.setVisibility(View.VISIBLE);
            } else {
                this.extraContainer.setVisibility(View.GONE);
            }
        }
        return this;
    }

    public void setTitle(CharSequence title) {
        tvTitle.setText(title);
        tvTitle.setVisibility(TextUtils.isEmpty(title)?View.GONE:View.VISIBLE);
    }

    public void setSubtitle(CharSequence subtitle) {
        tvSubtitle.setText(subtitle);
        tvSubtitle.setVisibility(TextUtils.isEmpty(subtitle)?View.GONE:View.VISIBLE);
    }

    public void setBtnText(CharSequence leftStr, CharSequence rightStr) {
        if (TextUtils.isEmpty(leftStr)) {
            btnLeft.setVisibility(View.GONE);
        } else {
            btnLeft.setText(leftStr);
            btnLeft.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(rightStr)) {
            btnRight.setVisibility(View.GONE);
        } else {
            btnRight.setText(rightStr);
            btnRight.setVisibility(View.VISIBLE);
        }
    }

    public MaterialButton getBtnLeft() {
        return btnLeft;
    }

    public MaterialButton getBtnRight() {
        return btnRight;
    }

    public void setOnBtnLeftClickListener(final View.OnClickListener onAcceptButtonClickListener) {
        btnLeft.setOnClickListener(onAcceptButtonClickListener);
    }

    public void setOnBtnRightClickListener(final View.OnClickListener onCancelButtonClickListener) {
        btnRight.setOnClickListener(onCancelButtonClickListener);
    }
}
