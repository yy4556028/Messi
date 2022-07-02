package com.yuyang.messi.utils;

import android.content.res.AssetManager;

import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.MessiApp;
import com.yuyang.messi.bean.AddrPicker.ProvinceBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
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

    /**
     * read file content
     *
     * @param assetPath the asset path
     * @return String string
     */
    public static String readText(String assetPath) {
        try {
            return toString(MessiApp.getInstance().getAssets().open(assetPath));
        } catch (Exception e) {
            return "";
        }
    }

    public static List<ProvinceBean> initProvinceData() {
        List<ProvinceBean> provinceList = null;
        AssetManager asset = MessiApp.getInstance().getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            // 获取解析出来的数据
            provinceList = handler.getProvinceList();

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            return provinceList;
        }
    }

    public static String toString(InputStream is) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();
        } catch (IOException e) {
        }
        return sb.toString();
    }

    /**
     * 将assets中的文件拷贝到app的缓存目录，并且返回拷贝之后文件的绝对路径
     */
    public static String copyAssetToCache(String fileName) {

        File cacheDir = MessiApp.getInstance().getFilesDir();//app的缓存目录
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();//如果没有缓存目录，就创建
        }
        File outPath = new File(cacheDir, fileName);//创建输出的文件位置
        if (outPath.exists()) {
            outPath.delete();//如果该文件已经存在，就删掉
        }
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            boolean res = outPath.createNewFile();//创建文件，如果创建成功，就返回true
            if (res) {
                is = MessiApp.getInstance().getAssets().open(fileName);//拿到main/assets目录的输入流，用于读取字节
                fos = new FileOutputStream(outPath);//读取出来的字节最终写到outPath
                byte[] buf = new byte[is.available()];//缓存区
                int byteCount;
                while ((byteCount = is.read(buf)) != -1) {//循环读取
                    fos.write(buf, 0, byteCount);
                }
                ToastUtil.showToast("加载成功");
                return outPath.getAbsolutePath();
            } else {
                ToastUtil.showToast("加载失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fos) {
                    fos.flush();
                    fos.close();
                }
                if (null != is)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}
