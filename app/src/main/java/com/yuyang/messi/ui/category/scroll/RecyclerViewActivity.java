package com.yuyang.messi.ui.category.scroll;

import android.os.Build;
import android.os.Bundle;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.adapter.RecyclerViewAdapter;
import com.yuyang.messi.recycler.RecyclerViewItemTouchHelperCallback;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.utils.TransitionUtil;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends AppBaseActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private ItemTouchHelper itemTouchHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recyclerview;
    }

    @Override
    protected void initTransition() {
        super.initTransition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionUtil.setEnterTransition(getActivity(), TransitionUtil.slideEnd, 1500);
            TransitionUtil.setReturnTransition(getActivity(), TransitionUtil.slideEnd, 1500);

            TransitionUtil.setExitTransition(getActivity(), TransitionUtil.slideStart, 1500);
            TransitionUtil.setReenterTransition(getActivity(), TransitionUtil.slideStart, 1500);
        }
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
        headerLayout.showTitle("列表");

        recyclerView = findViewById(R.id.activity_recyclerView_recyclerView);
        recyclerView.setAdapter(adapter = new RecyclerViewAdapter(getActivity()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        itemTouchHelper = new ItemTouchHelper(new RecyclerViewItemTouchHelperCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        adapter.itemTouchHelper = itemTouchHelper;
    }

    private void initEvent() {
    }

    private void initData() {
        List<DataBean> data = new ArrayList<>();
        data.add(new DataBean("滑动菜单List", SwipeMenuListViewActivity.class));
        data.add(new DataBean("滑动菜单RecyclerView", SwipeRecyclerViewActivity.class));
        data.add(new DataBean("GridLayoutManager", null));
        data.add(new DataBean("AutoGrowListView", AutoGrowListViewActivity.class));
        data.add(new DataBean("DragGrid", DragGridActivity.class));
        data.add(new DataBean("PinnedHeader", PinnedHeaderListActivity.class));
        data.add(new DataBean("Calendar", CalendarActivity.class));
        data.add(new DataBean("ScrollView", ScrollViewActivity.class));
        data.add(new DataBean("Folder", FolderActivity.class));
        data.add(new DataBean("FlowDrag", FlowDragActivity.class));
        data.add(new DataBean("StackOverView", StackViewActivity.class));
        adapter.updateData(data);
    }

    public static class DataBean {
        public String name;
        public Class intentClass;

        public DataBean(String name, Class intentClass) {
            this.name = name;
            this.intentClass = intentClass;
        }
    }
}
