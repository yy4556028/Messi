package com.yuyang.messi.ui.home.taobao;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.messi.R;

import java.util.List;

public class AdvertRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    public List<String> beanList;

    public final int showCount = 2;

    public AdvertRecyclerAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<String> beanList) {
        this.beanList = beanList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0: {
                TextView textView = new TextView(parent.getContext());
                textView.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.textPrimary));
                textView.setTextSize(14);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parent.getMeasuredHeight() / showCount));
                return new MyHolder(textView);
            }
            default: {
                return new EmptyHolder(getEmptyView());
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (0 == getItemViewType(position)) {

            MyHolder myHolder = (MyHolder) holder;

            ((TextView) myHolder.itemView).setText(beanList.get(position % beanList.size()));

            int outRadius = PixelUtils.dp2px(2);
            float[] outRadii = {outRadius, outRadius, outRadius, outRadius, outRadius, outRadius, outRadius, outRadius};
            RoundRectShape roundRectShape = new RoundRectShape(outRadii, null, null);
            ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
            shapeDrawable.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        }
    }

    @Override
    public int getItemCount() {
        if (beanList == null || beanList.size() == 0) return 1;
        if (beanList.size() <= showCount) return beanList.size();
        return Integer.MAX_VALUE;
    }

    @Override
    public int getItemViewType(int position) {
        return (beanList == null || beanList.size() == 0) ? 1 : 0;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        MyHolder(TextView textView) {
            super(textView);
        }
    }

    public class EmptyHolder extends RecyclerView.ViewHolder {
        EmptyHolder(View itemView) {
            super(itemView);
        }
    }

    private View getEmptyView() {
        TextView textView = new TextView(context);
        ViewGroup.LayoutParams params = new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(params);
        textView.setTextColor(ContextCompat.getColor(context, R.color.textPrimary));
        textView.setTextSize(14);
        textView.setGravity(Gravity.CENTER);
        textView.setText("暂无数据");
        return textView;
    }
}
