package com.yuyang.messi.db.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yuyang.messi.download.FileDownloadModel;

public class FileDownloadDBOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "filedownload.db";
    public final static int DATABASE_VERSION = 1;

    public FileDownloadDBOpenHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + FileDownloadDBHelper.TABLE_NAME
                + String.format(
                "(" +
                        "%s INTEGER PRIMARY KEY, " + // id
                        "%s VARCHAR, " + // icon
                        "%s VARCHAR, " + // name
                        "%s VARCHAR, " + // url
                        "%s VARCHAR, " + // package name
                        "%s VARCHAR, " + // version
                        "%s VARCHAR" + // path
                        ")",
                FileDownloadModel.ID,
                FileDownloadModel.ICON,
                FileDownloadModel.NAME,
                FileDownloadModel.URL,
                FileDownloadModel.PACKAGE,
                FileDownloadModel.VERSION,
                FileDownloadModel.PATH));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
