
package com.yuyang.messi.widget;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.yuyang.messi.R;

/**
 * Custom implementation of the MarkerView.
 * 
 * @author Philipp Jahoda
 */
public class ChartMarkerView extends MarkerView {

    private TextView textView;

    public ChartMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        textView = findViewById(R.id.widget_chart_marker_view_textView);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {
            CandleEntry ce = (CandleEntry) e;
            textView.setText(Utils.formatNumber(ce.getY(), 2, true));
        } else {

            textView.setText(Utils.formatNumber(e.getY(), 2, true));
        }
//        textView.setText(Utils.formatNumber(e.getX(), 0, true));
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-getMeasuredWidth(), -getMeasuredHeight());
    }
}
