package com.yuyang.messi.db.download;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.yuyang.messi.MessiApp;
import com.yuyang.messi.download.FileDownloadModel;

import java.util.ArrayList;
import java.util.List;

public class FileDownloadDBHelper {

    public final static String TABLE_NAME = FileDownloadDBHelper.class.getSimpleName().toLowerCase();

    private final SQLiteDatabase db;

    public FileDownloadDBHelper() {
        FileDownloadDBOpenHelper openHelper = new FileDownloadDBOpenHelper(MessiApp.getInstance());
        db = openHelper.getWritableDatabase();
    }

    public List<FileDownloadModel> getAll() {
        final Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        final List<FileDownloadModel> list = new ArrayList<>();
        try {

            while (c.moveToNext()) {
                FileDownloadModel model = new FileDownloadModel();
                model.setId(c.getInt(c.getColumnIndex(FileDownloadModel.ID)));
                model.setIcon(c.getString(c.getColumnIndex(FileDownloadModel.ICON)));
                model.setName(c.getString(c.getColumnIndex(FileDownloadModel.NAME)));
                model.setUrl(c.getString(c.getColumnIndex(FileDownloadModel.URL)));
                model.setPackageName(c.getString(c.getColumnIndex(FileDownloadModel.PACKAGE)));
                model.setVersion(c.getString(c.getColumnIndex(FileDownloadModel.VERSION)));
                model.setPath(c.getString(c.getColumnIndex(FileDownloadModel.PATH)));
                list.add(model);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return list;
    }

    public boolean insert(FileDownloadModel downloadModel) {
        if (downloadModel == null) {
            return false;
        }

        // have to use FileDownloadUtils.generateId to associate TasksManagerModel with FileDownloader
        final int id = FileDownloadUtils.generateId(downloadModel.getUrl(), downloadModel.getPath());
        downloadModel.setId(id);

        return db.insert(TABLE_NAME, null, downloadModel.toContentValues()) != -1;
    }

    public void delete(FileDownloadModel downloadModel) {
        if (downloadModel == null) {
            return;
        }

        db.delete(TABLE_NAME, FileDownloadModel.ID + " = ?", new String[]{String.valueOf(downloadModel.getId())});
    }

}

