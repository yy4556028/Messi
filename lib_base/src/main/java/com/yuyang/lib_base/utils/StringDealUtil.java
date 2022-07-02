package com.yuyang.lib_base.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import androidx.annotation.ColorInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringDealUtil {

    /**
     * 高亮关键字
     */
    public static SpannableString highlightKeyword(@ColorInt int color, CharSequence source, String... keywords) {
        if (source == null) {
            return null;
        }
        SpannableString spannable = new SpannableString(source);
        if (!TextUtils.isEmpty(source) && keywords != null) {
            for (String keyword : keywords) {
                Matcher matcher = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE).matcher(source);
                while (matcher.find()) {
                    spannable.setSpan(new ForegroundColorSpan(color), matcher.start(), matcher.end(), Spannable
                            .SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return spannable;
    }

    /**
     * 高亮关键字
     */
    public static SpannableString highlightKeywordFromStart(@ColorInt int color, CharSequence source, String keyword) {
        if (source == null) {
            return null;
        }
        SpannableString spannable = new SpannableString(source);
        if (!TextUtils.isEmpty(source) && !TextUtils.isEmpty(keyword)) {
            Matcher matcher = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE).matcher(source);
            while (matcher.find() && matcher.start() == 0) {
                spannable.setSpan(new ForegroundColorSpan(color), matcher.start(), matcher.end(), Spannable
                    .SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannable;
    }
}
