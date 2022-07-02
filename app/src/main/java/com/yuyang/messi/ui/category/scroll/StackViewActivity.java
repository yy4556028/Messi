package com.yuyang.messi.ui.category.scroll;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.StackView;

import androidx.core.content.ContextCompat;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

import java.util.ArrayList;
import java.util.List;


public class StackViewActivity extends AppBaseActivity {

    private StackView stackView;
    private MyAdapter myAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_stackview;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showTitle("StackView");
        headerLayout.showLeftBackButton();

        stackView = findViewById(R.id.activity_stackview_stackView);
        List<Integer> imageList = new ArrayList<>();
        imageList.add(ContextCompat.getColor(getActivity(), R.color.red));
        imageList.add(ContextCompat.getColor(getActivity(), R.color.yellow));
        imageList.add(ContextCompat.getColor(getActivity(), R.color.blue));
        imageList.add(ContextCompat.getColor(getActivity(), R.color.gray));
        imageList.add(ContextCompat.getColor(getActivity(), R.color.green));
        imageList.add(ContextCompat.getColor(getActivity(), R.color.purple));
        imageList.add(ContextCompat.getColor(getActivity(), R.color.pink));
        stackView.setAdapter(myAdapter = new MyAdapter(imageList));
    }

    private void initEvent() {
    }

    private class MyAdapter extends BaseAdapter{

        private List<Integer> beanList;

        public MyAdapter(List<Integer> beanList) {
            this.beanList = beanList;
        }

        @Override
        public int getCount() {
            return beanList.size();
        }

        @Override
        public Object getItem(int position) {
            return beanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = new View(getActivity());
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtils.dp2px(80)));
            view.setBackgroundColor(beanList.get(position));
            return view;
        }
    }
}
