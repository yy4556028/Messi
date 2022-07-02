package com.yuyang.messi.helper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.yuyang.aidl_audioplayer.AudioBean;
import com.yuyang.lib_base.utils.StorageUtil;
import com.yuyang.messi.MessiApp;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class AudioHelper {

    // 存放 audio 列表
    public static ArrayList<AudioBean> audioList = new ArrayList<>();

    private static int filterLength = 0;

    public static void initAudioList(int filterLength) {
        AudioHelper.filterLength = filterLength;
        // 获取歌曲列表
        audioList.clear();
        audioList.addAll(queryMusic());
    }

    /**
     * 获取音乐存放目录
     */
    public static String getMusicDir() {
        return StorageUtil.getExternalFilesDir("music").getAbsolutePath();
    }

    /**
     * 获取歌词存放目录
     */
    public static String getLrcDir() {
        return StorageUtil.getExternalFilesDir("lrc").getAbsolutePath();
    }

    /**
     * 创建文件夹
     *
     * @param dir
     * @return
     */
    public static String mkdir(String dir) {
        File f = new File(dir);
        if (!f.exists()) {
            for (int i = 0; i < 5; i++) {
                if (f.mkdirs()) return dir;
            }
            return null;
        }

        return dir;
    }

    /**
     * 获取目录下的歌曲
     */
    public static ArrayList<AudioBean> queryMusic() {
        ArrayList<AudioBean> results = new ArrayList<>();
        Cursor cursor = MessiApp.getInstance().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                null,
                null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor == null) return results;

        // id title singer data time image
        AudioBean audioBean;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // 如果不是音乐
            String isMusic = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC));
            if (isMusic != null && isMusic.equals("")) continue;

            int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

            if (duration < filterLength) continue;

            String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));

            // 根据音乐名称和艺术家来判断是否重复包含了
            for (AudioBean audio : audioList) {
                if (title.equals(audio.getTitle()) && artist.equals(audio.getArtist())) {
                    continue;
                }
            }

            audioBean = new AudioBean();
            audioBean.setAudioId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
            audioBean.setTitle(title);
            audioBean.setArtist(artist);
            audioBean.setLength(duration);
            audioBean.setImage(getAlbumImage(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))));
            results.add(audioBean);
        }

        cursor.close();
        return results;
    }

    /**
     * 根据歌曲id获取图片
     *
     * @param albumId
     * @return
     */
    private static String getAlbumImage(int albumId) {
        String result = null;
        Cursor cursor = null;
        try {
            cursor = MessiApp.getInstance().getContentResolver().query(
                    Uri.parse("content://media/external/audio/albums/"
                            + albumId), new String[]{"album_art"}, null,
                    null, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); ) {
                result = cursor.getString(0);
                break;
            }
        } catch (Exception e) {
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return result;
    }

    public static String convertTime(int time) {

        if (time <= 0) {
            return "00:00";
        }

        int minute = (time / 1000) / 60;
        int second = (time / 1000) % 60;

        String m = ("00" + minute).substring(("00" + minute).length() - 2);

        String s = ("00" + second).substring(("00" + second).length() - 2);

        return m + ":" + s;
    }

    public static String ARG_DIR = "air_dir";
    public static String ARG_DURATION = "air_duration";

    public static void loadAudio(AppCompatActivity activity, Bundle bundle, AudioResultCallback resultCallback) {
        if (bundle == null) bundle = new Bundle();
        LoaderManager.getInstance(activity).initLoader(1, bundle, new AudioLoaderCallbacks(activity, resultCallback));
    }

    private static class AudioLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        private final WeakReference<Context> context;
        private final AudioResultCallback resultCallback;

        private AudioLoaderCallbacks(Context context, AudioResultCallback resultCallback) {
            this.context = new WeakReference<>(context);
            this.resultCallback = resultCallback;
        }

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
            return new AudioCursorLoader(context.get(), bundle.getString(ARG_DIR), bundle.getInt(ARG_DURATION, -1));
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

            if (cursor == null) return;

            ArrayList<AudioBean> beanList = new ArrayList<>();

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                // 如果不是音乐
                String isMusic = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC));
                if (!TextUtils.equals(isMusic, "1")) {
//                    ToastUtil.showToast("111222333");
                    continue;
                }

//                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

//                if (duration < filterLength) continue;

                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));

                // 根据音乐名称和艺术家来判断是否重复包含了
//                for (AudioBean audioBean : beanList) {
//                    if (title.equals(audioBean.getTitle()) && artist.equals(audioBean.getArtist())) {
//                        continue;
//                    }
//                }

                AudioBean audioBean = new AudioBean();
                audioBean.setAudioId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
                audioBean.setTitle(title);
                audioBean.setArtist(artist);
                audioBean.setLength(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                audioBean.setImage(getAlbumImage(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))));
                beanList.add(audioBean);
            }

            if (resultCallback != null) {
                resultCallback.onResultCallback(beanList);
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        }
    }

    public interface AudioResultCallback {
        void onResultCallback(ArrayList<AudioBean> beanList);
    }

    private static class AudioCursorLoader extends CursorLoader {

        private AudioCursorLoader(Context context, String dir, int duration) {
            super(context);

            setUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            setSortOrder(MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

            if (TextUtils.isEmpty(dir)) {
                setSelection(MediaStore.Audio.Media.DURATION + " > ?");
                setSelectionArgs(new String[]{String.valueOf(duration)});
            } else {
                setSelection(MediaStore.Audio.Media.DURATION + " > ? " + "and " + MediaStore.Audio.Media.DATA + " like ?");
                setSelectionArgs(new String[]{String.valueOf(duration), dir + "%"});
            }
        }
    }
}
