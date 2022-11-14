package com.yuyang.messi.ui.category.scroll;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.view.Picker.DatePickerView;
import com.yuyang.messi.view.scroll.month_view.Day;
import com.yuyang.messi.view.scroll.WeekCalendarPager;
import com.yuyang.messi.view.scroll.WeekCalendarView;
import com.yuyang.messi.view.scroll.month_view.MonthPager;
import com.yuyang.messi.view.scroll.month_view.MonthView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class CalendarActivity extends AppBaseActivity {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat sdfYM = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
    private final HashSet<String> weekDataSet = new HashSet<>();
    private final HashSet<String> monthDataSet = new HashSet<>();

    private Button pickBtn;
    private WeekCalendarView weekCalendarView;

    private TextView weekCalendarPagerShowText;
    private WeekCalendarPager weekCalendarPager;

    private TextView monthCalendarPagerShowText;
    private ImageView prevMonthImage;
    private ImageView nextMonthImage;

    private MonthPager<String> monthPager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_calendar;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("Calendar");

        pickBtn = findViewById(R.id.activity_calendar_calendar_pick);
        weekCalendarView = findViewById(R.id.activity_calendar_weekCalendarView);
        weekCalendarPagerShowText = findViewById(R.id.activity_calendar_weekCalendarPager_show);
        weekCalendarPager = findViewById(R.id.activity_calendar_weekCalendarPager);
        monthCalendarPagerShowText = findViewById(R.id.activity_calendar_month_text);
        prevMonthImage = findViewById(R.id.activity_calendar_month_prev);
        nextMonthImage = findViewById(R.id.activity_calendar_month_next);

        Calendar calendar = Calendar.getInstance();
        weekCalendarPagerShowText.setText(calendar.get(Calendar.YEAR) + "年\n" + (calendar.get(Calendar.MONTH) + 1) + "月");

        monthCalendarPagerShowText.setText(sdfYM.format(calendar.getTime()));

        monthPager = findViewById(R.id.activity_calendar_monthPager);
        monthPager.init(new MonthView.OnItemViewListener<String>() {
            @Override
            public int onCreateView() {
                return R.layout.view_month_view_recycler_item;
            }

            @Override
            public void onBindView(View dayView, int pos, Day<String> day, int monthDiff) {
                TextView dayText = dayView.findViewById(R.id.view_month_view_recycler_item_day);
                ImageView flagIcon = dayView.findViewById(R.id.view_month_view_recycler_item_icon);

                if (monthDiff == 0) {   //当前月
                    dayText.setText(String.valueOf(day.getCalendar().get(Calendar.DAY_OF_MONTH)));

                    if (monthDataSet.contains(sdf.format(day.getCalendar().getTime()))) {
                        flagIcon.setVisibility(View.VISIBLE);
                    } else {
                        flagIcon.setVisibility(View.INVISIBLE);
                    }

                    if (TextUtils.equals(sdf.format(day.getCalendar().getTime()), sdf.format(Calendar.getInstance().getTime()))) {
                        dayText.setBackgroundResource(R.drawable.oval_theme);
                        dayText.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                    } else {
                        dayText.setBackground(null);
                        dayText.setTextColor(ContextCompat.getColor(getActivity(), R.color.textPrimary));
                    }

                } else {
                    dayText.setText(null);
                    flagIcon.setVisibility(View.INVISIBLE);
                    dayText.setBackground(null);
                    dayText.setTextColor(ContextCompat.getColor(getActivity(), R.color.textPrimary));
                }
            }

            @Override
            public void onDayClick(Day<String> day, int monthDiff) {
                ToastUtil.showToast(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(day.getCalendar().getTime()));
            }
        });
        monthPager.setOnMCPListener(new MonthPager.DefaultOnMCPListener() {
            @Override
            public void onMonthShow(Calendar calendar) {
                monthCalendarPagerShowText.setText(sdfYM.format(calendar.getTime()));
            }

            @Override
            public void onInstantiateItem(Calendar calendar) {
                for (int i = 0; i < 5; i++) {
                    calendar.set(Calendar.DAY_OF_MONTH, new Random().nextInt(calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 1) + 1);//Calendar.SUNDAY ~ Calendar.SATURDAY
                    String date = sdf.format(calendar.getTime());
                    if (!monthDataSet.contains(date)) {
                        monthDataSet.add(date);
                    }
                }
                monthPager.refreshUI();
            }

            @Override
            public void onDestroyItem(Calendar calendar) {
                String ym = sdfYM.format(calendar.getTime());
                Set<String> deleteSet = new HashSet<>();
                for (String s : monthDataSet) {
                    if (s.startsWith(ym)) {
                        deleteSet.add(s);
                    }
                }
                monthDataSet.removeAll(deleteSet);
            }
        });
    }

    private void initEvent() {
        pickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerView.pickDate(getActivity(), pickBtn, new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()), new DatePickerView.OnPickListener() {
                    @Override
                    public void onPick(Calendar calendar) {
                        weekCalendarView.selectDay((Calendar) calendar.clone());
                        weekCalendarPager.selectDay((Calendar) calendar.clone());
                        monthPager.selectDay((Calendar) calendar.clone());
                    }
                });
            }
        });

        weekCalendarView.setOnDayClickListener(new WeekCalendarView.OnDayClickListener() {
            @Override
            public void onDayClick(Calendar calendar) {
                ToastUtil.showToast(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime()));
            }
        });

        weekCalendarPager.setOnWeekCalendarPagerListener(new WeekCalendarPager.OnWeekCalendarPagerListener() {
            @Override
            public void onWeekShow(Calendar calendar) {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                weekCalendarPagerShowText.setText(calendar.get(Calendar.YEAR) + "年\n" + (calendar.get(Calendar.MONTH) + 1) + "月");
            }

            @Override
            public void onDayClick(Calendar calendar) {
                ToastUtil.showToast(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime()));
            }

            @Override
            public void onInstantiateItem(Calendar calendar) {
                synchronized (weekDataSet) {
                    for (int i = 0; i < 3; i++) {
                        calendar.set(Calendar.DAY_OF_WEEK, new Random().nextInt(6) + 1);//Calendar.SUNDAY ~ Calendar.SATURDAY
                        String date = sdf.format(calendar.getTime());
                        if (!weekDataSet.contains(date)) {
                            weekDataSet.add(date);
                        }
                    }
                    weekCalendarPager.setShowFlag(weekDataSet);
                }
            }

            @Override
            public void onDestroyItem(Calendar calendar) {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                String startDate = sdf.format(calendar.getTime());
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                String endDate = sdf.format(calendar.getTime());

                Set<String> deleteSet = new HashSet<>();
                for (String s : weekDataSet) {
                    if (s.compareTo(startDate) >= 0 && s.compareTo(endDate) <= 0) {
                        deleteSet.add(s);
                    }
                }
                weekDataSet.removeAll(deleteSet);
            }
        });

        prevMonthImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthPager.setCurrentItem(monthPager.getCurrentItem() - 1);
            }
        });

        nextMonthImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthPager.setCurrentItem(monthPager.getCurrentItem() + 1);
            }
        });
    }
}
