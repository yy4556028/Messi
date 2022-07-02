package com.yuyang.messi.view.scroll;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class CustomGridLayoutManager extends GridLayoutManager {

    private int mMeasuredDiemension[] = new int[2];

    public CustomGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CustomGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public CustomGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        final int widthMode = View.MeasureSpec.getMode(widthSpec);
        final int heightMode = View.MeasureSpec.getMode(heightSpec);
        final int widthSize = View.MeasureSpec.getSize(widthSpec);
        final int heightSize = View.MeasureSpec.getSize(heightSpec);
        int width = 0;
        int height = 0;
        int count = getItemCount();
        int span = getSpanCount();

        for (int i = 0; i < count; i++) {
            measureScrapChild(recycler, i, View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED), View
                    .MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED), mMeasuredDiemension);
            if (getOrientation() == HORIZONTAL) {
                if (i % span == 0) {
                    width = width + mMeasuredDiemension[0];
                }
                if (i == 0) {
                    height = mMeasuredDiemension[1];
                }
            } else {
                if (i % span == 0) {
                    height = height + mMeasuredDiemension[1];
                }
                if (i == 0) {
                    height = mMeasuredDiemension[1];
                }
                if (i == 0) {
                    width = mMeasuredDiemension[0];
                }
            }
        }
        switch (widthMode) {
            case View.MeasureSpec.EXACTLY:
                width = widthSize;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
        }
        switch (heightMode) {
            case View.MeasureSpec.EXACTLY:
                height = heightSize;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
        }
        setMeasuredDimension(width, height);
    }

    private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec, int heightSpec, int[] measuredDiemension) {

        if (position < getItemCount()) {
            try {
                View view = recycler.getViewForPosition(0);//动态添加的时候报IndexOutOfBoundsException
                if (view != null) {
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
                    int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, getPaddingLeft() + getPaddingRight(), params.width);
                    int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, getPaddingBottom() + getPaddingTop(), params.height);
                    view.measure(childWidthSpec, childHeightSpec);
                    measuredDiemension[0] = view.getMeasuredWidth() + params.leftMargin + params.rightMargin;
                    measuredDiemension[1] = view.getMeasuredHeight() + params.bottomMargin + params.topMargin;
                    recycler.recycleView(view);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
