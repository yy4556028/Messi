package com.yuyang.messi.helper;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.yuyang.messi.bean.ImageBean;
import com.yuyang.messi.bean.PhotoDirectory;
import com.yuyang.messi.ui.media.AlbumActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AlbumHelper {

    public static void loadPhoto(AppCompatActivity activity, Bundle bundle, PhotosResultCallback resultCallback) {
        LoaderManager.getInstance(activity).initLoader(0, bundle, new PhotoDirLoaderCallbacks(activity, resultCallback));
    }

    static class PhotoDirLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        private final WeakReference<Context> context;
        private final PhotosResultCallback resultCallback;

        PhotoDirLoaderCallbacks(Context context, PhotosResultCallback resultCallback) {
            this.context = new WeakReference<>(context);
            this.resultCallback = resultCallback;
        }

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new PhotoDirectoryLoader(context.get(), args.getBoolean(AlbumActivity.EXTRA_SHOW_GIF, false));
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

            if (cursor == null) return;

            List<PhotoDirectory> directories = new ArrayList<>();
            PhotoDirectory photoDirectoryAll = new PhotoDirectory();
            photoDirectoryAll.setName("所有图片");
            photoDirectoryAll.setId("ALL");

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                long imageId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                String bucketId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID));
                String bucketName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String imageName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                PhotoDirectory photoDirectory = new PhotoDirectory();
                photoDirectory.setId(bucketId);
                photoDirectory.setName(bucketName);

                ImageBean imageBean = new ImageBean(imageId, imageName, path);

                if (!directories.contains(photoDirectory)) {
                    photoDirectory.setCoverImageBean(imageBean);
                    photoDirectory.getImageBeans().add(imageBean);
                    photoDirectory.setDateAdded(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)));
                    directories.add(photoDirectory);
                } else {
                    directories.get(directories.indexOf(photoDirectory)).getImageBeans().add(imageBean);
                }

                photoDirectoryAll.getImageBeans().add(imageBean);
            }
            if (photoDirectoryAll.getImageBeans() != null && photoDirectoryAll.getImageBeans().size() > 0) {
                photoDirectoryAll.setCoverImageBean(photoDirectoryAll.getImageBeans().get(0));
            }
            directories.add(0, photoDirectoryAll);
            if (resultCallback != null) {
                resultCallback.onResultCallback(directories);
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        }
    }

    public interface PhotosResultCallback {
        void onResultCallback(List<PhotoDirectory> directories);
    }

    private static class PhotoDirectoryLoader extends CursorLoader {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DATE_ADDED
        };

        private PhotoDirectoryLoader(Context context, boolean showGif) {
            super(context);

            setProjection(IMAGE_PROJECTION);
            setUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            setSortOrder(MediaStore.Images.Media.DATE_ADDED + " DESC");

            setSelection(
                    MediaStore.MediaColumns.MIME_TYPE + "=? or " + MediaStore.MediaColumns.MIME_TYPE + "=? or " + MediaStore.MediaColumns.MIME_TYPE + "=? " + (showGif ? ("or " + MediaStore.MediaColumns.MIME_TYPE + "=?") : ""));
            String[] selectionArgs;
            if (showGif) {
                selectionArgs = new String[]{"image/jpeg", "image/png", "image/jpg", "image/gif"};
            } else {
                selectionArgs = new String[]{"image/jpeg", "image/png", "image/jpg"};
            }
            setSelectionArgs(selectionArgs);
        }
    }
}
