package com.yuyang.messi.ui.card;

import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yuyang.lib_base.ui.base.BaseFragment;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.GankBean;
import com.yuyang.messi.net.okhttp.OkHttpUtil;
import com.yuyang.messi.net.okhttp.callback.StringCallback;
import com.yuyang.messi.net.retrofit.Apis;
import com.yuyang.messi.ui.card.common.CardLayoutManager;
import com.yuyang.messi.ui.card.common.TanTanCallback;
import com.yuyang.messi.utils.SnackBarUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class MeinvFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private MeinvRecyclerAdapter recyclerAdapter;

    private List<GankBean> cardBeanList = new ArrayList<>();

    private int page = 1;
    private int rows = 10;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_card;
    }

    @Override
    public void doOnViewCreated() {
        initViews();
        initEvents();
        loadData();
    }

    private void initViews() {
        $(R.id.fragment_card_filterBtn).setVisibility(View.GONE);
        recyclerView = $(R.id.fragment_card_recyclerView);

        recyclerView.setLayoutManager(new CardLayoutManager());
        recyclerView.setAdapter(recyclerAdapter = new MeinvRecyclerAdapter(getActivity()));
        recyclerAdapter.beanList = cardBeanList;

        ItemTouchHelper.Callback callback = new TanTanCallback(recyclerAdapter, cardBeanList, new TanTanCallback.CardListener() {
            @Override
            public void onPraise(Object object) {
                if (cardBeanList.size() < 8) {
                    page++;
                    loadData();
                }
            }

            @Override
            public void onPass(Object object) {
                if (cardBeanList.size() < 8) {
                    page++;
                    loadData();
                }
            }

            @Override
            public void onReport(Object object) {
                if (cardBeanList.size() < 8) {
                    page++;
                    loadData();
                }
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void initEvents() {

        recyclerAdapter.setOnCardListener(new MeinvRecyclerAdapter.OnCardListener() {
            @Override
            public void onCardClick(GankBean gankBean) {
            }
        });
    }

    private void loadData() {
        OkHttpUtil
                .get()
                .url(getUrl())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        SnackBarUtil.makeShort(getContentView(), e.getMessage()).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            boolean error = jsonResponse.getBoolean("error");
                            if (error) {
                                ToastUtil.showToast(response);
                                return;
                            }

                            List<GankBean> beanList = new Gson().fromJson(jsonResponse.getString("results"), new TypeToken<List<GankBean>>() {
                            }.getType());

                            if (beanList != null) {
                                if (page == 1) {
                                    cardBeanList.clear();
                                }
                                cardBeanList.addAll(beanList);
                            }
                            recyclerAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.showToast(e.getMessage());
                        }
                    }
                });
    }

    private String getUrl() {
        return Apis.GanHuo + "/福利/" + rows + "/" + page;
    }

}
