package com.yuyang.messi.ui.category;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.alibaba.fastjson.JSON;
import com.yamap.lib_chat.keyboard.utils.KeyboardUtil;
import com.yuyang.lib_base.config.Config;
import com.yuyang.lib_base.net.unify.HttpException;
import com.yuyang.lib_base.net.unify.HttpRequest;
import com.yuyang.lib_base.net.unify.HttpUtils;
import com.yuyang.lib_base.net.unify.RequestCallBack;
import com.yuyang.lib_base.net.unify.RequestParams;
import com.yuyang.lib_base.net.unify.ResponseInfo;
import com.yuyang.lib_base.recyclerview.item_decoration.GridItemDecoration;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.lib_base.utils.KeyboardTool;
import com.yuyang.lib_base.utils.PixelUtils;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.adapter.GifAdapter;
import com.yuyang.messi.bean.riffsy.RiffsyMediaBean;
import com.yuyang.messi.bean.riffsy.RiffsyResponseBean;
import com.yuyang.messi.threadPool.ThreadPool;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.ui.common.photo.PhotoShowActivity;
import com.yuyang.messi.utils.GifCacheUtil;
import com.yuyang.messi.view.GifView;
import com.yuyang.messi.view.Progress.CircleProgressBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//https://tenor.com/
//https://api.tenor.com/v1/random?q=excited
//https://api.tenor.com/v1/search?q=excited
public class GifActivity extends AppBaseActivity {

    private final int SPACE = PixelUtils.dp2px(16);

    private EditText searchEdit;
    private ImageView eraseAllButton;
    private RecyclerView recyclerView;
    private GifAdapter adapter;
    private StaggeredGridLayoutManager layoutManager;
    private ItemTouchHelper itemTouchHelper;

    private RelativeLayout progressLyt;

    private RelativeLayout gifLyt;
    private CircleProgressBar gifProgress;
    private GifView gifView;

    private int lastVisibleItem = -1;

    private String pos = null;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gif;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
        loadGif(null);
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("Gif");

        searchEdit = findViewById(R.id.activity_gif_searchEdit);
        eraseAllButton = findViewById(R.id.activity_gif_eraseView);
        recyclerView = findViewById(R.id.activity_gif_recyclerView);

        gifLyt = findViewById(R.id.activity_gif_dialogLyt);
        gifProgress = findViewById(R.id.activity_gif_dialogProgress);
        gifView = findViewById(R.id.activity_gif_dialogGif);

        progressLyt = findViewById(R.id.activity_staggered_progress);

        //设置布局管理器
        layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);// 瀑布流

        //设置adapter
        int itemWidth = (CommonUtil.getScreenWidth() - SPACE * 2) / ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).getSpanCount();
//        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter = new GifAdapter(getActivity(), itemWidth));
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(this, SPACE));
        recyclerView.addItemDecoration(new GridItemDecoration(ContextCompat.getDrawable(getActivity(), R.color.transparent), SPACE, SPACE, false));

//        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
//        itemTouchHelper = new ItemTouchHelper(callback);
//        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void initEvent() {

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    eraseAllButton.setVisibility(View.VISIBLE);
                } else {
                    eraseAllButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    KeyboardUtil.showHideIme(getActivity(), false);
                    final String query = searchEdit.getText().toString();
                    if (!TextUtils.isEmpty(query)) {
                        loadGif(query);
                    }
                    return true;
                }
                return false;
            }
        });

        eraseAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEdit.setText(null);
            }
        });

        adapter.setOnItemClickListener(new GifAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final View view, final int position) {
                if (gifLyt != null && gifLyt.getVisibility() == View.VISIBLE) {
                    gifLyt.setVisibility(View.INVISIBLE);
                } else {

                    File gifFile = GifCacheUtil.getInstance().get(adapter.mediaBeanList.get(position).getTinygif().getUrl());
                    if (gifFile != null && gifFile.exists()) {
                        PhotoShowActivity.launchActivity(getActivity(), gifFile.getAbsolutePath(), view);
                    }
                }
            }

            @Override
            public void onItemLongClick(final View view, final int position) {

                gifLyt.getLayoutParams().width = CommonUtil.getScreenWidth() / 2;
                gifLyt.getLayoutParams().height = gifLyt.getLayoutParams().width;

                float x = view.getLeft() - (gifLyt.getLayoutParams().width - view.getWidth()) / 2;

                if (x < -gifLyt.getLayoutParams().width / 9) {
                    x = -gifLyt.getLayoutParams().width / 9;
                } else if (x > CommonUtil.getScreenWidth() - gifLyt.getLayoutParams().width + gifLyt.getLayoutParams().width / 9) {
                    x = CommonUtil.getScreenWidth() - gifLyt.getLayoutParams().width + gifLyt.getLayoutParams().width / 9;
                }

                gifLyt.setX(x);

                int arrowPadding = gifLyt.getLayoutParams().height / 12 / 3;

                if (recyclerView.getTop() + view.getTop() - gifLyt.getLayoutParams().height + gifLyt.getLayoutParams().height / 7 + arrowPadding > findViewById(R.id.headerLayout).getBottom()) {
                    gifLyt.setY(recyclerView.getTop() + view.getTop() - gifLyt.getLayoutParams().height + arrowPadding);
                    gifLyt.setPadding(gifLyt.getLayoutParams().width / 8, gifLyt.getLayoutParams().height / 7, gifLyt.getLayoutParams().width / 8, gifLyt.getLayoutParams().height / 12);
                    gifView.setPadding(0, 0, 0, gifLyt.getLayoutParams().height / 16);
                    gifLyt.setBackgroundResource(R.drawable.activity_staggered_gif_background_up);
                } else {
                    gifLyt.setY(recyclerView.getTop() + view.getTop() + view.getHeight() - arrowPadding);
                    gifLyt.setPadding(gifLyt.getLayoutParams().width / 8, gifLyt.getLayoutParams().height / 12, gifLyt.getLayoutParams().width / 8, gifLyt.getLayoutParams().height / 7);
                    gifView.setPadding(0, gifLyt.getLayoutParams().height / 16, 0, 0);
                    gifLyt.setBackgroundResource(R.drawable.activity_staggered_gif_background_down);
                }

                gifView.setVisibility(View.INVISIBLE);
                gifProgress.setVisibility(View.VISIBLE);
                gifLyt.setVisibility(View.VISIBLE);

                File file = GifCacheUtil.getInstance().get(adapter.mediaBeanList.get(position).getTinygif().getUrl());
                if (file != null) {
                    gifView.setMovieFile(file.getAbsolutePath());
                    gifView.setVisibility(View.VISIBLE);
                    gifProgress.setVisibility(View.INVISIBLE);
                    return;
                }

                ((GifAdapter.MyHolder) recyclerView.getChildViewHolder(view)).imageView.setTag(adapter.mediaBeanList.get(position).getTinygif().getUrl());

                ThreadPool.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        GifCacheUtil.getInstance().downloadGif(adapter.mediaBeanList.get(position).getTinygif().getUrl(), new GifCacheUtil.GifDownloadCallback() {
                            @Override
                            public void onSuccess() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        View imageView = ((GifAdapter.MyHolder) recyclerView.getChildViewHolder(view)).imageView;

                                        if (imageView.getTag() != null && imageView.getTag().equals(adapter.mediaBeanList.get(position).getTinygif().getUrl())) {
                                            gifView.setMovieFile(GifCacheUtil.getInstance().get(adapter.mediaBeanList.get(position).getTinygif().getUrl()).getAbsolutePath());
                                            gifView.setVisibility(View.VISIBLE);
                                            gifProgress.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onFailed() {
                                GifCacheUtil.getInstance().clearKeyUri(adapter.mediaBeanList.get(position).getTinygif().getUrl());
                            }

                            @Override
                            public void onProgress(int degree) {

                            }
                        });
                    }
                });
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                final int action = e.getAction();

                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        if (gifLyt != null)
                            gifLyt.setVisibility(View.INVISIBLE);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                int[] info = new int[3];
//                layoutManager.findLastCompletelyVisibleItemPositions(info);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE && Arrays.asList(info).contains(lastVisibleItem)) {
//
//                    if (!isLoading) {
//                        progressLyt.setVisibility(View.VISIBLE);
//                        if (TextUtils.isEmpty(curSearchStr)) {
//                            loadTrendingGif();
//                        } else {
//                            searchRiffsyGif(null);
//                        }
//                    }
//                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
    }

    private void loadGif(String searchStr) {
        progressLyt.setVisibility(View.VISIBLE);

        String url = TextUtils.isEmpty(searchStr) ? "https://api.tenor.com/v1/random" : "https://api.tenor.com/v1/search";

        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("q", TextUtils.isEmpty(searchStr) ? "美女" : searchStr);
        params.addBodyParameter("pos", pos);
//        params.addBodyParameter("key", );
        params.addBodyParameter("limit", Integer.toString(Config.LIMIT));
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
//                recyclerView.refreshComplete();
//                recyclerView.loadMoreComplete();
                progressLyt.setVisibility(View.INVISIBLE);
                RiffsyResponseBean responseBean = JSON.parseObject(responseInfo.result, RiffsyResponseBean.class);
                if (responseBean != null) {
                    List<RiffsyMediaBean> mediaBeans = new ArrayList<>();
                    for (int i = 0; i < responseBean.getResults().length; i++) {
                        for (int j = 0; j < responseBean.getResults()[i].getMedia().length; j++) {
                            RiffsyMediaBean gifBean = responseBean.getResults()[i].getMedia()[j];
                            if (gifBean != null) {
                                mediaBeans.add(gifBean);
                            }
                        }
                    }
                    if (mediaBeans.size() > 0) {
                        if (pos == null) {
                            adapter.updateData(mediaBeans);
                        } else {
                            adapter.addData(mediaBeans);
                        }
                        pos = responseBean.getNext();
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
//                recyclerView.refreshComplete();
//                recyclerView.loadMoreComplete();
                progressLyt.setVisibility(View.INVISIBLE);
                ToastUtil.showToast(s);
            }
        });
    }

}
