package com.yuyang.lib_base.utils;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.yuyang.lib_base.BaseApp;

public class ToastUtil {

    private static final Handler handler = new Handler(Looper.getMainLooper());

    private static ShapeDrawable bgShapeDrawable;

    public static void showToast(CharSequence msg) {
        showToast(msg, -1);
    }

    public static void showToast(CharSequence msg, @DrawableRes int resId) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(BaseApp.getInstance(), msg, Toast.LENGTH_SHORT);

                TextView textView = new TextView(BaseApp.getInstance());
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(16);
                textView.setText(msg);
                textView.setTextColor(Color.WHITE);
                textView.setPadding(46, 36, 46, 36);
                textView.setCompoundDrawablePadding(23);
                if (resId != -1) {
                    Drawable drawable = ContextCompat.getDrawable(BaseApp.getInstance(), resId);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    textView.setCompoundDrawables(null, drawable, null, null);
                }

                if (bgShapeDrawable == null) {
                    int outRadius = PixelUtils.dp2px(12);
                    float[] outRadii = {outRadius, outRadius, outRadius, outRadius, outRadius, outRadius, outRadius, outRadius};
                    RoundRectShape roundRectShape = new RoundRectShape(outRadii, null, null);
                    bgShapeDrawable = new ShapeDrawable(roundRectShape);
                    bgShapeDrawable.getPaint().setStyle(Paint.Style.FILL);
                    bgShapeDrawable.getPaint().setColor(Color.parseColor("#B2000000"));
                }

                textView.setBackground(bgShapeDrawable);
                toast.setView(textView);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }
}
