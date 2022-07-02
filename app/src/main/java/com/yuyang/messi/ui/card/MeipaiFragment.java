package com.yuyang.messi.ui.card;

import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yuyang.lib_base.ui.base.BaseFragment;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.meipai.MeipaiBean;
import com.yuyang.messi.net.okhttp.OkHttpUtil;
import com.yuyang.messi.net.okhttp.callback.StringCallback;
import com.yuyang.messi.net.retrofit.Apis;
import com.yuyang.messi.ui.card.common.CardLayoutManager;
import com.yuyang.messi.ui.card.common.TanTanCallback;
import com.yuyang.messi.utils.SnackBarUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;

public class MeipaiFragment extends BaseFragment {

    private Button filterBtn;
    private MeipaiRecyclerAdapter recyclerAdapter;

    private List<MeipaiBean> cardBeanList = new ArrayList<>();

    private int page = 1;
    private final int count = 20;
    private int paramId = 1;
    private int selectIndex;
    private final String[] classArray = {"热门", "搞笑", "明星名人", "男神", "女神", "音乐", "舞蹈", "美食", "美妆", "宝宝", "萌宠", "手工", "穿秀", "吃秀"};
    private final List<Integer> idList = Arrays.asList(1, 13, 16, 31, 19, 62, 63, 59, 27, 18, 6, 450, 460, 423);

    @Override
    public int getLayoutId() {
        return R.layout.fragment_meipai;
    }

    @Override
    public void doOnViewCreated() {
        initViews();
        initEvents();
        loadData();
    }

    private void initViews() {
        filterBtn = $(R.id.fragment_meipai_filterBtn);
        RecyclerView recyclerView = $(R.id.fragment_meipai_recyclerView);

        recyclerView.setLayoutManager(new CardLayoutManager());
        recyclerView.setAdapter(recyclerAdapter = new MeipaiRecyclerAdapter(getActivity()));
        recyclerAdapter.beanList = cardBeanList;

        filterBtn.setText(classArray[0]);
        paramId = 1;

        ItemTouchHelper.Callback callback = new TanTanCallback(recyclerAdapter, cardBeanList, new TanTanCallback.CardListener() {
            @Override
            public void onPraise(Object object) {
                if (recyclerAdapter.mediaPlayer != null) {
                    recyclerAdapter.mediaPlayer.setOnCompletionListener(null);
                    recyclerAdapter.mediaPlayer.pause();
                    recyclerAdapter.mediaPlayer.stop();
                }
                if (cardBeanList.size() < 8) {
                    page++;
                    loadData();
                }
            }

            @Override
            public void onPass(Object object) {
                if (recyclerAdapter.mediaPlayer != null) {
                    recyclerAdapter.mediaPlayer.setOnCompletionListener(null);
                    recyclerAdapter.mediaPlayer.pause();
                    recyclerAdapter.mediaPlayer.stop();
                }
                if (cardBeanList.size() < 8) {
                    page++;
                    loadData();
                }
            }

            @Override
            public void onReport(Object object) {
                if (recyclerAdapter.mediaPlayer != null) {
                    recyclerAdapter.mediaPlayer.setOnCompletionListener(null);
                    recyclerAdapter.mediaPlayer.pause();
                    recyclerAdapter.mediaPlayer.stop();
                }
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

        recyclerAdapter.setOnCardListener(new MeipaiRecyclerAdapter.OnCardListener() {
            @Override
            public void onCardClick(MeipaiBean meipaiBean) {
            }
        });
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("选择分类")
                        .setSingleChoiceItems(classArray, idList.indexOf(paramId), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectIndex = which;
                            }
                        })
                        // Set the action buttons
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                paramId = idList.get(selectIndex);
                                filterBtn.setText(classArray[selectIndex]);
                                page = 1;
                                loadData();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();
            }
        });
    }

    private void loadData() {
        OkHttpUtil
                .get()
                .url(Apis.meipai_list)
                .addParams("id", String.valueOf(page))
                .addParams("page", String.valueOf(page))
                .addParams("count", String.valueOf(count))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        SnackBarUtil.makeShort(getContentView(), e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            List<MeipaiBean> beanList = new Gson().fromJson(response, new TypeToken<List<MeipaiBean>>() {
                            }.getType());

                            if (beanList != null) {
                                if (page == 1) {
                                    cardBeanList.clear();
                                }
                                cardBeanList.addAll(beanList);
                            }
                            recyclerAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
