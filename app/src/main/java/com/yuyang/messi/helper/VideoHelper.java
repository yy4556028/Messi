package com.yuyang.messi.helper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.yuyang.lib_base.BaseApp;
import com.yuyang.messi.MessiApp;
import com.yuyang.messi.bean.VideoBean;

import java.util.ArrayList;
import java.util.List;

public class VideoHelper {

    public static float getVideoRatio(Uri videoUri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(MessiApp.getInstance(), videoUri);
        Bitmap bitmap = retriever.getFrameAtTime();
        if (bitmap != null)
            return (float) bitmap.getWidth() / bitmap.getHeight();
        else
            return 1;
    }

    public static List<Bitmap> getBitmapsFromVideo(Uri videoUri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(MessiApp.getInstance(), videoUri);

        // 取得视频的长度(单位为毫秒)
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        // 取得视频的长度(单位为秒)
        int seconds = Integer.valueOf(time) / 1000;

        List<Bitmap> bitmaps = new ArrayList<>();

        // 得到每一秒时刻的bitmap比如第一秒,第二秒
        for (int i = 1; i <= seconds; i++) {
            bitmaps.add(retriever.getFrameAtTime(i * 1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC));
        }

        return bitmaps;
    }

    private final static String KEY_DIR = "air_dir";
    private final static String KEY_DURATION = "air_duration";
    private final static String KEY_SIZE = "air_size";

    public static void loadVideo(AppCompatActivity activity, String queryDir, long minDuration, long minSize, VideoResultCallback resultCallback) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DIR, queryDir);
        bundle.putLong(KEY_DURATION, minDuration);
        bundle.putLong(KEY_SIZE, minSize);
        LoaderManager.getInstance(activity).initLoader(2, bundle, new VideoLoaderCallbacks(resultCallback));
    }

    public interface VideoResultCallback {
        void onResultCallback(List<VideoBean> beanList);
    }

    private static class VideoLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        private final VideoResultCallback resultCallback;

        public VideoLoaderCallbacks(VideoResultCallback resultCallback) {
            this.resultCallback = resultCallback;
        }

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
            return new VideoCursorLoader(MessiApp.getInstance(),
                    bundle.getString(KEY_DIR),
                    bundle.getLong(KEY_DURATION, -1),
                    bundle.getLong(KEY_SIZE, -1));
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
            if (cursor == null) return;

            new Thread(() -> {
                List<VideoBean> beanList = new ArrayList<>();

                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                VideoBean videoBean;
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    long videoId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                    Uri uri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, String.valueOf(videoId));

                    retriever.setDataSource(MessiApp.getInstance(), uri);
                    Bitmap bm = retriever.getFrameAtTime();
                    if (bm == null) continue;

                    int width = bm.getWidth();
                    int height = bm.getHeight();

                    videoBean = new VideoBean();
                    videoBean.setVideoId(videoId);
                    videoBean.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
                    videoBean.setDisplayName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)));
                    videoBean.setPath(path);
                    videoBean.setLength(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));
                    videoBean.setBitmap(bm);
                    videoBean.setWidth(width);
                    videoBean.setHeight(height);
                    videoBean.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)));
                    beanList.add(videoBean);
                }

                if (resultCallback != null) {
                    BaseApp.getInstance().currentActivity.get().runOnUiThread(() -> resultCallback.onResultCallback(beanList));
                }
            }).start();
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        }
    }

    private static class VideoCursorLoader extends CursorLoader {

        public VideoCursorLoader(@NonNull Context context, String dir, long duration, long fileSize) {
            super(context);
            setUri(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            setProjection(null);
            setSortOrder(MediaStore.Video.Media.DEFAULT_SORT_ORDER);

            if (TextUtils.isEmpty(dir)) {
                setSelection(MediaStore.Video.Media.DURATION + " > ?" + " and " + MediaStore.Video.Media.SIZE + " > ?");
                setSelectionArgs(new String[]{String.valueOf(duration), String.valueOf(fileSize)});
            } else {
                setSelection(MediaStore.Video.Media.DURATION + " > ?" + " and " + MediaStore.Video.Media.SIZE + " > ?" + " and " + MediaStore.Video.Media.DATA + " like ?");
                setSelectionArgs(new String[]{String.valueOf(duration), String.valueOf(fileSize), dir + "%"});
            }
        }
    }

//    public static List<VideoBean> get() {
//        Cursor cursor = BaseApp.getInstance().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
//                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
//                println("image uri is $uri")
//            }
//            cursor.close()
//        }
//    }

}
