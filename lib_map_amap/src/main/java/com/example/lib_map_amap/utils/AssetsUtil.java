package com.example.lib_map_amap.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;


import com.example.lib_map_amap.bean.ProvinceBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * 操作安装包中的“assets”目录下的文件
 */
public class AssetsUtil {

    public static String readFromAssets(Context context, String name) {
        try {
            InputStream is = context.getAssets().open(name);
            InputStreamReader reader = new InputStreamReader(is,"UTF-8");
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuffer buffer = new StringBuffer("");
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
                buffer.append("\n");
            }
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
