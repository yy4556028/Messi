package com.yuyang.lib_base.utils;

import java.text.DecimalFormat;

public class UnitUtil {

    /**
     * 带宽转换
     */
    public static String convertBandwidth(int speedByte) {
        int GB = 1024 * 1024 * 1024;//定义GB的计算常量
        int MB = 1024 * 1024;//定义MB的计算常量
        int KB = 1024;//定义KB的计算常量
        DecimalFormat df = new DecimalFormat("0.##");
        String resultStr = "";
        if (speedByte / GB >= 1) {
            //如果当前Byte的值大于等于1GB
            resultStr = df.format(speedByte / (float) GB) + "GB/S";
        } else if (speedByte / MB >= 1) {
            //如果当前Byte的值大于等于1MB
            resultStr = df.format(speedByte / (float) MB) + "MB/S";
        } else if (speedByte / KB >= 1) {
            //如果当前Byte的值大于等于1KB
            resultStr = df.format(speedByte / (float) KB) + "KB/S";
        } else {
            resultStr = speedByte + " B/S";
        }
        return resultStr;
    }
}
