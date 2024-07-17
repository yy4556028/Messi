package com.yuyang.messi.ui.home.overwork;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yuyang.lib_base.bean.PopBean;
import com.yuyang.lib_base.ui.base.BaseFragment;
import com.yuyang.lib_base.ui.view.picker.BottomChooseDialog;
import com.yuyang.lib_base.utils.FileUtil;
import com.yuyang.lib_base.utils.StorageUtil;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.MessiApp;
import com.yuyang.messi.R;
import com.yuyang.messi.utils.CalendarUtil;
import com.yuyang.messi.utils.FontUtil;
import com.yuyang.messi.utils.SharedPreferencesUtil;
import com.yuyang.messi.view.Picker.DatePickerView;
import com.yuyang.messi.view.Picker.picker.ColorPicker;
import com.yuyang.messi.view.scroll.month_view.Day;
import com.yuyang.messi.view.scroll.month_view.MonthView;
import com.yuyang.messi.widget.ChartMarkerView;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class OverworkFlexFragment extends BaseFragment {

    private final String KEY_SP_COLOR = OverworkFlexFragment.class.getSimpleName() + "_key_color";
    private final String KEY_TARGET_OFF = OverworkFlexFragment.class.getSimpleName() + "_key_target_off";
    private final String saveFilePath = StorageUtil.getExternalFile("/Overwork/data_flex.txt").getAbsolutePath();
    private final String saveFilePath_public = StorageUtil.getPublicPath("/Overwork/data_flex.txt");

    private static final String ON_TIME_MIN = "08:30";
    private static final String ON_TIME_MAX = "10:00";
    private static final long WORK_TIME_MINUTE = TimeUnit.HOURS.toMinutes(9);//最少上班时长
    private static final String OFF_TIME_NORMAL = "18:00";
    private static final String DEFAULT_TARGET_WORK_TIME = "10:00";//目标平均工作时长 9小时 (不包含午休)

    private static final int DEFAULT_COLOR_NORMAL = ContextCompat.getColor(MessiApp.getInstance(), R.color.textSecondary);
    private static final int DEFAULT_COLOR_PROBLEM = ContextCompat.getColor(MessiApp.getInstance(), R.color.red);

    private static final int DEFAULT_COLOR_OFF_LEVEL1 = ContextCompat.getColor(MessiApp.getInstance(), R.color.theme_ripple);
    private static final int DEFAULT_COLOR_OFF_LEVEL2 = ContextCompat.getColor(MessiApp.getInstance(), R.color.theme);
    private static final int DEFAULT_COLOR_OFF_LEVEL3 = ContextCompat.getColor(MessiApp.getInstance(), R.color.cyan);
    private static final int DEFAULT_COLOR_OFF_LEVEL4 = ContextCompat.getColor(MessiApp.getInstance(), R.color.yellow);

    private final SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
    private final SimpleDateFormat ymdSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat dateTimeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    private final SimpleDateFormat hourMinuteSdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

    private TextView selectDateText;
    private TextView infoText;
    private TextView setTargetOffText;

    private ImageView normalImage;
    private TextView normalText;
    private ImageView problemImage;
    private TextView problemText;
    private ImageView overworkLevel1Image;
    private TextView overworkLevel1Text;
    private ImageView overworkLevel2Image;
    private TextView overworkLevel2Text;
    private ImageView overworkLevel3Image;
    private TextView overworkLevel3Text;
    private ImageView overworkLevel4Image;
    private TextView overworkLevel4Text;

    private MonthView<OverworkBean> monthView;

    private LineChart lineChart;

    private HashMap<String, List<OverworkBean>> beanMap;

    private String targetWorkTime;
    private int color_normal;
    private int color_problem;
    private int color_off_level1;
    private int color_off_level2;
    private int color_off_level3;
    private int color_off_level4;

    private final static ValueFormatter valueFormatter = new ValueFormatter() {//设置柱状图Y轴的数据格式

        @Override
        public String getFormattedValue(float arg0) {
            if (arg0 == 0) {
                return "";
            } else {
                return new DecimalFormat("0.##").format(arg0) + "小时";
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_overwork_flex;
    }

    @Override
    protected void doOnViewCreated() {
        loadSaveData();
        initViews();
        initEvents();
        Calendar calendar = Calendar.getInstance();
        selectDateText.setText(dateSdf.format(calendar.getTime()));
        loadDataAndUpdate(calendar);
        calcInfo();
    }

    private void loadSaveData() {
        String data = null;
        // 如果AndroidQ 且 非兼容模式
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Environment.isExternalStorageLegacy()) {
//            data = FileUtil.readFileV29(saveFilePath_public);
//        } else {
        if (new File(saveFilePath).exists())
            data = FileUtil.readFile(saveFilePath);
//        }

        if (!TextUtils.isEmpty(data)) {
            try {
                beanMap = new Gson().fromJson(data, new TypeToken<HashMap<String, List<OverworkBean>>>() {
                }.getType());
            } catch (Exception ignored) {
                ToastUtil.showToast("数据错误");
            }
        }

        if (beanMap == null) beanMap = new HashMap<>();

        String colorStr = SharedPreferencesUtil.getString(KEY_SP_COLOR, null);
        if (TextUtils.isEmpty(colorStr)) {
            color_normal = DEFAULT_COLOR_NORMAL;
            color_problem = DEFAULT_COLOR_PROBLEM;
            color_off_level1 = DEFAULT_COLOR_OFF_LEVEL1;
            color_off_level2 = DEFAULT_COLOR_OFF_LEVEL2;
            color_off_level3 = DEFAULT_COLOR_OFF_LEVEL3;
            color_off_level4 = DEFAULT_COLOR_OFF_LEVEL4;
        } else {
            List<Integer> colorList = new Gson().fromJson(colorStr, new TypeToken<List<Integer>>() {
            }.getType());
            color_normal = colorList.get(0);
            color_problem = colorList.get(1);
            color_off_level1 = colorList.get(2);
            color_off_level2 = colorList.get(3);
            color_off_level3 = colorList.get(4);
            color_off_level4 = colorList.get(5);
        }
        targetWorkTime = SharedPreferencesUtil.getString(KEY_TARGET_OFF, DEFAULT_TARGET_WORK_TIME);
    }

    private void initViews() {
        normalImage = rootView.findViewById(R.id.fragment_overwork_normalView);
        normalText = rootView.findViewById(R.id.fragment_overwork_normalText);
        problemImage = rootView.findViewById(R.id.fragment_overwork_problemView);
        problemText = rootView.findViewById(R.id.fragment_overwork_problemText);
        overworkLevel1Image = rootView.findViewById(R.id.fragment_overwork_overworkLevel1Image);
        overworkLevel1Text = rootView.findViewById(R.id.fragment_overwork_overworkLevel1Text);
        overworkLevel2Image = rootView.findViewById(R.id.fragment_overwork_overworkLevel2Image);
        overworkLevel2Text = rootView.findViewById(R.id.fragment_overwork_overworkLevel2Text);
        overworkLevel3Image = rootView.findViewById(R.id.fragment_overwork_overworkLevel3Image);
        overworkLevel3Text = rootView.findViewById(R.id.fragment_overwork_overworkLevel3Text);
        overworkLevel4Image = rootView.findViewById(R.id.fragment_overwork_overworkLevel4Image);
        overworkLevel4Text = rootView.findViewById(R.id.fragment_overwork_overworkLevel4Text);

        normalImage.setImageDrawable(new ColorDrawable(color_normal));
        problemImage.setImageDrawable(new ColorDrawable(color_problem));
        overworkLevel1Image.setImageDrawable(new ColorDrawable(color_off_level1));
        overworkLevel2Image.setImageDrawable(new ColorDrawable(color_off_level2));
        overworkLevel3Image.setImageDrawable(new ColorDrawable(color_off_level3));
        overworkLevel4Image.setImageDrawable(new ColorDrawable(color_off_level4));

        monthView = rootView.findViewById(R.id.fragment_overwork_monthView);
        lineChart = rootView.findViewById(R.id.fragment_overwork_chart);
        initChart();

        monthView.setOnItemViewListener(new MonthView.OnItemViewListener<OverworkBean>() {
            @Override
            public int onCreateView() {
                return R.layout.fragment_overwork_day_item;
            }

            @Override
            public void onBindView(View view, int pos, Day<OverworkBean> day, int monthDiff) {
                TextView dayText = view.findViewById(R.id.fragment_overwork_day_item_dayText);
                TextView onTimeText = view.findViewById(R.id.fragment_overwork_day_item_onTimeText);
                TextView offTimeText = view.findViewById(R.id.fragment_overwork_day_item_offTimeText);

                dayText.setText(String.valueOf(day.getCalendar().get(Calendar.DAY_OF_MONTH)));

                if (monthDiff == 0) {   //本月

                    if (TextUtils.equals(day.getBean().getDate(), ymdSdf.format(Calendar.getInstance().getTime()))) {   //今天
                        dayText.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                        view.setBackgroundResource(R.drawable.rectangle_stroke_white);
                    } else {
                        dayText.setTypeface(null, Typeface.NORMAL);
                        view.setBackground(null);
                    }

                    if (day.getBean().isCalcFlag()) {
                        dayText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                    } else {
                        dayText.setTextColor(ContextCompat.getColor(requireContext(), R.color.textSecondary));
                    }

                    if (day.getBean() == null) {
                        onTimeText.setText(null);
                        offTimeText.setText(null);
                    } else {
                        if (day.getBean().getOnTimeMills() == null) {
                            onTimeText.setText(null);
                        } else {

                            onTimeText.setText(hourMinuteSdf.format(new Date(day.getBean().getOnTimeMills())));
                            onTimeText.setTextColor(getOnTimeColor(day.getBean()));
                        }
                        if (day.getBean().getOffTimeMills() == null) {
                            offTimeText.setText(null);
                        } else {
                            offTimeText.setText(hourMinuteSdf.format(new Date(day.getBean().getOffTimeMills())));
                            offTimeText.setTextColor(getOffTimeColor(day.getBean()));
                        }
                    }
                } else {
                    view.setBackground(null);
                    dayText.setTypeface(null, Typeface.NORMAL);
                    dayText.setTextColor(ContextCompat.getColor(requireContext(), R.color.textSecondary));
                    onTimeText.setText(null);
                    offTimeText.setText(null);
                }
            }

            @Override
            public void onDayClick(final Day<OverworkBean> day, int monthDiff) {
                if (monthDiff == 0) {
                    String title;
                    if (day.getBean().getOnTimeMills() == null && day.getBean().getOffTimeMills() == null) {
                        title = "请选择";
                    } else {
                        title = String.format("%s - %s",
                                day.getBean().getOnTimeMills() == null ? "" : dateTimeSdf.format(new Date(day.getBean().getOnTimeMills())),
                                day.getBean().getOffTimeMills() == null ? "" : dateTimeSdf.format(new Date(day.getBean().getOffTimeMills())));
                    }

                    BottomChooseDialog.showSingle(getActivity(),
                            title,
                            Arrays.asList(new PopBean(null, "设置上班时间"),
                                    new PopBean(null, "设置下班时间"),
                                    new PopBean(null, "清除当日数据"),
                                    new PopBean(null, day.getBean().isCalcFlag() ? "取消参与计算时间" : "参与计算时间")),
                            new BottomChooseDialog.SingleChoiceListener() {
                                @Override
                                public void onItemClick(int index, PopBean popBean) {
                                    switch (popBean.getName()) {
                                        case "设置上班时间": {
                                            String onTimeStr;
                                            if (day.getBean().getOnTimeMills() == null) {
                                                if (CalendarUtil.isSameDay(day.getCalendar().getTimeInMillis(), Calendar.getInstance().getTimeInMillis())) {
                                                    onTimeStr = dateTimeSdf.format(Calendar.getInstance().getTime());
                                                } else {
                                                    onTimeStr = day.getBean().getDate() + " " + ON_TIME_MIN;
                                                }
                                            } else {
                                                onTimeStr = dateTimeSdf.format(new Date(day.getBean().getOnTimeMills()));
                                            }
                                            DatePickerView.pickDateTime(getActivity(), null, dateTimeSdf, onTimeStr, new DatePickerView.OnPickListener() {
                                                @Override
                                                public void onPick(Calendar calendar) {
                                                    day.getBean().setOnTimeMills(calendar.getTimeInMillis());
                                                    monthView.updateDay(day);
                                                    saveDataFile();
                                                    calcInfo();
                                                }
                                            });
                                            break;
                                        }
                                        case "设置下班时间": {
                                            String offTimeStr;
                                            if (day.getBean().getOffTimeMills() == null) {
                                                if (CalendarUtil.isSameDay(day.getCalendar().getTimeInMillis(), Calendar.getInstance().getTimeInMillis())) {
                                                    offTimeStr = dateTimeSdf.format(Calendar.getInstance().getTime());
                                                } else {
                                                    offTimeStr = day.getBean().getDate() + " " + OFF_TIME_NORMAL;
                                                }
                                            } else {
                                                offTimeStr = dateTimeSdf.format(new Date(day.getBean().getOffTimeMills()));
                                            }
                                            DatePickerView.pickDateTime(getActivity(), null, dateTimeSdf, offTimeStr, new DatePickerView.OnPickListener() {
                                                @Override
                                                public void onPick(Calendar calendar) {
                                                    day.getBean().setOffTimeMills(calendar.getTimeInMillis());
                                                    monthView.updateDay(day);
                                                    saveDataFile();
                                                    calcInfo();
                                                }
                                            });
                                            break;
                                        }
                                        case "清除当日数据": {
                                            day.getBean().setOnTimeMills(null);
                                            day.getBean().setOffTimeMills(null);
                                            monthView.updateDay(day);
                                            saveDataFile();
                                            calcInfo();
                                            break;
                                        }
                                        case "参与计算时间": {
                                            day.getBean().setCalcFlag(true);
                                            monthView.updateDay(day);
                                            saveDataFile();
                                            calcInfo();
                                            break;
                                        }
                                        case "取消参与计算时间": {
                                            day.getBean().setCalcFlag(false);
                                            monthView.updateDay(day);
                                            saveDataFile();
                                            calcInfo();
                                            break;
                                        }
                                    }
                                }
                            });
                }
            }
        });

        selectDateText = rootView.findViewById(R.id.fragment_overwork_selectDateText);
        infoText = rootView.findViewById(R.id.fragment_overwork_infoText);
        infoText.setTypeface(FontUtil.getBoldTypeFace());
        setTargetOffText = rootView.findViewById(R.id.fragment_overwork_setTargetOffText);
        setTargetOffText.setText(String.format("工时指标：%s", targetWorkTime));
    }

    private void initEvents() {
        setTargetOffText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerView.pickTime(getActivity(), null, targetWorkTime, hourMinuteSdf, new DatePickerView.OnPickListener() {
                    @Override
                    public void onPick(Calendar calendar) {
                        targetWorkTime = hourMinuteSdf.format(calendar.getTime());
                        setTargetOffText.setText(String.format("工时指标：%s", targetWorkTime));
                        SharedPreferencesUtil.setString(KEY_TARGET_OFF, targetWorkTime);
                        calcInfo();
                    }
                });
            }
        });
        rootView.findViewById(R.id.fragment_overwork_resetColorText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(requireContext())
                        .setMessage("确认重置颜色吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferencesUtil.setString(KEY_SP_COLOR, null);
                                color_normal = DEFAULT_COLOR_NORMAL;
                                color_problem = DEFAULT_COLOR_PROBLEM;
                                color_off_level1 = DEFAULT_COLOR_OFF_LEVEL1;
                                color_off_level2 = DEFAULT_COLOR_OFF_LEVEL2;
                                color_off_level3 = DEFAULT_COLOR_OFF_LEVEL3;
                                color_off_level4 = DEFAULT_COLOR_OFF_LEVEL4;
                                normalImage.setImageDrawable(new ColorDrawable(color_normal));
                                problemImage.setImageDrawable(new ColorDrawable(color_problem));
                                overworkLevel1Image.setImageDrawable(new ColorDrawable(color_off_level1));
                                overworkLevel2Image.setImageDrawable(new ColorDrawable(color_off_level2));
                                overworkLevel3Image.setImageDrawable(new ColorDrawable(color_off_level3));
                                overworkLevel4Image.setImageDrawable(new ColorDrawable(color_off_level4));
                                monthView.updateMonth();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
        rootView.findViewById(R.id.fragment_overwork_clearMonthText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setMessage("数据清除后不可恢复？")
                        .setPositiveButton("清除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                beanMap.put(dateSdf.format(monthView.currentMonthCalendar.getTime()), null);
                                saveDataFile();
                                loadDataAndUpdate(monthView.currentMonthCalendar);
                                calcInfo();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
        rootView.findViewById(R.id.fragment_overwork_tvBackup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Environment.isExternalStorageLegacy()) {
                            boolean isSuccess = FileUtil.writeFileV29(StorageUtil.getPublicPath("/Overwork/data_flex_backup.txt"), new Gson().toJson(beanMap));
                            if (isSuccess) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showToast("文件备份成功");
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
        normalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPicker picker = new ColorPicker(getActivity());
                picker.setInitColor(color_normal);
                picker.setOnColorPickListener(new ColorPicker.OnColorPickListener() {
                    @Override
                    public void onColorPicked(int pickedColor) {
                        color_normal = pickedColor;
                        SharedPreferencesUtil.setString(KEY_SP_COLOR, new Gson().toJson(Arrays.asList(color_normal, color_problem,
                                color_off_level1, color_off_level2, color_off_level3, color_off_level4)));
                        normalImage.setImageDrawable(new ColorDrawable(color_normal));
                        monthView.updateMonth();
                    }
                });
                picker.show();
            }
        });
        normalText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                normalImage.callOnClick();
            }
        });
        problemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPicker picker = new ColorPicker(getActivity());
                picker.setInitColor(color_problem);
                picker.setOnColorPickListener(new ColorPicker.OnColorPickListener() {
                    @Override
                    public void onColorPicked(int pickedColor) {
                        color_problem = pickedColor;
                        SharedPreferencesUtil.setString(KEY_SP_COLOR, new Gson().toJson(Arrays.asList(color_normal, color_problem,
                                color_off_level1, color_off_level2, color_off_level3, color_off_level4)));
                        problemImage.setImageDrawable(new ColorDrawable(color_problem));
                        monthView.updateMonth();
                    }
                });
                picker.show();
            }
        });
        problemText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                problemImage.callOnClick();
            }
        });
        overworkLevel1Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPicker picker = new ColorPicker(getActivity());
                picker.setInitColor(color_off_level1);
                picker.setOnColorPickListener(new ColorPicker.OnColorPickListener() {
                    @Override
                    public void onColorPicked(int pickedColor) {
                        color_off_level1 = pickedColor;
                        SharedPreferencesUtil.setString(KEY_SP_COLOR, new Gson().toJson(Arrays.asList(color_normal, color_problem,
                                color_off_level1, color_off_level2, color_off_level3, color_off_level4)));
                        overworkLevel1Image.setImageDrawable(new ColorDrawable(color_off_level1));
                        monthView.updateMonth();
                    }
                });
                picker.show();
            }
        });
        overworkLevel1Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overworkLevel1Image.callOnClick();
            }
        });
        overworkLevel2Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPicker picker = new ColorPicker(getActivity());
                picker.setInitColor(color_off_level2);
                picker.setOnColorPickListener(new ColorPicker.OnColorPickListener() {
                    @Override
                    public void onColorPicked(int pickedColor) {
                        color_off_level2 = pickedColor;
                        SharedPreferencesUtil.setString(KEY_SP_COLOR, new Gson().toJson(Arrays.asList(color_normal, color_problem,
                                color_off_level1, color_off_level2, color_off_level3, color_off_level4)));
                        overworkLevel2Image.setImageDrawable(new ColorDrawable(color_off_level2));
                        monthView.updateMonth();
                    }
                });
                picker.show();
            }
        });
        overworkLevel2Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overworkLevel2Image.callOnClick();
            }
        });
        overworkLevel3Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPicker picker = new ColorPicker(getActivity());
                picker.setInitColor(color_off_level3);
                picker.setOnColorPickListener(new ColorPicker.OnColorPickListener() {
                    @Override
                    public void onColorPicked(int pickedColor) {
                        color_off_level3 = pickedColor;
                        SharedPreferencesUtil.setString(KEY_SP_COLOR, new Gson().toJson(Arrays.asList(color_normal, color_problem,
                                color_off_level1, color_off_level2, color_off_level3, color_off_level4)));
                        overworkLevel3Image.setImageDrawable(new ColorDrawable(color_off_level3));
                        monthView.updateMonth();
                    }
                });
                picker.show();
            }
        });
        overworkLevel3Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overworkLevel3Image.callOnClick();
            }
        });
        overworkLevel4Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPicker picker = new ColorPicker(getActivity());
                picker.setInitColor(color_off_level4);
                picker.setOnColorPickListener(new ColorPicker.OnColorPickListener() {
                    @Override
                    public void onColorPicked(int pickedColor) {
                        color_off_level4 = pickedColor;
                        SharedPreferencesUtil.setString(KEY_SP_COLOR, new Gson().toJson(Arrays.asList(color_normal, color_problem,
                                color_off_level1, color_off_level2, color_off_level3, color_off_level4)));
                        overworkLevel4Image.setImageDrawable(new ColorDrawable(color_off_level4));
                        monthView.updateMonth();
                    }
                });
                picker.show();
            }
        });
        overworkLevel4Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overworkLevel4Image.callOnClick();
            }
        });

        rootView.findViewById(R.id.fragment_overwork_prevMonthView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monthView.currentMonthCalendar.add(Calendar.MONTH, -1);
                loadDataAndUpdate(monthView.currentMonthCalendar);
                selectDateText.setText(dateSdf.format(monthView.currentMonthCalendar.getTime()));
                calcInfo();
            }
        });
        rootView.findViewById(R.id.fragment_overwork_nextMonthView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monthView.currentMonthCalendar.add(Calendar.MONTH, 1);
                loadDataAndUpdate(monthView.currentMonthCalendar);
                selectDateText.setText(dateSdf.format(monthView.currentMonthCalendar.getTime()));
                calcInfo();
            }
        });
        selectDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerView.pickYearMonth(getActivity(), null, new DatePickerView.OnYearMonthPickListener() {
                    @Override
                    public void onYearMonthPick(int year, int month) {
                        monthView.currentMonthCalendar.set(Calendar.YEAR, year);
                        monthView.currentMonthCalendar.set(Calendar.DAY_OF_MONTH, 1);
                        monthView.currentMonthCalendar.set(Calendar.MONTH, month);
                        monthView.updateCalendar(monthView.currentMonthCalendar);
                        selectDateText.setText(dateSdf.format(monthView.currentMonthCalendar.getTime()));
                        loadDataAndUpdate(monthView.currentMonthCalendar);
                        calcInfo();
                    }
                });
            }
        });

    }

    private void loadDataAndUpdate(Calendar calendar) {
        Calendar tempCalendar = (Calendar) calendar.clone();
        String dateKey = dateSdf.format(tempCalendar.getTime());
        List<OverworkBean> beanList = beanMap.get(dateKey);
        if (beanList == null) {
            beanList = new ArrayList<>();
            beanMap.put(dateKey, beanList);

            int size = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int i = tempCalendar.getActualMinimum(Calendar.DAY_OF_MONTH); i <= size; i++) {
                tempCalendar.set(Calendar.DAY_OF_MONTH, i);
                OverworkBean overworkBean = new OverworkBean();
                overworkBean.setDate(ymdSdf.format(tempCalendar.getTime()));
                overworkBean.setWeek(tempCalendar.get(Calendar.DAY_OF_WEEK));
                if (tempCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                        tempCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    overworkBean.setCalcFlag(false);
                } else {
                    overworkBean.setCalcFlag(true);
                }
                beanList.add(overworkBean);
            }
        }

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int indexOffset = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        for (int i = 0; i < beanList.size(); i++) {
            monthView.getDayList().get(i + indexOffset).setBean(beanList.get(i));
        }
        for (int i = 0; i < monthView.getDayList().size(); i++) {
            if (i < indexOffset || i >= indexOffset + beanList.size()) {
                monthView.getDayList().get(i).setBean(null);
            } else {
                monthView.getDayList().get(i).setBean(beanList.get(i - indexOffset));
            }
        }

        monthView.updateCalendar(calendar);
    }

    private void calcInfo() {
        int count = 0;//有数据的天数
        int remainFridayCount = 0;//剩余周五天数
        int remainNotFridayCount = 0;//剩余非周五天数
        long totalWorkTimeMills = 0;//总工作时长

        for (Day<OverworkBean> overworkBeanDay : monthView.getDayList()) {
            if (overworkBeanDay.getBean() == null) continue;

            OverworkBean overworkBean = overworkBeanDay.getBean();
            Long onTimeMills = overworkBean.getOnTimeMills();
            Long offTimeMills = overworkBean.getOffTimeMills();

            if (onTimeMills == null || offTimeMills == null) {
                if (overworkBean.isCalcFlag()) {
                    if (overworkBean.getWeek() == Calendar.FRIDAY) {
                        remainFridayCount++;
                    } else {
                        remainNotFridayCount++;
                    }
                }
                continue;
            }

            if (overworkBean.isCalcFlag()) {
                count++;
                totalWorkTimeMills += (offTimeMills - onTimeMills);
            }
        }
        if (count == 0) {
            infoText.setText("无数据");
            return;
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(totalWorkTimeMills / count);
        float hourFraction = minutes / 60f;
        long hour = TimeUnit.MINUTES.toHours(minutes);
        minutes = minutes - TimeUnit.HOURS.toMinutes(hour);

        StringBuilder retStr = new StringBuilder();
        retStr.append(hour).append("小时");
        if (minutes > 0) {
            retStr.append(minutes).append("分钟");
        }
        retStr.append(String.format("(%s小时)", new DecimalFormat("0.#").format(hourFraction)));

        long targetMinutesPerDay = 0;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(hourMinuteSdf.parse(targetWorkTime));
            targetMinutesPerDay = TimeUnit.HOURS.toMinutes(calendar.get(Calendar.HOUR_OF_DAY)) + calendar.get(Calendar.MINUTE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (remainNotFridayCount == 0) {
            infoText.setText(String.format("当月平均工作时长：%s", retStr));
        } else {
            long expectMinutesPerDay = (targetMinutesPerDay * (count + remainFridayCount + remainNotFridayCount) - TimeUnit.MILLISECONDS.toMinutes(totalWorkTimeMills) - WORK_TIME_MINUTE * remainFridayCount) / remainNotFridayCount;

            if (expectMinutesPerDay <= WORK_TIME_MINUTE) {
                infoText.setText(String.format("当月平均工作时长：%s\r\n本月时间已达标", retStr));
            } else {
                long remainHour = TimeUnit.MINUTES.toHours(expectMinutesPerDay);
                float remainHourFraction = expectMinutesPerDay / 60f;
                long remainMinute = expectMinutesPerDay - TimeUnit.HOURS.toMinutes(remainHour);
                StringBuilder remainStr = new StringBuilder();
                remainStr.append(remainHour).append("小时");
                if (remainMinute > 0) {
                    remainStr.append(remainMinute).append("分钟");
                }
                remainStr.append(String.format("(%s小时)", new DecimalFormat("0.#").format(remainHourFraction)));

                infoText.setText(String.format("当月平均工作时长：%s\r\n本月剩余工作日(除周五)预期工作时长：%s",
                        retStr,
                        remainStr
                ));
            }
        }
        setChartData(monthView.getDayList());
    }

    private int getOnTimeColor(OverworkBean todayBean) {
        if (todayBean.getOnTimeMills() == null) return 0;

        if ((todayBean.getDate() + " " + ON_TIME_MAX).compareTo(dateTimeSdf.format(new Date(todayBean.getOnTimeMills()))) <= 0) {
            return color_problem;
        } else {
            return color_normal;
        }
    }

    private int getOffTimeColor(OverworkBean bean) {
        if (bean.getOffTimeMills() == null) return 0;

        if (bean.getOnTimeMills() == null) return color_normal;

        long workMills = bean.getOffTimeMills() - bean.getOnTimeMills();

        long overworkTimeMills = workMills - TimeUnit.MINUTES.toMillis(WORK_TIME_MINUTE);//加班时长

        if (overworkTimeMills < 0) {
            return color_problem;
        } else if (overworkTimeMills <= TimeUnit.HOURS.toMillis(1)) {
            return color_normal;
        } else if (overworkTimeMills <= TimeUnit.HOURS.toMillis(2)) {
            return color_off_level1;
        } else if (overworkTimeMills <= TimeUnit.HOURS.toMillis(3)) {
            return color_off_level2;
        } else if (overworkTimeMills <= TimeUnit.HOURS.toMillis(4)) {
            return color_off_level3;
        } else {
            return color_off_level4;
        }
    }

    private void saveDataFile() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                FileUtil.writeFile(saveFilePath, new Gson().toJson(beanMap));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Environment.isExternalStorageLegacy()) {
                    FileUtil.writeFileV29(saveFilePath_public, new Gson().toJson(beanMap));
                }
            }
        });
    }

    private void initChart() {
        lineChart.setDrawGridBackground(false);//是否展示网格线
        lineChart.setDrawBorders(false);//是否显示边界

        lineChart.setDragEnabled(true);//是否可以拖动
        lineChart.setTouchEnabled(true);//是否有触摸事件
        lineChart.setScaleEnabled(true);
//        lineChart.setScaleXEnabled(true);
//        lineChart.setScaleYEnabled(false);
        lineChart.setPinchZoom(true);

        lineChart.setGridBackgroundColor(Color.TRANSPARENT);
        lineChart.setBackgroundColor(Color.WHITE);

//        Description description = new Description();
//        description.setTextColor(Color.RED);
//        description.setText("呵呵呵呵呵");
//        description.setTextSize(16);
//        lineChart.setDescription(description);
        lineChart.setDescription(null);

        lineChart.setNoDataText("You need to provide data for the chart.");

        ChartMarkerView markerView = new ChartMarkerView(getActivity(), R.layout.widget_chart_marker_view);
        lineChart.setMarker(markerView);
        lineChart.setDrawMarkers(true);
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (handler != null) {
                    //刷新定时器
                    handler.removeCallbacks(valueChooseRunnable);
                    handler.postDelayed(valueChooseRunnable, 3000);
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });

        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        /***XY轴的设置***/
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
//        xAxis.setAxisMinimum(0);
//        xAxis.setAxisMaximum(dataList.size() - 0.9f);
        xAxis.setDrawGridLines(false);
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) value + "日";
            }
        });
        //xAxis.addLimitLine(llXAxis); // add x-axis limit line

        LimitLine limitLine_up = new LimitLine(20, "Upper Limit");
        limitLine_up.setLineWidth(4f);
        limitLine_up.enableDashedLine(10f, 10f, 0f);
        limitLine_up.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limitLine_up.setTextSize(10f);
        limitLine_up.setTextColor(Color.BLUE);
        limitLine_up.setTypeface(FontUtil.getRegularTypeFace());

        LimitLine limitLine_low = new LimitLine(0f, "Lower Limit");
        limitLine_low.setLineWidth(4f);
        limitLine_low.enableDashedLine(10f, 10f, 0f);
        limitLine_low.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        limitLine_low.setTextSize(10f);
        limitLine_low.setTypeface(FontUtil.getRegularTypeFace());

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setDrawGridLines(false);
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
//        leftAxis.addLimitLine(limitLine_up);
//        leftAxis.addLimitLine(limitLine_low);
        leftAxis.setAxisMaximum(20f);
        leftAxis.setAxisMinimum(0f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setValueFormatter(new ValueFormatter() {//设置柱状图Y轴的数据格式

            @Override
            public String getFormattedValue(float arg0) {
                return new DecimalFormat("0.##").format(arg0) + "小时";
            }
        });

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        lineChart.getAxisRight().setEnabled(false);

        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

//        mChart.setVisibleXRange(20);
//        mChart.setVisibleYRange(20f, AxisDependency.LEFT);
//        mChart.centerViewTo(20, 50, AxisDependency.LEFT);

        lineChart.animateX(2500);
        //mChart.invalidate();

        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        //设置显示类型，LINE CIRCLE SQUARE EMPTY 等等 多种方式，查看LegendForm 即可
        legend.setTextSize(12f);
        //显示位置 左下方
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //是否绘制在图表里面
        legend.setDrawInside(false);
    }

    private void setChartData(List<Day<OverworkBean>> beanList) {

        ArrayList<Entry> values = new ArrayList<>();

        Date normalOverworkDate = null;
        Date offTimeDate = null;

        int count = 1;
        for (int i = 0; i < beanList.size(); i++) {
            OverworkBean overworkBean = beanList.get(i).getBean();

            if (overworkBean == null) {
                continue;
            }

            if (overworkBean.getOffTimeMills() == null) {
                values.add(new Entry(count, 0));
                count++;
                continue;
            }

            try {
                normalOverworkDate = dateTimeSdf.parse(overworkBean.getDate() + " " + OFF_TIME_NORMAL);
                offTimeDate = new Date(overworkBean.getOffTimeMills());
            } catch (Exception e) {
                e.printStackTrace();
            }

            float hour = (offTimeDate.getTime() - normalOverworkDate.getTime()) / (1000 * 60 * 60f);
            values.add(new Entry(count, hour));
            count++;
        }

        LineDataSet lineDataSet;

        lineDataSet = new LineDataSet(values, "加班统计");

        // set the line to be drawn like this "- - - - - -"
//            set1.enableDashedLine(10f, 5f, 0f);
//            set1.enableDashedHighlightLine(10f, 5f, 0f);
        lineDataSet.setColor(Color.RED);
        lineDataSet.setCircleColor(Color.YELLOW);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(9f);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setValueFormatter(valueFormatter);

        lineDataSet.setHighLightColor(Color.RED); //设置高亮颜色
        lineDataSet.setHighlightEnabled(true);  //打开高亮开关
        lineDataSet.setHighlightLineWidth(1f);  //设置高亮宽度
        lineDataSet.setDrawHighlightIndicators(true);   //绘制高亮
        lineDataSet.setDrawVerticalHighlightIndicator(true);    //绘制垂直高亮
        lineDataSet.setDrawHorizontalHighlightIndicator(true); //不绘制水平高亮

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
        gradientDrawable.setColors(new int[]{Color.parseColor("#00ff0000"), Color.parseColor("#ff0000")});
        lineDataSet.setFillDrawable(gradientDrawable);
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet); // add the datasets

        LineData data = new LineData(dataSets);

        lineChart.getXAxis().setAxisMinimum(values.get(0).getX());
        lineChart.getXAxis().setAxisMaximum(values.get(values.size() - 1).getX());
        lineChart.clear();
        lineChart.fitScreen();
        lineChart.setData(data);
        lineChart.invalidate();
        lineChart.animateY(2000);
    }

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable valueChooseRunnable = new Runnable() {
        @Override
        public void run() {
            lineChart.highlightValues(null);
        }
    };
}
