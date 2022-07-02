package com.yuyang.lib_base.config;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class CommonConstant {

    public static final AppEnvironment environment = AppEnvironment.Development;

    public static DecimalFormat moneyFormat = new DecimalFormat(",##0.00");  // 千分位格式化
    public static DecimalFormat twoDecFormat = new DecimalFormat("0.00");  // 格式化保留两位小数

    public static DecimalFormat percentInstance = (DecimalFormat) NumberFormat.getPercentInstance();//百分数

    static {
        moneyFormat.setRoundingMode(RoundingMode.HALF_UP);
        twoDecFormat.setRoundingMode(RoundingMode.HALF_UP);
        percentInstance.applyPattern("00.0000%");//34.5670%
    }
}
