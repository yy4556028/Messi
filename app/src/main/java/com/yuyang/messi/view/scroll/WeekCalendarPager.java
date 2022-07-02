package com.yuyang.messi.view.scroll;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuyang.messi.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class WeekCalendarPager extends ViewPager {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private static final int TOTAL_PAGE_COUNT = Integer.MAX_VALUE;

    static final String[] weeks = new String[]{"周日", "周一", "周二", "周三", "周四", "周五", "周六",};

    private Calendar focusCalendar;
    private int focusPageIndex;

    private Set<String> dateSet = new HashSet<>();

    public WeekCalendarPager(Context context) {
        this(context, null);
    }

    public WeekCalendarPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Calendar getFocusCalendar() {
        return (Calendar) focusCalendar.clone();
    }

    private void init() {
        setAdapter(new WeekAdapter());
        setCurrentItem(focusPageIndex = Integer.MAX_VALUE / 2, false);
        focusCalendar = Calendar.getInstance();
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (onWeekCalendarPagerListener != null) {
                    Calendar calendar = (Calendar) focusCalendar.clone();
                    calendar.add(Calendar.WEEK_OF_YEAR, position - focusPageIndex);
                    onWeekCalendarPagerListener.onWeekShow(calendar);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class WeekAdapter extends PagerAdapter {

        private List<WeekView> recyclerList = new ArrayList<>();

        @Override
        public int getCount() {
            return TOTAL_PAGE_COUNT;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            WeekView weekView;
            if (recyclerList.size() == 0) {
                weekView = new WeekView(getContext());
                weekView.setOnPageItemClickListener(onPageItemClickListener);
            } else {
                weekView = recyclerList.remove(recyclerList.size() - 1);
            }
            container.addView(weekView);
            weekView.updateView(position);

            if (onWeekCalendarPagerListener != null) {
                onWeekCalendarPagerListener.onInstantiateItem((Calendar) weekView.itemCalendar.clone());
            }
            return weekView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            WeekView weekView = (WeekView) object;
            container.removeView(weekView);
            recyclerList.add(weekView);
            if (onWeekCalendarPagerListener != null) {
                onWeekCalendarPagerListener.onDestroyItem((Calendar) weekView.itemCalendar.clone());
            }
        }
    }

    private class WeekView extends LinearLayout {

        private View[] lyt = new View[7];
        private TextView[] weekTextArr = new TextView[7];
        private TextView[] dayTextArr = new TextView[7];
        private ImageView[] flagImage = new ImageView[7];

        private OnClickListener listener;
        private OnPageItemClickListener onPageItemClickListener;
        private Calendar itemCalendar;
        private int position = -1;

        public WeekView(Context context) {
            super(context);
            LayoutInflater.from(context).inflate(R.layout.view_week_calendar_pager_view, this);
            listener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewGroup viewGroup = (ViewGroup) getChildAt(0);
                    int index = viewGroup.indexOfChild(v);
                    if (index >= 0 && onPageItemClickListener != null) {
                        onPageItemClickListener.onPageItemClick(itemCalendar, index);
                    }
                }
            };
            initView();
        }

        private void initView() {
            ViewGroup viewGroup = (ViewGroup) getChildAt(0);
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                lyt[i] = viewGroup.getChildAt(i).findViewById(R.id.view_week_calendar_pager_item_lyt);
                weekTextArr[i] = (TextView) viewGroup.getChildAt(i).findViewById(R.id.view_week_calendar_pager_item_week);
                dayTextArr[i] = (TextView) viewGroup.getChildAt(i).findViewById(R.id.view_week_calendar_pager_item_day);
                flagImage[i] = (ImageView) viewGroup.getChildAt(i).findViewById(R.id.view_week_calendar_pager_item_icon);
                weekTextArr[i].setText(weeks[i]);

                viewGroup.getChildAt(i).setOnClickListener(listener);
            }
        }

        public void updateView(int position) {
            this.position = position;
            itemCalendar = (Calendar) focusCalendar.clone();
            itemCalendar.add(Calendar.WEEK_OF_YEAR, position - focusPageIndex);
            itemCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            Calendar tempCalendar = (Calendar) itemCalendar.clone();
            for (int i = 0; i < 7; i++) {
                tempCalendar.set(Calendar.DAY_OF_WEEK, i + 1);
                dayTextArr[i].setText(String.valueOf(tempCalendar.get(Calendar.DAY_OF_MONTH)));

                if (dateSet != null && dateSet.contains(sdf.format(tempCalendar.getTime()))) {
                    flagImage[i].setVisibility(VISIBLE);
                } else {
                    flagImage[i].setVisibility(INVISIBLE);
                }

                int index = getDayDiff(tempCalendar, focusCalendar);
                if (index == 0) {
                    lyt[i].setBackgroundResource(R.drawable.week_calendar_center);
                    weekTextArr[i].setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    dayTextArr[i].setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                } else if (1 == Math.abs(index)) {
                    lyt[i].setBackgroundResource(index > 0 ? R.drawable.week_calendar_left : R.drawable.week_calendar_right);

                    if (sdf.format(Calendar.getInstance().getTime()).equals(sdf.format(tempCalendar.getTime()))) {//如果今天
                        weekTextArr[i].setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                        dayTextArr[i].setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                    } else {
                        weekTextArr[i].setTextColor(ContextCompat.getColor(getContext(), R.color.textSecondary));
                        dayTextArr[i].setTextColor(ContextCompat.getColor(getContext(), R.color.textSecondary));
                    }
                } else {
                    lyt[i].setBackgroundResource(0);


                    if (sdf.format(Calendar.getInstance().getTime()).equals(sdf.format(tempCalendar.getTime()))) {//如果今天
                        weekTextArr[i].setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                        dayTextArr[i].setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                    } else {
                        weekTextArr[i].setTextColor(ContextCompat.getColor(getContext(), R.color.textSecondary));
                        dayTextArr[i].setTextColor(ContextCompat.getColor(getContext(), R.color.textSecondary));
                    }
                }
            }
        }

        public void setOnPageItemClickListener(OnPageItemClickListener onPageItemClickListener) {
            this.onPageItemClickListener = onPageItemClickListener;
        }
    }

    public interface OnWeekCalendarPagerListener {
        void onWeekShow(Calendar calendar);

        void onDayClick(Calendar calendar);

        void onInstantiateItem(Calendar calendar);

        void onDestroyItem(Calendar calendar);
    }

    private OnWeekCalendarPagerListener onWeekCalendarPagerListener;

    public void setOnWeekCalendarPagerListener(OnWeekCalendarPagerListener onWeekCalendarPagerListener) {
        this.onWeekCalendarPagerListener = onWeekCalendarPagerListener;
    }

    private interface OnPageItemClickListener {
        void onPageItemClick(Calendar itemCalendar, int index);
    }

    private OnPageItemClickListener onPageItemClickListener = new OnPageItemClickListener() {
        @Override
        public void onPageItemClick(Calendar itemCalendar, int index) {

//            focusPageIndex += getWeekDiff(focusCalendar, itemCalendar);
            focusPageIndex = getCurrentItem();
            focusCalendar.setTime(itemCalendar.getTime());
            focusCalendar.set(Calendar.DAY_OF_WEEK, index + 1);
            refreshUI();

            if (onWeekCalendarPagerListener != null) {
                onWeekCalendarPagerListener.onDayClick((Calendar) focusCalendar.clone());
            }
        }
    };

    public void setShowFlag(Set<String> dateSet) {
        this.dateSet.clear();
        this.dateSet.addAll(dateSet);
        refreshUI();
    }

    public void setShowFlagSingle(Set<String> dateSet, String yyyyMMdd) {
        if (dateSet == null || dateSet.size() == 0) return;
        this.dateSet.addAll(dateSet);
        for (int i = 0; i < getChildCount(); i++) {
            WeekView weekView = (WeekView) getChildAt(i);
            if (sdf.format(weekView.itemCalendar.getTime()).substring(0, 10).equals(yyyyMMdd)) {
                weekView.updateView(weekView.position);
                break;
            }
        }
    }

    public void selectDay(Calendar calendar) {
        focusPageIndex = getCurrentItem();
        focusCalendar.setTime(calendar.getTime());

        List<String> instantiateList = new ArrayList<>();
        List<String> destroyList = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            WeekView weekView = (WeekView) getChildAt(i);
            destroyList.add(sdf.format(weekView.itemCalendar.getTime()));
        }
        refreshUI();
        for (int i = 0; i < getChildCount(); i++) {
            WeekView weekView = (WeekView) getChildAt(i);
            if (destroyList.contains(sdf.format(weekView.itemCalendar.getTime()))) {
                destroyList.remove(sdf.format(weekView.itemCalendar.getTime()));
            } else {
                instantiateList.add(sdf.format(weekView.itemCalendar.getTime()));
            }
        }

        if (onWeekCalendarPagerListener != null) {
            onWeekCalendarPagerListener.onWeekShow(calendar);
            try {
                for (String s : instantiateList) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(sdf.parse(s));
                    onWeekCalendarPagerListener.onInstantiateItem(cal);
                }
                for (String s : destroyList) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(sdf.parse(s));
                    onWeekCalendarPagerListener.onDestroyItem(cal);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void refreshUI() {
        for (int i = 0; i < getChildCount(); i++) {
            WeekView weekView = (WeekView) getChildAt(i);
            weekView.updateView(weekView.position);
        }
    }

    private int getDayDiff(Calendar calendar1, Calendar calendar2) {
        Calendar cal1 = (Calendar) calendar1.clone();
        Calendar cal2 = (Calendar) calendar2.clone();
        cal1.set(Calendar.HOUR, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        cal2.set(Calendar.HOUR, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);

        return (int) ((cal2.getTimeInMillis() - cal1.getTimeInMillis()) / (1000 * 3600 * 24));
    }

}
