package com.yuyang.messi.view.scroll.month_view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyang.messi.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MonthView<T> extends RecyclerView {

    private final MonthViewRecyclerAdapter adapter;

    private final List<Day<T>> dayList = new ArrayList<>();

    public Calendar currentMonthCalendar;//默认本月第一天

    public MonthView(@NonNull Context context) {
        this(context, null);
    }

    public MonthView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        setLayoutManager(new GridLayoutManager(getContext(), 7));
        setAdapter(adapter = new MonthViewRecyclerAdapter(getContext()));
        for (int i = 0; i < 6 * 7; i++) {
            dayList.add(new Day<T>(Calendar.getInstance()));
        }
    }

    public List<Day<T>> getDayList() {
        return dayList;
    }

    public void updateCalendar(Calendar monthCalendar) {
        currentMonthCalendar = (Calendar) monthCalendar.clone();
        currentMonthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        currentMonthCalendar.getTime();//不加这句的话，clone得到的tempCalendar日期不是1
        Calendar tempCalendar = (Calendar) currentMonthCalendar.clone();
        tempCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        for (int i = 0; i < dayList.size(); i++) {
            Day<T> day = dayList.get(i);
            day.getCalendar().setTime(tempCalendar.getTime());
            day.setPageMonth(currentMonthCalendar.get(Calendar.MONTH));
            tempCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        adapter.notifyDataSetChanged();
    }

    public void updateMonth() {
        adapter.notifyDataSetChanged();
    }

    public void updateDay(Day<T> day) {
        int pos = dayList.indexOf(day);

        if (pos >= 0) {
            adapter.notifyItemChanged(pos);
        }
    }

    class MonthViewRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final Context context;
        private final LayoutInflater mInflater;

        MonthViewRecyclerAdapter(Context context) {
            this.context = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public MyBaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (onItemViewListener != null && onItemViewListener.onCreateView() > 0) {
                return new MyBaseHolder(mInflater.inflate(onItemViewListener.onCreateView(), parent, false));
            }
            return new MyBaseHolder(mInflater.inflate(R.layout.view_month_view_recycler_item, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (onItemViewListener != null && onItemViewListener.onCreateView() > 0) {
                Day<T> day = dayList.get(position);
                onItemViewListener.onBindView(holder.itemView, position, day, day.getCalendar().get(Calendar.MONTH) - currentMonthCalendar.get(Calendar.MONTH));
            } else {
                MyExampleHolder myHolder = (MyExampleHolder) holder;
                Day<T> day = dayList.get(position);

                if (day.getCalendar().get(Calendar.MONTH) == day.getPageMonth()) {
                    myHolder.dayText.setText(String.valueOf(day.getCalendar().get(Calendar.DAY_OF_MONTH)));
                    if (day.getBean() != null) {
                        myHolder.flagIcon.setVisibility(View.VISIBLE);
                    } else {
                        myHolder.flagIcon.setVisibility(View.INVISIBLE);
                    }

                    if (day.isFocus()) {
                        myHolder.dayText.setBackgroundResource(R.drawable.oval_theme);
                        myHolder.dayText.setTextColor(ContextCompat.getColor(context, R.color.white));
                    } else {
                        myHolder.dayText.setBackgroundResource(0);
                        myHolder.dayText.setTextColor(ContextCompat.getColor(context, R.color.textPrimary));
                    }
                } else {
                    myHolder.dayText.setText("");
                    myHolder.flagIcon.setVisibility(View.INVISIBLE);
                    myHolder.dayText.setBackgroundResource(0);
                    myHolder.dayText.setTextColor(ContextCompat.getColor(context, R.color.textPrimary));
                }
            }
        }

        @Override
        public int getItemCount() {
            return dayList == null ? 0 : dayList.size();
        }

        class MyBaseHolder extends RecyclerView.ViewHolder {

            MyBaseHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onItemViewListener != null) {
                            Day<T> day = dayList.get(getAdapterPosition());
                            onItemViewListener.onDayClick(day, day.getCalendar().get(Calendar.MONTH) - currentMonthCalendar.get(Calendar.MONTH));
                        }
                    }
                });
                itemView.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (onItemViewListener != null) {
                            Day<T> day = dayList.get(getAdapterPosition());
                            onItemViewListener.onDayLongClick(day, day.getCalendar().get(Calendar.MONTH) - currentMonthCalendar.get(Calendar.MONTH));
                        }
                        return true;
                    }
                });
            }
        }

        class MyExampleHolder extends MyBaseHolder {

            TextView dayText;
            ImageView flagIcon;

            MyExampleHolder(View itemView) {
                super(itemView);
                dayText = itemView.findViewById(R.id.view_month_view_recycler_item_day);
                flagIcon = itemView.findViewById(R.id.view_month_view_recycler_item_icon);
            }
        }
    }

    public abstract static class OnItemViewListener<T> {

        public int onCreateView() {
            return 0;
        }

        public void onBindView(View dayView, int pos, Day<T> day, int monthDiff) {
        }

        public void onDayClick(Day<T> day, int monthDiff) {
        }

        public void onDayLongClick(Day<T> day, int monthDiff) {
        }
    }

    private OnItemViewListener<T> onItemViewListener;

    public void setOnItemViewListener(OnItemViewListener<T> onItemViewListener) {
        this.onItemViewListener = onItemViewListener;
    }
}
