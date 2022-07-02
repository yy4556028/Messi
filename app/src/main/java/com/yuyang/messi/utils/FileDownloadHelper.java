package com.yuyang.messi.utils;

import android.os.Environment;

import com.yuyang.lib_base.utils.StorageUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 简单的文件下载demo，没有断点，没有管理，just download
 * Created by Yamap on 2017/3/17.
 */

public class FileDownloadHelper {

    public static void downloadFile() {
        String urlStr = "http://172.17.54.91:8080/download/1.mp3";
        String path = StorageUtil.getPublicPath(Environment.DIRECTORY_DOWNLOADS + "/file");
        OutputStream output = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            File file = new File(path);
            InputStream input = conn.getInputStream();
            if (file.exists()) {
                return;
            }

            file.getParentFile().mkdirs();
            file.createNewFile();//新建文件
            output = new FileOutputStream(file);
            //读取大文件
            byte[] buffer = new byte[4 * 1024];
            int fileLength = conn.getContentLength();
            int downloadLength = 0;
            while (downloadLength < fileLength) {
                output.write(buffer);
            }
            output.flush();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
