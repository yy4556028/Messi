package com.yuyang.lib_base.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 * For popup window list view adjust width
 * pop window 使用 list view  宽度会 match_parent
 * Created by yuyang on 2015/11/18.
 */
public class ListViewAdapterWidth extends ListView {

    public ListViewAdapterWidth(Context context) {
        super(context);
    }

    public ListViewAdapterWidth(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewAdapterWidth(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = measureWidthByChilds() + getPaddingLeft() + getPaddingRight();
        super.onMeasure(MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
    }

    private int measureWidthByChilds() {

        int maxWidth = 0;
        View view = null;
        for (int i=0; i<getAdapter().getCount(); i++) {
            view = getAdapter().getView(i, view, this);
            view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            if (view.getMeasuredWidth() > maxWidth) {
                maxWidth = view.getMeasuredWidth();
            }
        }
        return maxWidth;
    }
}
