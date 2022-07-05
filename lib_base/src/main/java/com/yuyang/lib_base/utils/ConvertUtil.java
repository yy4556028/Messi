package com.yuyang.lib_base.utils;

import java.text.DecimalFormat;

public class ConvertUtil {

    private static final DecimalFormat format = new DecimalFormat("00");

    public static String convertNumber(int number) {
        return format.format(number);
    }

    public static String convertTimeMs(long timeMs) {
        return convertTimeMs(timeMs / 1000);
    }

    public static String convertTimeSecond(long timeSecond) {

        if (timeSecond <= 0) {
            return "00:00";
        }

        int second = (int) (timeSecond % 60);
        long minute = timeSecond / 60;
        long hour = 0;
        if (minute >= 60) {
            hour = minute / 60;
            minute = minute % 60;
        }

        if (hour > 0) {
            return String.format("%s:%s:%s", format.format(hour), format.format(minute), format.format(second));
        } else {
            return String.format("%s:%s", format.format(minute), format.format(second));
        }
    }
}
