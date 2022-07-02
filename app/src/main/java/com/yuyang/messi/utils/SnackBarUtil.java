package com.yuyang.messi.utils;

import android.app.Activity;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.yuyang.messi.MessiApp;
import com.yuyang.messi.R;

public class SnackBarUtil {
    private static final int color_danger = ContextCompat.getColor(MessiApp.getInstance(), R.color.red);
    private static final int color_success = ContextCompat.getColor(MessiApp.getInstance(), R.color.green);
    private static final int color_info = ContextCompat.getColor(MessiApp.getInstance(), R.color.blue);
    private static final int color_warning = ContextCompat.getColor(MessiApp.getInstance(), R.color.yellow);

    private static final int color_action = ContextCompat.getColor(MessiApp.getInstance(), R.color.white);

    private final Snackbar mSnackbar;

    private SnackBarUtil(Snackbar snackbar) {
        mSnackbar = snackbar;
    }

    public static SnackBarUtil makeShort(Activity activity, String text) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT);
        return new SnackBarUtil(snackbar);
    }

    public static SnackBarUtil makeShort(View view, String text) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
        return new SnackBarUtil(snackbar);
    }

    public static SnackBarUtil makeLong(View view, String text) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        return new SnackBarUtil(snackbar);
    }

    private View getSnackBarLayout(Snackbar snackbar) {
        if (snackbar != null) {
            return snackbar.getView();
        }
        return null;

    }


    private Snackbar setSnackBarBackColor(int colorId) {
        View snackBarView = getSnackBarLayout(mSnackbar);
        if (snackBarView != null) {
            snackBarView.setBackgroundColor(colorId);
        }
        return mSnackbar;
    }

    public void info() {
        setSnackBarBackColor(color_info);
        show();
    }

    public void info(String actionText, View.OnClickListener listener) {
        setSnackBarBackColor(color_info);
        show(actionText, listener);
    }

    public void warning() {
        setSnackBarBackColor(color_warning);
        show();
    }

    public void warning(String actionText, View.OnClickListener listener) {
        setSnackBarBackColor(color_warning);
        show(actionText, listener);
    }

    public void danger() {
        setSnackBarBackColor(color_danger);
        show();
    }

    public void danger(String actionText, View.OnClickListener listener) {
        setSnackBarBackColor(color_danger);
        show(actionText, listener);
    }

    public void success() {
        setSnackBarBackColor(color_success);
        show();
    }

    public void success(String actionText, View.OnClickListener listener) {
        setSnackBarBackColor(color_success);
        show(actionText, listener);
    }

    public void show() {
        mSnackbar.show();
    }

    public void show(String actionText, View.OnClickListener listener) {
        mSnackbar.setActionTextColor(color_action);
        mSnackbar.setAction(actionText, listener).show();
    }
}
