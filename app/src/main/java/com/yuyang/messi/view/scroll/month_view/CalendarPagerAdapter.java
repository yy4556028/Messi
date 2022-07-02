package com.yuyang.messi.view.scroll.month_view;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarPagerAdapter<T> extends PagerAdapter {

    private MonthView.OnItemViewListener<T> onItemViewListener;

    private List<MonthView<T>> recyclerList = new ArrayList<>();

    private int currentMonthIndex;
    private int totalCount;

    public CalendarPagerAdapter(MonthView.OnItemViewListener<T> onItemViewListener) {
        this(Integer.MAX_VALUE / 2, Integer.MAX_VALUE, onItemViewListener);
    }

    public CalendarPagerAdapter(int currentMonthIndex, int totalCount, MonthView.OnItemViewListener<T> onItemViewListener) {
        this.onItemViewListener = onItemViewListener;
        this.currentMonthIndex = currentMonthIndex;
        this.totalCount = totalCount;
    }

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

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, position - Integer.MAX_VALUE / 2);

        monthView.updateCalendar(calendar);
        return monthView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        MonthView<T> monthView = (MonthView<T>) object;
        container.removeView(monthView);
        recyclerList.add(monthView);
    }
}
