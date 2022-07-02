package com.yuyang.messi.ui.football;

import android.animation.LayoutTransition;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.yuyang.lib_base.recyclerview.item_decoration.LinearItemDecoration;
import com.yuyang.lib_base.ui.base.BaseFragment;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.football.FootballBean;
import com.yuyang.messi.net.okhttp.OkHttpUtil;
import com.yuyang.messi.net.okhttp.callback.StringCallback;
import com.yuyang.messi.net.retrofit.Apis;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.messi.utils.SnackBarUtil;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class FootballFragment extends BaseFragment {

    public static final String TITLE = "title";

    private String title;

    private TextView scoreTabText;
    private TextView goalTabText;

    private XRecyclerView recyclerView;

    private ScoreAdapter scoreAdapter;
    private GoalAdapter goalAdapter;

    private View header_score;
    private View header_goal;

    private FootballBean footballBean;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_football;
    }

    @Override
    protected void doOnViewCreated() {
        title = getArguments().getString(TITLE);
        initViews();
        initEvents();
        loadData();
        scoreTabText.performClick();
    }

    private void initViews() {
        scoreTabText = $(R.id.fragment_football_tab_score);
        goalTabText = $(R.id.fragment_football_tab_goal);
        recyclerView = $(R.id.fragment_football_recycleView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new LinearItemDecoration(getContext()));
        recyclerView.setPullRefreshEnabled(true);
        recyclerView.setLoadingMoreEnabled(false);

        header_score = View.inflate(getActivity(), R.layout.fragment_football_score_recycler_item, null);
        header_goal = View.inflate(getActivity(), R.layout.fragment_football_goal_recycler_item, null);
        FrameLayout header = new FrameLayout(getActivity());
        header.setLayoutParams(new ViewGroup.LayoutParams(CommonUtil.getScreenWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        header.addView(header_score);
        header.addView(header_goal);
        header.setBackgroundResource(R.color.gray);
        header.setLayoutTransition(new LayoutTransition());
        recyclerView.addHeaderView(header);
    }

    private void initEvents() {
        scoreTabText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scoreTabText.isSelected()) return;
                scoreTabText.setSelected(true);
                goalTabText.setSelected(false);

                header_goal.setVisibility(View.GONE);
                header_score.setVisibility(View.VISIBLE);

                recyclerView.setAdapter(scoreAdapter = new ScoreAdapter(getActivity()));
                if (footballBean != null) {
                    scoreAdapter.updateData(footballBean.getViews().getJifenbang());
                }
            }
        });
        goalTabText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goalTabText.isSelected()) return;
                scoreTabText.setSelected(false);
                goalTabText.setSelected(true);

                header_score.setVisibility(View.GONE);
                header_goal.setVisibility(View.VISIBLE);

                recyclerView.setAdapter(goalAdapter = new GoalAdapter(getActivity()));
                if (footballBean != null) {
                    goalAdapter.updateData(footballBean.getViews().getSheshoubang());
                }
            }
        });
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                loadData();
            }

            @Override
            public void onLoadMore() {

            }
        });
    }

    private void loadData() {
        OkHttpUtil
                .get()
                .url(Apis.FOOTBALL_LEAGUE)
                .addParams("key", Apis.API_JUHE_APPKEY)
                .addParams("league", title)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        SnackBarUtil.makeShort(getContentView(), e.getMessage());
                        recyclerView.refreshComplete();
                        recyclerView.loadMoreComplete();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            if (0 != jsonResponse.optInt("error_code")) {
                                ToastUtil.showToast(jsonResponse.optString("reason"));
                                return;
                            }

                            footballBean = new Gson().fromJson(jsonResponse.optString("result"), FootballBean.class);

                            if (scoreAdapter != null) {
                                scoreAdapter.updateData(footballBean.getViews().getJifenbang());
                            }

                            if (goalAdapter != null) {
                                goalAdapter.updateData(footballBean.getViews().getSheshoubang());
                            }

                            recyclerView.refreshComplete();
                            recyclerView.loadMoreComplete();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.showToast(e.getMessage());
                            recyclerView.refreshComplete();
                            recyclerView.loadMoreComplete();
                        }
                    }
                });
    }

}
