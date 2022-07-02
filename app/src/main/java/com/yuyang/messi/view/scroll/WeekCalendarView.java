package com.yuyang.messi.view.scroll;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuyang.messi.R;
import com.yuyang.messi.utils.CalendarUtil;

import java.util.Calendar;

public class WeekCalendarView extends RecyclerView {

    private boolean todayIsEnd = true;//今天是否最后一项

    static final String[] weeks = new String[]{"周日", "周一", "周二", "周三", "周四", "周五", "周六",};

    private int focusIndex;

    public WeekCalendarView(Context context) {
        this(context, null);
    }

    public WeekCalendarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeekCalendarView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutManager(new LinearLayoutManager(getContext(), HORIZONTAL, false));
        setAdapter(new WeekAdapter());
        getLayoutManager().scrollToPosition(focusIndex = Integer.MAX_VALUE / 2);
    }

    private class WeekAdapter extends Adapter<WeekAdapter.MyHolder> {

        private LayoutInflater mInflater;

        WeekAdapter() {
            mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyHolder(mInflater.inflate(R.layout.view_week_calendar_recycler_item, parent, false));
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {

            holder.calendar.setTimeInMillis(System.currentTimeMillis());
            holder.calendar.add(Calendar.DAY_OF_MONTH, position - Integer.MAX_VALUE / 2);

            holder.weekText.setText(weeks[holder.calendar.get(Calendar.DAY_OF_WEEK) - 1]);
            holder.dayText.setText(String.valueOf(holder.calendar.get(Calendar.DAY_OF_MONTH)));

            if (position == focusIndex) {
                holder.dayText.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                holder.dayText.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            } else if (1 == Math.abs(position - focusIndex)) {
                holder.dayText.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                holder.dayText.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            } else {
                holder.dayText.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent));
                holder.dayText.setTextColor(ContextCompat.getColor(getContext(), R.color.textSecondary));
            }
        }

        @Override
        public int getItemCount() {
            return todayIsEnd ? Integer.MAX_VALUE / 2 + 1 : Integer.MAX_VALUE;
        }

        class MyHolder extends ViewHolder {
            TextView weekText;
            TextView dayText;
            Calendar calendar;
            OnClickListener onClickListener;

            public MyHolder(View itemView) {
                super(itemView);
                weekText = (TextView) itemView.findViewById(R.id.view_week_calendar_recycler_item_week);
                dayText = (TextView) itemView.findViewById(R.id.view_week_calendar_recycler_item_day);
                calendar = Calendar.getInstance();
                onClickListener = new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        focusIndex = getAdapterPosition();
                        notifyDataSetChanged();
                        if (listener != null) {
                            listener.onDayClick(calendar);
                        }
                    }
                };
                itemView.setOnClickListener(onClickListener);
            }
        }
    }

    public interface OnDayClickListener {
        void onDayClick(Calendar calendar);
    }

    private OnDayClickListener listener;

    public void setOnDayClickListener(OnDayClickListener listener) {
        this.listener = listener;
    }

    public void selectDay(Calendar calendar) {
        focusIndex = Integer.MAX_VALUE / 2 + CalendarUtil.getDayDiff(Calendar.getInstance(), calendar);
        getAdapter().notifyDataSetChanged();
        getLayoutManager().scrollToPosition(focusIndex);
    }

}
