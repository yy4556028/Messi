package com.yuyang.messi.ui.chat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yuyang.lib_base.ui.base.BaseFragment;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.chat.adapter.ChatListRecyclerAdapter;

import org.greenrobot.eventbus.EventBus;

public class ChatListFragment extends BaseFragment {

    private SwipeRefreshLayout refreshLayout;
    private ChatListRecyclerAdapter recyclerAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat_list;
    }

    @Override
    protected void doOnViewCreated() {
        refreshLayout = $(R.id.fragment_chat_list_refreshLayout);
        refreshLayout.setEnabled(false);

        RecyclerView recyclerView = $(R.id.fragment_chat_list_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerAdapter = new ChatListRecyclerAdapter(getActivity()));
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        updateList();
    }

}
