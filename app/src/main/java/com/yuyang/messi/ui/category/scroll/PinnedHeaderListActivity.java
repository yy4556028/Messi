package com.yuyang.messi.ui.category.scroll;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.messi.R;
import com.yuyang.messi.adapter.SectionedAdapter;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.view.scroll.pinnedsectionlistview.PinnedHeaderListView;

public class PinnedHeaderListActivity extends AppBaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pinned_header_list_view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("PinnedHeaderList");

        PinnedHeaderListView listView = findViewById(R.id.activity_pinned_header_listView);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < 3; i++) {
            TextView header = new TextView(this);
            header.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtils.dp2px(40)));
            header.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
            header.setText("Header " + i);
            listView.addHeaderView(header);
        }

        for (int i = 0; i < 3; i++) {
            TextView footer = new TextView(this);
            footer.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtils.dp2px(48)));
            footer.setTextColor(ContextCompat.getColor(getActivity(), R.color.purple));
            footer.setText("Footer " + i);
            listView.addFooterView(footer);
        }

        SectionedAdapter sectionedAdapter = new SectionedAdapter();
        listView.setAdapter(sectionedAdapter);
    }
}
