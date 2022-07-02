package com.yuyang.messi.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;

import java.util.Locale;

public class ResourcesUtil {

    /**
     * 应用内语言切换
     * @param context
     * @param language
     */
    public static void changeSystemLanguage(Context context, String language) {
        if (context == null || TextUtils.isEmpty(language)) {
            return;
        }

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();

        if (Locale.SIMPLIFIED_CHINESE.getLanguage().equals(language)) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else {
            config.locale = new Locale(language);
        }

        resources.updateConfiguration(config, null);
    }
}
