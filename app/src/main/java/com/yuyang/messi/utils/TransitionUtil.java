package com.yuyang.messi.utils;

import android.app.Activity;
import android.os.Build;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Visibility;

import androidx.annotation.RequiresApi;

import static android.transition.Visibility.MODE_IN;
import static android.transition.Visibility.MODE_OUT;
import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.END;
import static android.view.Gravity.START;
import static android.view.Gravity.TOP;

/**
 * Created by Yamap on 2017/2/26.
 */

public class TransitionUtil {

    public static Fade fadeIn = new Fade(MODE_IN);
    public static Fade fadeOut = new Fade(MODE_OUT);
    public static Slide slideStart = new Slide(START);
    public static Slide slideEnd = new Slide(END);
    public static Slide slideTop = new Slide(TOP);
    public static Slide slideBottom = new Slide(BOTTOM);
    public static Explode explode = new Explode();

    /**
     * 当A start B 时，使A中的View退出场景的transition    在A中设置
     * @param activity activity A
     * @param visibility visibility
     */
    public static void setExitTransition(Activity activity, Visibility visibility, int duration) {
        visibility.setMode(MODE_OUT);
        visibility.setDuration(duration);
        activity.getWindow().setExitTransition(visibility);
    }

    /**
     * 当A startB时，使B中的View进入场景的transition    在B中设置
     * @param activity activity B
     * @param visibility visibility
     */
    public static void setEnterTransition(Activity activity, Visibility visibility, int duration) {
        visibility.setMode(MODE_IN);
        visibility.setDuration(duration);
        activity.getWindow().setEnterTransition(visibility);
    }

    /**
     * 当B 返回A时，使B中的View退出场景的transition  在B中设置
     * @param activity activity B
     * @param visibility visibility
     */
    public static void setReturnTransition(Activity activity, Visibility visibility, int duration) {
        visibility.setMode(MODE_OUT);
        visibility.setDuration(duration);
        activity.getWindow().setReturnTransition(visibility);
    }

    /**
     * 当B 返回A时，使A中的View进入场景的transition   在A中设置
     * @param activity activity A
     * @param visibility visibility
     */
    public static void setReenterTransition(Activity activity, Visibility visibility, int duration) {
        visibility.setMode(MODE_IN);
        visibility.setDuration(duration);
        activity.getWindow().setReenterTransition(visibility);
    }
}
