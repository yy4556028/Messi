package com.yuyang.messi.db.step;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.yuyang.messi.MessiApp;
import com.yuyang.messi.bean.StepBean;

import java.util.ArrayList;
import java.util.List;

public class StepDao {

    private StepDBHelper dbHelper;

    public StepDao() {
        dbHelper = new StepDBHelper(MessiApp.getInstance());
    }

    public List<StepBean> getAll() {
        List<StepBean> result = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(StepDBHelper.TABLE_NAME, null, null, null, null, null, StepBean.TODAY + " ASC", null);
        while (cursor.moveToNext()) {
            StepBean model = new StepBean();
            model.setToday(cursor.getString(cursor.getColumnIndex(StepBean.TODAY)));
            model.setStep(cursor.getInt(cursor.getColumnIndex(StepBean.STEP)));
            result.add(model);
        }
        cursor.close();
        return result;
    }

    public StepBean getByDate(String today) {
        if (TextUtils.isEmpty(today))
            return null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        StepBean stepBean = null;
        Cursor cursor = db.query(StepDBHelper.TABLE_NAME, null, StepBean.TODAY + " = ?", new String[]{today}, null, null, null);
        if (cursor.moveToNext()) {
            stepBean = new StepBean();
            stepBean.setToday(cursor.getString(cursor.getColumnIndex(StepBean.TODAY)));
            stepBean.setStep(cursor.getInt(cursor.getColumnIndex(StepBean.STEP)));
        }
        cursor.close();
        return stepBean;
    }

    public void insert(StepBean stepBean) {
        if (stepBean == null || TextUtils.isEmpty(stepBean.getToday())) {
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = stepBean.toContentValues();
        if (exists(stepBean)) {
            db.updateWithOnConflict(StepDBHelper.TABLE_NAME, values, StepBean.TODAY + " = ?", new String[]{stepBean.getToday()}, SQLiteDatabase.CONFLICT_IGNORE);
        } else {
            db.insertWithOnConflict(StepDBHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        }
    }

    public void delete(StepBean stepBean) {
        if (stepBean == null || TextUtils.isEmpty(stepBean.getToday())) {
            return;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete(StepDBHelper.TABLE_NAME, StepBean.TODAY + " = ?", new String[]{stepBean.getToday()});
    }

    public boolean exists(StepBean stepBean) {
        if (stepBean == null || TextUtils.isEmpty(stepBean.getToday()))
            return false;
        boolean result;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(StepDBHelper.TABLE_NAME, null, StepBean.TODAY + " = ?", new String[]{stepBean.getToday()}, null, null, null);
        result = cursor.moveToNext();
        cursor.close();
        return result;
    }

}

