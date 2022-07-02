package com.yuyang.messi.ui.category.scroll;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.adapter.SwipeRecyclerViewAdapter;
import com.yuyang.messi.ui.base.AppBaseActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Yamap on 2017/9/13.
 */

public class SwipeRecyclerViewActivity extends AppBaseActivity {

    private SwipeMenuRecyclerView recyclerView;
    private SwipeRecyclerViewAdapter adapter;
    private List<String> beanList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_swiperecyclerview;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("SwipeMenu");

        recyclerView = findViewById(R.id.activity_swiperecyclerView_recyclerView);
        recyclerView.setLongPressDragEnabled(true); // 开启拖拽。
        recyclerView.setItemViewSwipeEnabled(true);
        recyclerView.setAdapter(adapter = new SwipeRecyclerViewAdapter(getActivity()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setOnItemMoveListener(mItemMoveListener);// 监听拖拽，更新UI。

    }

    private void initEvent() {
    }

    private void initData() {
        beanList.add("");
        beanList.add("11");
        beanList.add("12");
        beanList.add("13");
        beanList.add("14");
        beanList.add("15");
        beanList.add("16");
        beanList.add("17");
        beanList.add("18");
        beanList.add("19");
        beanList.add("20");
        beanList.add("21");
        beanList.add("22");
        adapter.updateData(beanList);
    }

    OnItemMoveListener mItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
            int fromPosition = srcHolder.getAdapterPosition();
            int toPosition = targetHolder.getAdapterPosition();

            // Item被拖拽时，交换数据，并更新adapter。
            Collections.swap(beanList, fromPosition, toPosition);
            adapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
            int position = srcHolder.getAdapterPosition();
            // Item被侧滑删除时，删除数据，并更新adapter。
            beanList.remove(position);
            adapter.notifyItemRemoved(position);
        }
    };
}

