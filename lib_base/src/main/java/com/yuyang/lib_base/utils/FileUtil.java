package com.yuyang.lib_base.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.yuyang.lib_base.BaseApp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtil {

    private FileUtil() {
    }

    public static String readFile(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(isr);

            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            fis.close();
            isr.close();
            reader.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static String readFileV29(String relativePath) {
        Uri uri = queryFileUri(relativePath);
        return readUri(uri);
    }

    public static void writeFile(String filePath, String content) {
        if (!new File(filePath).getParentFile().exists()) {
            new File(filePath).getParentFile().mkdirs();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath, false);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            BufferedWriter writer = new BufferedWriter(osw);
            writer.write(content);
            writer.close();
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static boolean writeFileV29(String filePath, String content) {
        Uri uri = queryFileUri(filePath);
        if (uri == null) {
            int lastSeparatorIndex = filePath.lastIndexOf(File.separator);
            String relativePath = filePath.substring(0, lastSeparatorIndex);
            String fileName = filePath.substring(lastSeparatorIndex + 1);

            ContentResolver resolver = BaseApp.getInstance().getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
            //设置文件类型（有哪些类型网上很容易查到，如果不设置的话，就是默认没有扩展名的文件）
            values.put(MediaStore.Files.FileColumns.MIME_TYPE, "text/plain");
            //这个方法只可在Android10的手机上执行，设置路径
            values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + relativePath);
            // 写入文件
            uri = resolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL), values);
        }
        return writeUri(uri, content);
    }

    public static String readUri(Uri uri) {

        // io写入
        InputStream is = null;
        try {
            is = BaseApp.getInstance().getContentResolver().openInputStream(uri);
            if (is == null) {
                return null;
            }
            byte[] buff = new byte[1024];
            int hasRead;
            StringBuilder sb = new StringBuilder();
            while ((hasRead = is.read(buff)) > 0) {
                sb.append(new String(buff, 0, hasRead));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    public static boolean writeUri(Uri uri, String content) {
        // io写入
        OutputStream os = null;
        try {
            os = BaseApp.getInstance().getContentResolver().openOutputStream(uri, "rwt");
            if (os == null) {
                return false;
            }
            byte[] buffer = content.getBytes();
            os.write(buffer, 0, buffer.length);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    public static Uri saveImageV29(Bitmap bitmap, String relativeImagePath) {
        int lastSeparatorIndex = relativeImagePath.lastIndexOf(File.separator);
        String relativePath = relativeImagePath.substring(0, lastSeparatorIndex);
        String imageName = relativeImagePath.substring(lastSeparatorIndex + 1);

        String imageType;
        if (!TextUtils.isEmpty(imageName) && imageName.toLowerCase().endsWith("png")) {
            imageType = "PNG";
        } else {
            imageType = "JPG";
        }

        ContentResolver resolver = BaseApp.getInstance().getContentResolver();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);//设置文件名
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, imageName);//设置文件描述，这里以文件名代替
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/" + imageType);//设置文件类型
        //兼容Android Q和以下版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //android Q中不再使用DATA字段，而用RELATIVE_PATH代替
            //RELATIVE_PATH是相对路径不是绝对路径
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + relativePath);
//            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + relativePath);
        } else {
            contentValues.put(MediaStore.Images.Media.DATA,
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + relativeImagePath);
        }

        //执行insert操作，向系统文件夹中添加文件
        //EXTERNAL_CONTENT_URI代表外部存储器，该值不变
        Uri insertUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        //若生成了uri，则表示该文件添加成功
        //使用流将内容写入该uri中即可
        OutputStream outputStream = null;
        try {
            outputStream = resolver.openOutputStream(insertUri);
            if (outputStream != null) {
                if ("PNG".equalsIgnoreCase(imageType)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                }
            }
//            ToastUtil.showToast(insertUri.getPath());
            return insertUri;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static Uri queryFileUri(String filePath) {
        int lastSeparatorIndex = filePath.lastIndexOf(File.separator);
        String relativePath = filePath.substring(0, lastSeparatorIndex);
        String fileName = filePath.substring(lastSeparatorIndex + 1);

        ContentResolver resolver = BaseApp.getInstance().getContentResolver();

        Cursor cursor = resolver.query(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
                null,
                MediaStore.Files.FileColumns.DATA + " like ?",
                new String[]{"%" + filePath},
                MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC");
//        Cursor cursor = resolver.query(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
//                null,
//                MediaStore.Files.FileColumns.RELATIVE_PATH + " like ? AND " + MediaStore.Files.FileColumns.DISPLAY_NAME + " = ?",
//                new String[]{"%" + relativePath, fileName},
//                MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC");

        Uri uri = null;

        int count = cursor.getCount();

        if (cursor.moveToFirst()) {
//            do {
//
//            } while (cursor.moveToNext());

            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
            int size = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE));
            uri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
                    .buildUpon()
                    .appendPath(String.valueOf(id)).build();
//            ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, cursor.getLong(0));
        }
        cursor.close();
        return uri;
    }

    public static void deleteUri(Uri uri) {
        if (uri != null) {
            BaseApp.getInstance().getContentResolver().delete(uri, null, null);
        }
    }

    public static void appendFile(File file, String content) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try {
            FileWriter fw = new FileWriter(file, true);
            fw.write(content);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean copyFile(File srcFile, File targetFile) {

        try {
            int byteread = 0;

            if (srcFile.exists()) {

                if (!targetFile.exists() || !targetFile.isFile()) {
                    targetFile.createNewFile();
                }

                InputStream is = new FileInputStream(srcFile);
                FileOutputStream fos = new FileOutputStream(targetFile);
                byte[] buffer = new byte[1024];

                while ((byteread = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteread);
                }
                is.close();

                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static boolean cutFile(File srcFile, File targetFile) {

        boolean isCopy = copyFile(srcFile, targetFile);

        if (isCopy) {
            srcFile.delete();
            return true;
        } else {
            return false;
        }
    }

    /**
     * force delete a file,thread safe.
     *
     * @param file file
     * @return del result
     */
    public static boolean forceDeleteFile(File file) {
        boolean result = false;
        int tryCount = 0;
        while (!result && tryCount++ < 10) {
            result = file.delete();
            if (!result) {
                try {
                    synchronized (FileUtil.class) {
                        file.wait(200);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static boolean deleteDir(File dir, boolean deleteRoot) {
        long size = dir.length();
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]), true);
                if (!success) {
                    return false;
                }
            }
        }
        return !deleteRoot || dir.delete();
    }

    public static String getSuffix(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index == -1) return "";
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static void unzip(InputStream is, String dir) throws IOException {
        File dest = new File(dir);
        if (!dest.exists()) {
            dest.mkdirs();
        }

        if (!dest.isDirectory())
            throw new IOException("Invalid Unzip destination " + dest);
        if (null == is) {
            throw new IOException("InputStream is null");
        }

        ZipInputStream zip = new ZipInputStream(is);

        ZipEntry ze;
        while ((ze = zip.getNextEntry()) != null) {
            final String path = dest.getAbsolutePath()
                    + File.separator + ze.getName();

            String zeName = ze.getName();
            char cTail = zeName.charAt(zeName.length() - 1);
            if (cTail == File.separatorChar) {
                File file = new File(path);
                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        throw new IOException("Unable to create folder " + file);
                    }
                }
                continue;
            }

            FileOutputStream fout = new FileOutputStream(path);
            byte[] bytes = new byte[1024];
            int c;
            while ((c = zip.read(bytes)) != -1) {
                fout.write(bytes, 0, c);
            }
            zip.closeEntry();
            fout.close();
        }
    }

    public static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    public static String formatSize(long finishedSize, long totalSize) {
        StringBuilder sb = new StringBuilder(50);

        float finished = ((float) finishedSize) / 1024 / 1024;
        if (finished < 1) {
            sb.append(String.format("%1$.2f K / ", ((float) finishedSize) / 1024));
        } else {
            sb.append((String.format("%1$.2f M / ", finished)));
        }

        float total = ((float) totalSize) / 1024 / 1024;
        if (total < 1) {
            sb.append(String.format("%1$.2f K ", ((float) totalSize) / 1024));
        } else {
            sb.append(String.format("%1$.2f M ", total));
        }
        return sb.toString();
    }

    public static String getDownloadPerSize(long finished, long total) {
        DecimalFormat DF = new DecimalFormat("0.00");
        return DF.format((float) finished / (1024 * 1024)) + "M/" + DF.format((float) total / (1024 * 1024)) + "M";
    }
}
