package com.example.lpr.utils;

import android.content.Context;

import com.yuyang.lib_base.utils.StorageUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DeepAssetUtil {


    public static final String ApplicationDir = "lpr";
    public static final String CASCADE_FILENAME = "cascade.xml";
    public static final String FINEMAPPING_PROTOTXT = "HorizonalFinemapping.prototxt";
    public static final String FINEMAPPING_CAFFEMODEL = "HorizonalFinemapping.caffemodel";
    public static final String SEGMENTATION_PROTOTXT = "Segmentation.prototxt";
    public static final String SEGMENTATION_CAFFEMODEL = "Segmentation.caffemodel";
    public static final String RECOGNIZATION_PROTOTXT = "CharacterRecognization.prototxt";
    public static final String RECOGNIZATION_CAFFEMODEL = "CharacterRecognization.caffemodel";
    public static final String FREE_INCEPTION_PROTOTXT = "SegmenationFree-Inception.prototxt";
    public static final String FREE_INCEPTION_CAFFEMODEL = "SegmenationFree-Inception.caffemodel";

    public static final String SDCARD_DIR = StorageUtil.getExternalFilesDir(ApplicationDir).getAbsolutePath(); //解压文件存放位置


    private static void CopyAssets(Context context, String assetDir, String dir) {
        String[] files;
        try {
            // 获得Assets一共有几多文件
            files = context.getAssets().list(assetDir);
        } catch (IOException e1) {
            return;
        }
        File mWorkingPath = new File(dir);
        // 如果文件路径不存在
        if (!mWorkingPath.exists()) {
            // 创建文件夹
            if (!mWorkingPath.mkdirs()) {
                // 文件夹创建不成功时调用
            }
        }

        for (String file : files) {
            try {
                // 根据路径判断是文件夹还是文件
                if (!file.contains(".")) {
                    if (0 == assetDir.length()) {
                        CopyAssets(context, file, dir + file + "/");
                    } else {
                        CopyAssets(context, assetDir + "/" + file, dir + "/" + file + "/");
                    }
                    continue;
                }
                File outFile = new File(mWorkingPath, file);
                if (outFile.exists()) {
                    continue;
                }
                InputStream in;
                if (0 != assetDir.length()) {
                    in = context.getAssets().open(assetDir + "/" + file);
                } else {
                    in = context.getAssets().open(file);
                }

                OutputStream out = new FileOutputStream(outFile);
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void copyFilesFromAssets(Context context) {
        DeepAssetUtil.CopyAssets(context, ApplicationDir, SDCARD_DIR);
    }

    //初始化识别资源
    public static long initRecognizer(Context context) {
        String cascade_filename = SDCARD_DIR + File.separator + CASCADE_FILENAME;
        String finemapping_prototxt = SDCARD_DIR + File.separator + FINEMAPPING_PROTOTXT;
        String finemapping_caffemodel = SDCARD_DIR + File.separator + FINEMAPPING_CAFFEMODEL;
        String segmentation_prototxt = SDCARD_DIR + File.separator + SEGMENTATION_PROTOTXT;
        String segmentation_caffemodel = SDCARD_DIR + File.separator + SEGMENTATION_CAFFEMODEL;
        String character_prototxt = SDCARD_DIR + File.separator + RECOGNIZATION_PROTOTXT;
        String character_caffemodel = SDCARD_DIR + File.separator + RECOGNIZATION_CAFFEMODEL;
        String segmentation_free_prototxt = SDCARD_DIR + File.separator + FREE_INCEPTION_PROTOTXT;
        String segmentation_free_caffemodel = SDCARD_DIR + File.separator + FREE_INCEPTION_CAFFEMODEL;
        copyFilesFromAssets(context);
        //调用JNI 加载资源函数
        return PlateRecognition.InitPlateRecognizer(
                cascade_filename,
                finemapping_prototxt, finemapping_caffemodel,
                segmentation_prototxt, segmentation_caffemodel,
                character_prototxt, character_caffemodel,
                segmentation_free_prototxt, segmentation_free_caffemodel);
    }
}
