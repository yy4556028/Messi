package com.yuyang.lib_base.utils;

import android.content.res.Resources;

/**
 * 像素转换工具
 */
public class PixelUtils {

    public static int dp2px(float dpValue) {
        return (int) (dpValue * Resources.getSystem().getDisplayMetrics().density + 0.5f);
    }

    public static int px2dp(float pxValue) {
        return (int) (pxValue / Resources.getSystem().getDisplayMetrics().density + 0.5f);
    }

    public static int sp2px(float spValue) {
        return (int) (spValue * Resources.getSystem().getDisplayMetrics().scaledDensity + 0.5f);
    }

    public static int px2sp(float pxValue) {
        return (int) (pxValue / Resources.getSystem().getDisplayMetrics().scaledDensity + 0.5f);
    }
}
