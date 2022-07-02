package com.yuyang.messi.utils;

import android.text.TextUtils;

import com.yuyang.messi.MessiApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GifCacheUtil {

    public static String RIFFSY_FILE_PATH = MessiApp.getInstance().getFilesDir().getPath() + "/riffsy";
    public static String SUFFIX = ".gif";
    private static final int MAX_SIZE_LIMIT = 10 * 1024 * 1024;

    private final LinkedHashMap<String, File> map;

    private int size;

    private static GifCacheUtil gifCache;

    public static GifCacheUtil getInstance() {
        if (gifCache == null) {
            gifCache = new GifCacheUtil();
        }
        return gifCache;
    }

    private GifCacheUtil() {
        this.map = new LinkedHashMap<>(0, 0.75f, true);
        init();
    }

    private void init() {
        File file = new File(RIFFSY_FILE_PATH);
        if (file.exists() && file.isDirectory()) {

            File[] childFiles = file.listFiles();
            if (childFiles.length == 0)
                return;

            List<File> fileList = Arrays.asList(childFiles);
            Collections.sort(fileList, new ComparatorByTime());

            for (File f : fileList) {
                if (f.getName().endsWith(SUFFIX)) {
                    map.put(f.getName(), f);
                    size += f.length();
                } else {
                    f.delete();
                }
            }
            trimToSize(MAX_SIZE_LIMIT);
        } else {
            file.mkdirs();
        }
    }

    public File get(String uri) {
        String key = getCacheDecodeString(uri);
        if (TextUtils.isEmpty(key)) {
            throw new NullPointerException("key == null");
        }

        File mapValue;
        synchronized (this) {
            mapValue = map.get(key);
            if (mapValue != null) {
                if (mapValue.exists()) {
                    map.remove(mapValue);
                    map.put(key, mapValue);
                    mapValue.setLastModified(System.currentTimeMillis());
                    return mapValue;
                } else {
                    map.remove(key);
                }
            }
        }

        return null;
    }

    public void set(String uri, File file) {
        String key = getCacheDecodeString(uri);
        if (TextUtils.isEmpty(key) || !file.exists()) {
            throw new NullPointerException("key == null || file == null");
        }

        File previous;
        synchronized (this) {
            size += file.length();
            previous = map.put(key, file);
            if (previous != null) {
                size -= previous.length();
            }
        }

        trimToSize(MAX_SIZE_LIMIT);
    }

    private void trimToSize(final int maxSize) {
        while (true) {
            String key;
            File value;
            synchronized (this) {
                if (size < 0 || (map.isEmpty() && size != 0)) {
                    throw new IllegalStateException(
                            getClass().getName() + ".sizeOf() is reporting inconsistent results!");
                }

                if (size <= maxSize || map.isEmpty()) {
                    break;
                }

                Map.Entry<String, File> toEvict = map.entrySet().iterator().next();
                key = toEvict.getKey();
                value = toEvict.getValue();
                map.remove(key);
                size -= value.length();
                value.delete();
            }
        }
    }

    /**
     * Clear the cache.
     */
    public final void evictAll() {
        trimToSize(-1); // -1 will evict 0-sized elements
    }

    public final synchronized void clear() {
        evictAll();
    }

    public final synchronized void clearKeyUri(String uri) {
        String key = getCacheDecodeString(uri);
        if (TextUtils.isEmpty(key)) {
            return;
        }

        File tempFile = new File(RIFFSY_FILE_PATH, key.substring(0, key.indexOf(SUFFIX)));

        if (tempFile.exists()) {
            tempFile.delete();
        }

        File file = map.remove(key);
        if (file != null) {
            if (file.exists()) {
                size -= file.length();
                file.delete();
            }
        }
        trimToSize(MAX_SIZE_LIMIT);
    }

    public synchronized File getValueByUri(String uri) {
        String key = getCacheDecodeString(uri);
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        return map.get(key);
    }

    public static String getCacheDecodeString(String url) {
        if (!TextUtils.isEmpty(url)) {
            return url.replaceAll("[.:/,%?&=]", "+").replaceAll("[+]+", "+") + SUFFIX;
        }
        return null;
    }

    private class ComparatorByTime implements Comparator<File> {
        @Override
        public int compare(File lhs, File rhs) {
            return (int) (lhs.lastModified() - (rhs.lastModified()));
        }
    }

    //
    public void downloadGif(String urlString, GifDownloadCallback callback) {

        String fileName = getCacheDecodeString(urlString);

        File dirFile = new File(RIFFSY_FILE_PATH);

        if (!dirFile.exists() || !dirFile.isDirectory()) {
            dirFile.mkdirs();
        }

        File file = new File(RIFFSY_FILE_PATH, fileName.substring(0, fileName.indexOf(SUFFIX)));

        try {
            if (!file.exists()) {
                file.createNewFile();
            } else {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (file.exists()) {
                file.delete();
            }
            if (callback != null) {
                callback.onFailed();
            }
            return;
        }

        URL url = null;

        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            if (file.exists()) {
                file.delete();
            }
            if (callback != null) {
                callback.onFailed();
            }
            return;
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            if (file.exists()) {
                file.delete();
            }
            if (callback != null) {
                callback.onFailed();
            }
            return;
        }

        //set up some things on the connection
        try {
            urlConnection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
            if (file.exists()) {
                file.delete();
            }
            if (callback != null) {
                callback.onFailed();
            }
            return;
        }
        urlConnection.setDoOutput(false);

        try {
            urlConnection.connect();
        } catch (Exception e) {
            e.printStackTrace();
            if (file.exists()) {
                file.delete();
            }
            if (callback != null) {
                callback.onFailed();
            }
            return;
        }

        int fileLength = urlConnection.getContentLength();

        //this will be used to write the downloaded data into the file we created
        FileOutputStream fileOutput;
        try {
            fileOutput = new FileOutputStream(file);

            InputStream inputStream;
            try {
                //this will be used in reading the data from the internet
                inputStream = urlConnection.getInputStream();
                //used to store a temporary size of the buffer
                byte[] buffer = new byte[1024];
                int bufferLength = 0;
                int total = 0;
                //now, read through the input buffer and write the contents to the file
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    total += bufferLength;
                    if (callback != null) {
                        callback.onProgress((total * 100) / fileLength);
                    }
                    //add the data in the buffer to the file in the file output stream (the file on the sd card
                    fileOutput.write(buffer, 0, bufferLength);
                }
                fileOutput.close();

                file.renameTo(new File(RIFFSY_FILE_PATH, fileName));

                set(urlString, new File(RIFFSY_FILE_PATH, fileName));
                if (callback != null) {
                    callback.onSuccess();
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (file.exists()) {
                    file.delete();
                }
                if (callback != null) {
                    callback.onFailed();
                }
                return;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            file.delete();
            if (callback != null) {
                callback.onFailed();
            }
            return;
        }
    }

    public interface GifDownloadCallback {
        public void onSuccess();

        public void onFailed();

        public void onProgress(int degree);
    }
}
