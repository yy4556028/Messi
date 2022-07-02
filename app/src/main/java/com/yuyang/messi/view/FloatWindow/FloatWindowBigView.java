package com.yuyang.messi.view.FloatWindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.yuyang.messi.R;

public class FloatWindowBigView extends LinearLayout {

    /**
     * 记录大悬浮窗的宽度
     */
    public static int viewWidth;

    /**
     * 记录大悬浮窗的高度
     */
    public static int viewHeight;

    public FloatWindowBigView(final Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_float_window_big, this);
        View view = findViewById(R.id.big_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        Button close = findViewById(R.id.close);
        Button back = findViewById(R.id.back);
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onClose();
            }
        });
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onBack();
            }
        });
    }

    public interface OnBigWindowClickListener {
        void onClose();

        void onBack();
    }

    private OnBigWindowClickListener listener;

    public void setOnBigWindowClickListener(OnBigWindowClickListener l) {
        this.listener = l;
    }
}
