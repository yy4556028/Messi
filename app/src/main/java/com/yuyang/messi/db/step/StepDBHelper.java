package com.yuyang.messi.db.step;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yuyang.messi.bean.StepBean;

public class StepDBHelper extends SQLiteOpenHelper {

    public final static String TABLE_NAME = StepDBHelper.class.getSimpleName().toLowerCase();
    private static final String DATABASE_NAME = "step.db";
    private final static int DATABASE_VERSION = 1;

    public StepDBHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
                + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + StepBean.TODAY + " VARCHAR(100),"
                + StepBean.STEP + " INTEGER"
                + ")";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
