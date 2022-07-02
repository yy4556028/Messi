package com.example.lib_map_amap.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePickerView {

    public static void pickYear(Context context, final TextView view, final OnYearPickListener listener) {
        int year = 0;
        if (view != null) {
            try {
                year = Integer.parseInt(view.getText().toString().replace("年", ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (year < 1970 || year > 2100) {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
        }

        final NumberPicker mYearSpinner = new NumberPicker(context);
        mYearSpinner.setMinValue(1970);
        mYearSpinner.setMaxValue(2100);
        mYearSpinner.setValue(year);
        mYearSpinner.setWrapSelectorWheel(false);

        new AlertDialog.Builder(context).setTitle("选择年份").
                setView(mYearSpinner).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (view != null) {
                    view.setText(mYearSpinner.getValue() + "年");
                }
                if (listener != null) {
                    listener.onYearPick(mYearSpinner.getValue());
                }
            }
        }).setNegativeButton("取消", null).show();
    }

    public static void pickMonth(Context context, final TextView view, final OnMonthPickListener listener) {
        int month = -1;
        if (view != null) {
            try {
                month = Integer.parseInt(view.getText().toString().replace("月", "")) - 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (month < 0 || month > 11) {
            Calendar calendar = Calendar.getInstance();
            month = calendar.get(Calendar.MONTH);
        }

        final NumberPicker mMonthSpinner = new NumberPicker(context);
        mMonthSpinner.setMinValue(0);
        mMonthSpinner.setMaxValue(11);
        mMonthSpinner.setValue(month);
        mMonthSpinner.setDisplayedValues(new DateFormatSymbols().getShortMonths());

        new AlertDialog.Builder(context).setTitle("选择月份").
                setView(mMonthSpinner).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (view != null) {
                    view.setText((mMonthSpinner.getValue() + 1) + "月");
                }
                if (listener != null) {
                    listener.onMonthPick(mMonthSpinner.getValue() + 1);
                }
            }
        }).setNegativeButton("取消", null).show();
    }

    public static void pickDate(final SimpleDateFormat simpleDateFormat, final View anchorView, final OnDatePickListener listener) {
        Calendar calendar = Calendar.getInstance();

        try {
            if (anchorView instanceof TextView) {
                calendar.setTime(simpleDateFormat.parse(((TextView) anchorView).getText().toString()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final DatePickerDialog dialog = new DatePickerDialog(anchorView.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatePicker datePicker = dialog.getDatePicker();
                Calendar calendar = Calendar.getInstance();
                calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

                if (anchorView instanceof TextView) {
                    ((TextView) anchorView).setText(simpleDateFormat.format(calendar.getTime()));
                }

                if (listener != null) {
                    listener.onDatePick(calendar);
                }
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
//        datePickerDialog.setTitle("");
//        dialog.getDatePicker().init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);
        dialog.show();
    }

    public interface OnDateTimePickListener {
        void onDateTimePick(Calendar calendar);
    }

    private OnDateTimePickListener onDateTimePickListener;

    public interface OnDatePickListener {
        void onDatePick(Calendar calendar);
    }

    private OnDateTimePickListener onDatePickListener;

    public interface OnYearPickListener {
        void onYearPick(int year);
    }

    public interface OnMonthPickListener {
        void onMonthPick(int month);
    }
}

