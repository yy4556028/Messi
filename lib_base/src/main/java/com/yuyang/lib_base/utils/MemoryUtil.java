package com.yuyang.lib_base.utils;

import android.app.ActivityManager;
import android.content.Context;

import com.yuyang.lib_base.BaseApp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MemoryUtil {

    private void getMemoryInfo() {
        ActivityManager activityManager = (ActivityManager) BaseApp.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
//        LogUtil.d("totalMem=" + memoryInfo.totalMem + ",availMem=" + memoryInfo.availMem);
        if (!memoryInfo.lowMemory) {
            // 运行在低内存环境
        }
    }

    public static long getPhoneMemoryTotal() {
        String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryTotalLine = br.readLine();
            String subMemoryTotalLine = memoryTotalLine.substring(memoryTotalLine.indexOf("MemTotal:"));
            br.close();
            long totalMemorySize_kb = Integer.parseInt(subMemoryTotalLine.replaceAll("\\D+", ""));
            return totalMemorySize_kb;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getPhoneMemoryFree() {
        String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryFreeLine = br.readLine();
            String subMemoryFreeLine = memoryFreeLine.substring(memoryFreeLine.indexOf("MemFree:"));
            br.close();
            long freeMemorySize_kb = Integer.parseInt(subMemoryFreeLine.replaceAll("\\D+", ""));
            return freeMemorySize_kb;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取当前可用内存，返回数据以字节为单位。
     *
     * @return 当前可用内存。  是/proc/meminfo中的MemFree加上一些cache和buffer
     */
    public static long getPhoneAvailableMemory() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ((ActivityManager) BaseApp.getInstance().getSystemService(Context.ACTIVITY_SERVICE)).getMemoryInfo(mi);
        return mi.availMem;
    }
}