package com.yamap.lib_chat.keyboard.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.yamap.lib_chat.R;

public class ChatPageView extends RelativeLayout {

    private GridView gridView;

    public GridView getGridView() {
        return gridView;
    }

    public ChatPageView(Context context) {
        this(context, null);
    }

    public ChatPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_page_view, this);
        gridView = (GridView) view.findViewById(R.id.chat_page_view_grid);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            gridView.setMotionEventSplittingEnabled(false);
        }
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setCacheColorHint(0);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setVerticalScrollBarEnabled(false);
    }

    public void setNumColumns(int row) {
        gridView.setNumColumns(row);
    }
}
