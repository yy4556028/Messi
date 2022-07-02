package com.yuyang.messi.jetpack.paging;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.messi.R;
import com.yuyang.messi.kotlinui.beauty.BeautyBean;
import com.yuyang.lib_base.utils.CommonUtil;

public class PagedListFragment extends Fragment {

    private RecyclerView recyclerView;
    private PagedRecyclerAdapter pagedRecyclerAdapter;
    private LiveData<PagedList<BeautyBean>> beanList;

    private PageFragmentViewModel pageFragmentViewModel;

    public static PagedListFragment newInstance() {
        return new PagedListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.common_recycler, container, false);
        recyclerView = inflate.findViewById(R.id.common_recycler_recyclerView);
        return inflate;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pageFragmentViewModel = ViewModelProviders.of(this).get(PageFragmentViewModel.class);
        pageFragmentViewModel.getPagedList().observe(this, new Observer<PagedList<BeautyBean>>() {
            @Override
            public void onChanged(PagedList<BeautyBean> itemBeans) {
                pagedRecyclerAdapter.submitList(itemBeans);
            }
        });
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
                recyclerView.setLayoutManager(layoutManager);// 瀑布流
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                int itemWidth = ((CommonUtil.getScreenWidth() - PixelUtils.dp2px(16)) / ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).getSpanCount());
                pagedRecyclerAdapter = new PagedRecyclerAdapter(itemWidth);
                recyclerView.setAdapter(pagedRecyclerAdapter);
            }
        });
    }
}
