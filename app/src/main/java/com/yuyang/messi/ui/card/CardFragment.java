package com.yuyang.messi.ui.card;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yuyang.lib_base.ui.base.BaseFragment;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.tngou.LiveBean;
import com.yuyang.messi.net.okhttp.OkHttpUtil;
import com.yuyang.messi.net.okhttp.callback.StringCallback;
import com.yuyang.messi.net.retrofit.Apis;
import com.yuyang.messi.ui.card.common.CardLayoutManager;
import com.yuyang.messi.ui.card.common.TanTanCallback;
import com.yuyang.messi.utils.SnackBarUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class CardFragment extends BaseFragment {

    private CardRecyclerAdapter recyclerAdapter;

    private final List<LiveBean> cardBeanList = new ArrayList<>();

    private int page = 1;

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
        RecyclerView recyclerView = findViewById(R.id.fragment_card_recyclerView);

        recyclerView.setLayoutManager(new CardLayoutManager());
        recyclerView.setAdapter(recyclerAdapter = new CardRecyclerAdapter(requireActivity()));
        recyclerAdapter.beanList = cardBeanList;

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

        recyclerAdapter.setOnCardListener(new CardRecyclerAdapter.OnCardListener() {
            @Override
            public void onCardClick(LiveBean liveBean) {
//                loadDetail(liveBean);
            }
        });
    }

    private void loadData() {
        OkHttpUtil
                .get()
                .url(Apis.live_url)
                .addParams("lon", "0.0")
                .addParams("lat", "0.0")
                .addParams("type", "1")
                .addParams("page", String.valueOf(page))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        SnackBarUtil.makeShort(getContentView(), e.getMessage()).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            List<LiveBean> beanList = new Gson().fromJson(new JSONObject(response).optJSONObject("data").getString("list"), new TypeToken<List<LiveBean>>() {
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

//    private void loadDetail(LiveBean liveBean) {
//        OkHttpUtil
//                .get()
//                .url(Apis.tn_imageShow)
//                .addParams("id", liveBean.getId())
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        SnackBarUtil.makeShort(getContentView(), e.getMessage());
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        try {
//                            GalleryDetailBean galleryDetailBean = FastJsonTool.getSingle(response, GalleryDetailBean.class);
//
//                            if (galleryDetailBean != null) {
//                                CardGalleryActivity.goMyActivity(getActivity(), galleryDetailBean);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            ToastUtil.showToast(e.getMessage());
//                        }
//                    }
//                });
//    }

}
