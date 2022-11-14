package com.yuyang.messi.view.scroll.month_view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.yuyang.messi.utils.CalendarUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MonthPager<T> extends ViewPager {

    private Calendar currentCalendar;//防止页面中时间过了12点
    private int currentMonthIndex;
    private int totalCount;
    private MonthView.OnItemViewListener<T> onItemViewListener;

    public MonthPager(Context context) {
        this(context, null);
    }

    public MonthPager(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if (h > height) {
                height = h;
            }
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void init(MonthView.OnItemViewListener<T> onItemViewListener) {
        init(1970, 2100, Calendar.getInstance(), onItemViewListener);
    }

    public void init(int startYear, int endYear, final Calendar currentCalendar, MonthView.OnItemViewListener<T> onItemViewListener) {
        this.currentMonthIndex = (currentCalendar.get(Calendar.YEAR) - startYear) * 12 + currentCalendar.get(Calendar.MONTH);
        this.totalCount = (endYear - startYear + 1) * 12;
        this.onItemViewListener = onItemViewListener;
        this.currentCalendar = currentCalendar;

        setOffscreenPageLimit(1);
        setAdapter(new MonthAdapter());
        setCurrentItem(currentMonthIndex);
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (onMCPListener != null) {
                    Calendar calendar = (Calendar) currentCalendar.clone();
                    calendar.add(Calendar.MONTH, position - currentMonthIndex);
                    onMCPListener.onMonthShow(calendar);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void selectDay(Calendar calendar) {
        Calendar currentCal = (Calendar) currentCalendar.clone();
        final int newItemIndex = currentMonthIndex + CalendarUtil.getMonthDiff(currentCal, calendar);
        setCurrentItem(newItemIndex, false);
    }

    public void refreshUI() {
        for (int i = 0; i < getChildCount(); i++) {
            MonthView<T> monthView = (MonthView<T>) getChildAt(i);
            monthView.updateMonth();
        }
    }

    private class MonthAdapter extends PagerAdapter {

        private List<MonthView<T>> recyclerList = new ArrayList<>();

        @Override
        public int getCount() {
            return totalCount;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            MonthView<T> monthView;
            if (recyclerList.size() == 0) {
                monthView = new MonthView<>(container.getContext());
                monthView.setOnItemViewListener(onItemViewListener);
            } else {
                monthView = recyclerList.remove(recyclerList.size() - 1);
            }
            container.addView(monthView);

            Calendar calendar = (Calendar) currentCalendar.clone();
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.MONTH, position - currentMonthIndex);

            monthView.updateCalendar(calendar);

            if (onMCPListener != null) {
                onMCPListener.onInstantiateItem((Calendar) monthView.currentMonthCalendar.clone());
            }
            return monthView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            MonthView<T> monthView = (MonthView<T>) object;
            container.removeView(monthView);
            recyclerList.add(monthView);
            if (onMCPListener != null) {
                onMCPListener.onDestroyItem((Calendar) monthView.currentMonthCalendar.clone());
            }
        }
    }

    public interface OnMCPListener {
        void onMonthShow(Calendar calendar);

        void onInstantiateItem(Calendar calendar);

        void onDestroyItem(Calendar calendar);
    }

    public interface DefaultOnMCPListener extends OnMCPListener {
        @Override
        default void onMonthShow(Calendar calendar) {
        }

        @Override
        default void onInstantiateItem(Calendar calendar) {
        }

        @Override
        default void onDestroyItem(Calendar calendar) {
        }
    }

    private OnMCPListener onMCPListener;

    public void setOnMCPListener(OnMCPListener onMCPListener) {
        this.onMCPListener = onMCPListener;
    }
}
