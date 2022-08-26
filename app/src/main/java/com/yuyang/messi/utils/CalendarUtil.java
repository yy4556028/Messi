package com.yuyang.messi.utils;

import java.util.Calendar;

/**
 * Created by Yamap on 2016/12/16.
 */

public class CalendarUtil {

    /**
     * @param cal1
     * @param cal2
     * @return 返回 第二个时间 比 第一个时间 多出的月数
     */
    public static int getMonthDiff(Calendar cal1, Calendar cal2) {
        int yearDiff = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR);
        int monthDiff = cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH);
        return yearDiff * 12 + monthDiff;
    }

    /**
     * @param calendar1
     * @param calendar2
     * @return 返回 第二个时间 比 第一个时间 多出的周数
     */
    public static int getWeekDiff(Calendar calendar1, Calendar calendar2) {
        Calendar cal1 = (Calendar) calendar1.clone();
        Calendar cal2 = (Calendar) calendar2.clone();
        cal1.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        cal1.set(Calendar.HOUR, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        cal2.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        cal2.set(Calendar.HOUR, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);
        return (int)((cal2.getTimeInMillis() - cal1.getTimeInMillis())/(1000 * 3600 * 24 * 7));
    }

    /**
     * @param calendar1
     * @param calendar2
     * @return 返回 第二个时间 比 第一个时间 多出的天数
     */
    public static int getDayDiff(Calendar calendar1, Calendar calendar2) {
        Calendar cal1 = (Calendar) calendar1.clone();
        Calendar cal2 = (Calendar) calendar2.clone();
        cal1.set(Calendar.HOUR, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        cal2.set(Calendar.HOUR, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);
        return (int)((cal2.getTimeInMillis() - cal1.getTimeInMillis())/(1000 * 3600 * 24));
    }

    public static boolean isSameDay(long time1, long time2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(time1);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(time2);
        return (calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR))
                && (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR));
    }
}
