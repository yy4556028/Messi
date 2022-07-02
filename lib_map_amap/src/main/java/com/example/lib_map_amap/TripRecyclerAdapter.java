package com.example.lib_map_amap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lib_map_amap.bean.TripBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TripRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String type;

    private Context context;
    public List<TripBean> beanList = new ArrayList<>();

    private int markFlag;

    public TripRecyclerAdapter(Context context, String type) {
        this.context = context;
        this.type = type;
    }

    public void setData(List<TripBean> beanList, int markFlag) {
        this.beanList.clear();
        this.markFlag = markFlag;
        if (beanList != null) {
            if (markFlag == 0) {
                Set<String> weiboNewsnoSet = new HashSet<>();
                for (TripBean tripBean : beanList) {
                    if (tripBean.getSourceTag().equals("微博")) {
                        if (weiboNewsnoSet.contains(tripBean.getNewsno())) {
                            //do nothing
                        } else {
                            weiboNewsnoSet.add(tripBean.getNewsno());
                            this.beanList.add(tripBean);
                        }
                    } else {
                        this.beanList.add(tripBean);
                    }
                }
            } else {
                this.beanList.addAll(beanList);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.activity_trip_recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;

        final TripBean tripBean = beanList.get(position);

        myHolder.cityText.setText(tripBean.getArrivecity());

        if (tripBean.getDate().length() > 10) {
            myHolder.dateText.setText(tripBean.getDate().substring(0, 10));
        } else {
            myHolder.dateText.setText(tripBean.getDate());
        }

        myHolder.titleText.setText(tripBean.getNewstitle());

        if (position == beanList.size() - 1) {
            myHolder.line.setVisibility(View.GONE);
        } else {
            myHolder.line.setVisibility(View.VISIBLE);

            TripBean nextTripBean = beanList.get(position + 1);
            if (isDiffZone(tripBean, nextTripBean)) {
                myHolder.line.setBackgroundResource(R.color.theme);
                ((ViewGroup.MarginLayoutParams) (myHolder.line.getLayoutParams())).setMargins(0, 0, 0, 0);
            } else {
                myHolder.line.setBackgroundResource(R.color.gray);
                ((ViewGroup.MarginLayoutParams) (myHolder.line.getLayoutParams())).setMargins((int) (Resources.getSystem().getDisplayMetrics().density * 18), 0, 0, 0);
            }
        }

        myHolder.itemView.setBackgroundColor(getBgColorByComeTimes(tripBean));
    }

    /**
     * @param tripBean
     * @param nextTripBean
     * @return true 表示为同一次访问，false为不同次访问
     */
    private boolean isDiffZone(TripBean tripBean, TripBean nextTripBean) {
        if (markFlag == 0 && type.equals("0")) {//国内省
            return tripBean.getComeTimesProvince() > nextTripBean.getComeTimesProvince();
        } else if (markFlag == 0 && type.equals("1")) {//国外国家
            return tripBean.getComeTimesNation() > nextTripBean.getComeTimesNation();
        } else if (markFlag == 1) {//城市
            return tripBean.getComeTimesCity() > nextTripBean.getComeTimesCity();
        } else {//cannot go here
            return false;
        }
    }

    private int getBgColorByComeTimes(TripBean tripBean) {
        if (markFlag == 0 && type.equals("0")) {//国内省
            int maxComeTimes = beanList.get(0).getComeTimesProvince();
            return ((maxComeTimes - tripBean.getComeTimesProvince()) % 2 == 0) ? context.getResources().getColor(R.color.white) : Color.parseColor("#f4f4f4");
        } else if (markFlag == 0 && type.equals("1")) {//国外国家
            int maxComeTimes = beanList.get(0).getComeTimesNation();
            return ((maxComeTimes - tripBean.getComeTimesNation()) % 2 == 0) ? context.getResources().getColor(R.color.white) : Color.parseColor("#f4f4f4");
        } else if (markFlag == 1) {//城市
            int maxComeTimes = beanList.get(0).getComeTimesCity();
            return ((maxComeTimes - tripBean.getComeTimesCity()) % 2 == 0) ? context.getResources().getColor(R.color.white) : Color.parseColor("#f4f4f4");
        } else {//cannot go here
            return context.getResources().getColor(R.color.white);
        }
    }

    @Override
    public int getItemCount() {
        return beanList == null ? 0 : beanList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView cityText;
        private TextView dateText;
        private TextView titleText;
        private View line;
        View.OnClickListener onClickListener;

        MyHolder(View itemView) {
            super(itemView);
            cityText = (TextView) itemView.findViewById(R.id.activity_trip_recycler_item_cityText);
            dateText = (TextView) itemView.findViewById(R.id.activity_trip_recycler_item_dateText);
            titleText = (TextView) itemView.findViewById(R.id.activity_trip_recycler_item_titleText);
            line = itemView.findViewById(R.id.activity_trip_recycler_item_line);

            onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(beanList.get(getAdapterPosition()));
                    }
                }
            };
            itemView.setOnClickListener(onClickListener);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(TripBean tripBean);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
