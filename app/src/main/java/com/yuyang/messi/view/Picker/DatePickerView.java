package com.yuyang.messi.view.Picker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;

import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.messi.MessiApp;
import com.yuyang.messi.R;

import java.lang.reflect.Field;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DatePickerView {

    public static void pickYear(Context context, final TextView textView, final OnYearPickListener listener) {
        int year = 0;
        if (textView != null) {
            try {
                year = Integer.parseInt(textView.getText().toString().replace("年", ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (year < 1970 || year > 2100) {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }

        final NumberPicker mYearSpinner = new NumberPicker(context);
        mYearSpinner.setMinValue(1970);
        mYearSpinner.setMaxValue(2100);
        mYearSpinner.setValue(year);
        mYearSpinner.setWrapSelectorWheel(false);

        new AlertDialog.Builder(context)
                .setTitle("选择年份")
                .setView(mYearSpinner)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (textView != null) {
                            textView.setText(String.format("%s年", mYearSpinner.getValue()));
                        }
                        if (listener != null) {
                            listener.onYearPick(mYearSpinner.getValue());
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    public static void pickMonth(Context context, final TextView textView, final OnMonthPickListener listener) {
        int month = -1;
        if (textView != null) {
            try {
                month = Integer.parseInt(textView.getText().toString().replace("月", "")) - 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (month < 0 || month > 11) {
            month = Calendar.getInstance().get(Calendar.MONTH);
        }

        final NumberPicker mMonthSpinner = new NumberPicker(context);
        mMonthSpinner.setMinValue(0);
        mMonthSpinner.setMaxValue(11);
        mMonthSpinner.setValue(month);
        mMonthSpinner.setDisplayedValues(new DateFormatSymbols().getShortMonths());

        new AlertDialog.Builder(context).setTitle("选择月份").
                setView(mMonthSpinner)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (textView != null) {
                            textView.setText(String.format("%s月", mMonthSpinner.getValue() + 1));
                        }
                        if (listener != null) {
                            listener.onMonthPick(mMonthSpinner.getValue() + 1);
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    public static void pickYearMonth(final Context context, final TextView textView, final OnYearMonthPickListener listener) {

        Calendar calendar = Calendar.getInstance();

        if (textView != null) {
            try {
                calendar.setTime(new SimpleDateFormat("yyyy年MM月", Locale.getDefault()).parse(textView.getText().toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        View rootView = LayoutInflater.from(context).inflate(R.layout.view_date_picker, null);
        final DatePicker datePicker = rootView.findViewById(R.id.view_date_picker_datePicker);
        rootView.findViewById(R.id.view_date_picker_timePicker).setVisibility(View.GONE);

        try {
//            datePicker.findViewById(com.android.internal.R.id.day).setVisibility(View.GONE);
            ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        new AlertDialog.Builder(context)
                .setTitle("选择年月")
                .setView(rootView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (textView != null) {
                            textView.setText(String.format("%s年%s月", datePicker.getYear(), datePicker.getMonth() + 1));
                        }
                        if (listener != null) {
                            listener.onYearMonthPick(datePicker.getYear(), datePicker.getMonth());
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    public static void pickMonthDay(final Context context, final TextView textView, final OnMonthDayPickListener listener) {

        Calendar calendar = Calendar.getInstance();

        if (textView != null) {
            try {
                calendar.setTime(new SimpleDateFormat("MM月dd日", Locale.getDefault()).parse(textView.getText().toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        View rootView = LayoutInflater.from(context).inflate(R.layout.view_date_picker, null);
        final DatePicker datePicker = rootView.findViewById(R.id.view_date_picker_datePicker);
        rootView.findViewById(R.id.view_date_picker_timePicker).setVisibility(View.GONE);

        try {
//            datePicker.findViewById(com.android.internal.R.id.day).setVisibility(View.GONE);
            ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(0).setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        new AlertDialog.Builder(context)
                .setTitle("选择月日")
                .setView(rootView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (textView != null) {
                            textView.setText(String.format("%s月%s日", datePicker.getMonth() + 1, datePicker.getDayOfMonth()));
                        }
                        if (listener != null) {
                            listener.onMonthDayPick(datePicker.getMonth(), datePicker.getDayOfMonth());
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    public static void pickDate(Context context, final TextView textView, final OnPickListener listener) {
        pickDate(context, textView, null, listener);
    }

    public static void pickDate(Context context, final TextView textView, SimpleDateFormat sdf, final OnPickListener listener) {
        final SimpleDateFormat finalSdf;
        if (sdf == null) {
            finalSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        } else {
            finalSdf = sdf;
        }
        Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(finalSdf.parse(textView.getText().toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        View rootView = LayoutInflater.from(context).inflate(R.layout.view_date_picker, null);
        final DatePicker datePicker = rootView.findViewById(R.id.view_date_picker_datePicker);
        rootView.findViewById(R.id.view_date_picker_timePicker).setVisibility(View.GONE);

        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);

        setDatePickerDividerColor(datePicker);

        new AlertDialog.Builder(context)
                .setTitle("选择日期")
                .setView(rootView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

                        if (textView != null) {
                            textView.setText(finalSdf.format(cal.getTime()));
                        }
                        if (listener != null) {
                            listener.onPick(cal);
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    public static void pickDateTime(Context context, final TextView textView, final OnPickListener listener) {
        pickDateTime(context, textView, null, null, listener);
    }

    public static void pickDateTime(Context context, final TextView textView, SimpleDateFormat sdf, String initStr, final OnPickListener listener) {
        final SimpleDateFormat titleSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm EEEE", Locale.getDefault());
        final SimpleDateFormat finalSdf;
        if (sdf == null) {
            finalSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        } else {
            finalSdf = sdf;
        }

        final Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(finalSdf.parse(!TextUtils.isEmpty(initStr) ? initStr : textView.getText().toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        View rootView = LayoutInflater.from(context).inflate(R.layout.view_date_picker, null);
        DatePicker datePicker = rootView.findViewById(R.id.view_date_picker_datePicker);
        TimePicker timePicker = rootView.findViewById(R.id.view_date_picker_timePicker);

        resizePicker(datePicker, PixelUtils.dp2px(50));
        resizePicker(timePicker, PixelUtils.dp2px(35));

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(rootView)
                .setTitle(titleSdf.format(calendar.getTime()))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (textView != null) {
                            textView.setText(finalSdf.format(calendar.getTime()));
                        }
                        if (listener != null) {
                            listener.onPick(calendar);
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create();

//        datePicker.setCalendarViewShown(false);
//        datePicker.setSpinnersShown(true);
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth);
                alertDialog.setTitle(titleSdf.format(calendar.getTime()));
            }
        });

        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                alertDialog.setTitle(titleSdf.format(calendar.getTime()));
            }
        });

        alertDialog.show();
    }

    public static void pickTime(Context context, final TextView textView, final OnPickListener listener) {
        pickTime(context, textView, null, null, listener);
    }

    public static void pickTime(final Context context, final TextView textView, String initStr, SimpleDateFormat sdf, final OnPickListener listener) {
        final SimpleDateFormat finalSdf;
        if (sdf == null) {
            finalSdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        } else {
            finalSdf = sdf;
        }

        final Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(finalSdf.parse(!TextUtils.isEmpty(initStr) ? initStr : textView.getText().toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        final View view = LayoutInflater.from(context).inflate(R.layout.view_date_picker, null);
        final TimePicker timePicker = view.findViewById(R.id.view_date_picker_timePicker);
        view.findViewById(R.id.view_date_picker_datePicker).setVisibility(View.GONE);
        timePicker.setIs24HourView(true);

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("选择时间")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (textView != null) {
                            textView.setText(finalSdf.format(calendar.getTime()));
                        }
                        if (listener != null) {
                            listener.onPick(calendar);
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create();

        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hh, int mm) {
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
            }
        });

        dialog.show();
    }

    private static void resizePicker(ViewGroup viewGroup, int width) {
        List<NumberPicker> npList = findNumberPicker(viewGroup);
        for (NumberPicker np : npList) {
            resizeNumberPicker(np, width);
        }
    }

    private static List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
        List<NumberPicker> list = new ArrayList<>();
        View child;
        if (viewGroup != null) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                child = viewGroup.getChildAt(i);
                if (child instanceof NumberPicker) {
                    list.add((NumberPicker) child);
                } else if (child instanceof LinearLayout) {
                    List<NumberPicker> result = findNumberPicker((ViewGroup) child);
                    if (result.size() > 0) {
                        return result;
                    }
                }
            }
        }
        return list;
    }

    private static void resizeNumberPicker(NumberPicker numberPicker, int width) {
        Resources mResources = Resources.getSystem();  //getResources()测试也可以
        int yearId = mResources.getIdentifier("year", "id", "android");
//        com.android.internal.R.id.year
        if (numberPicker.getId() == yearId) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (width * 1.3f), ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 0, 10, 0);
            numberPicker.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 0, 10, 0);
            numberPicker.setLayoutParams(params);
        }
    }

    private static void setDatePickerDividerColor(DatePicker datePicker) {
        // Divider changing:

        // 获取 mSpinners
        LinearLayout llFirst = (LinearLayout) datePicker.getChildAt(0);

        // 获取 NumberPicker
        LinearLayout mSpinners = (LinearLayout) llFirst.getChildAt(0);
        for (int i = 0; i < mSpinners.getChildCount(); i++) {
            NumberPicker picker = (NumberPicker) mSpinners.getChildAt(i);

            Field[] pickerFields = NumberPicker.class.getDeclaredFields();
            for (Field pf : pickerFields) {
                if (pf.getName().equals("mSelectionDivider")) {
                    pf.setAccessible(true);
                    try {
                        pf.set(picker, new ColorDrawable(MessiApp.getInstance().getResources().getColor(R.color.colorPrimary)));//设置分割线颜色
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    public interface OnPickListener {
        void onPick(Calendar calendar);
    }

    public interface OnYearPickListener {
        void onYearPick(int year);
    }

    public interface OnMonthPickListener {
        void onMonthPick(int month);
    }

    public interface OnYearMonthPickListener {
        void onYearMonthPick(int year, int month);
    }

    public interface OnMonthDayPickListener {
        void onMonthDayPick(int month, int day);
    }
}

