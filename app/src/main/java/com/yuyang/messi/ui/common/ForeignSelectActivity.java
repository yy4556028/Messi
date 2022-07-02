package com.yuyang.messi.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.yuyang.lib_base.config.Config;
import com.yuyang.lib_base.net.unify.HttpException;
import com.yuyang.lib_base.net.unify.HttpRequest.HttpMethod;
import com.yuyang.lib_base.net.unify.HttpUtils;
import com.yuyang.lib_base.net.unify.RequestCallBack;
import com.yuyang.lib_base.net.unify.RequestParams;
import com.yuyang.lib_base.net.unify.ResponseInfo;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForeignSelectActivity extends AppBaseActivity {

    private static final String KEY_TITLE = "key_title";
    private static final String KEY_SERVICE = "key_service";
    private static final String KEY_PARAM_MAP = "key_param_map";
    private static final String KEY_RESPONSE_ROWS = "key_response_rows";
    private static final String KEY_BEAN_CLASS = "key_bean_class";
    private static final String KEY_ITEM_TITLE = "key_item_title";

    public static final String KEY_BEAN = "key_bean";

    private XRecyclerView recyclerView;
    private MyRecyclerAdapter recyclerAdapter;

    private HashMap<String, String> paramMap;
    private String responseRows;
    private String service;
    private Class beanClass;
    private String itemTitle;

    private int page = -1;//-1代表无分页
    private int limit = Config.LIMIT;

    public static Intent getLaunchIntent(Activity activity, int requestCode,
                                    String title,
                                    String service,
                                    HashMap<String, String> paramMap,
                                    String responseRows,
                                    Class beanClass,
                                    String itemTitle) {
        Intent intent = new Intent(activity, ForeignSelectActivity.class);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_SERVICE, service);
        intent.putExtra(KEY_PARAM_MAP, paramMap);
        intent.putExtra(KEY_RESPONSE_ROWS, responseRows);
        intent.putExtra(KEY_BEAN_CLASS, beanClass);
        intent.putExtra(KEY_ITEM_TITLE, itemTitle);
        return intent;
    }

    public static Intent getLaunchIntent(Fragment fragment,
                                    String title,
                                    String service,
                                    HashMap<String, String> paramMap,
                                    String responseRows,
                                    Class beanClass,
                                    String itemTitle) {
        Intent intent = new Intent(fragment.getContext(), ForeignSelectActivity.class);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_SERVICE, service);
        intent.putExtra(KEY_PARAM_MAP, paramMap);
        intent.putExtra(KEY_RESPONSE_ROWS, responseRows);
        intent.putExtra(KEY_BEAN_CLASS, beanClass);
        intent.putExtra(KEY_ITEM_TITLE, itemTitle);
        return intent;
    }

    @Override
    protected int getLayoutId() {
//        return R.layout.common_header_xrecycler;
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = getIntent().getStringExtra(KEY_SERVICE);
        paramMap = (HashMap<String, String>) getIntent().getSerializableExtra(KEY_PARAM_MAP);
        responseRows = getIntent().getStringExtra(KEY_RESPONSE_ROWS);
        beanClass = (Class) getIntent().getSerializableExtra(KEY_BEAN_CLASS);
        itemTitle = getIntent().getStringExtra(KEY_ITEM_TITLE);

        initView();
        getLinkData();
    }

    private void initView() {
        HeaderLayout mHeaderLayout = findViewById(R.id.headerLayout);
        mHeaderLayout.showLeftBackButton();
        mHeaderLayout.showTitle(getIntent().getStringExtra(KEY_TITLE));

//        recyclerView = findViewById(R.id.common_header_xrecycler_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setLoadingMoreEnabled(false);
        recyclerView.setAdapter(recyclerAdapter = new MyRecyclerAdapter());
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getLinkData();
            }

            @Override
            public void onLoadMore() {
                page++;
                getLinkData();
            }
        });
    }

    /**
     * 获取外键信息
     */
    private void getLinkData() {
        new HttpUtils().send(HttpMethod.POST, service, getMParams(), new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                recyclerView.refreshComplete();
                recyclerView.loadMoreComplete();
                ToastUtil.showToast("请求失败");
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                recyclerView.refreshComplete();
                recyclerView.loadMoreComplete();
                parseLinkData(arg0.result);
            }
        });
    }

    private void parseLinkData(String result) {
        try {
            JSONObject obj = null;
            JSONArray array = null;
            if (!TextUtils.isEmpty(responseRows)) {
                obj = new JSONObject(result);
                array = obj.optJSONArray(responseRows);

                if (page != -1) {
                    if (page == 1) {
                        recyclerAdapter.beanList.clear();
                    }

                    recyclerView.setLoadingMoreEnabled(page * limit < obj.optInt("total"));
                }
            } else {
                array = new JSONArray(result);
                recyclerView.setLoadingMoreEnabled(false);
            }

            List beanList = JSON.parseArray(array.toString(), beanClass);
            List<String> titleList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                titleList.add(array.optJSONObject(i).optString(itemTitle));
            }

            recyclerAdapter.addData(titleList, beanList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RequestParams getMParams() {
        RequestParams params = new RequestParams();
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            if (entry.getKey().equals("page")) {
                if (page == -1) page = 1;
                params.addBodyParameter("page", String.valueOf(page));
            } else if (entry.getKey().equals("limit")) {
                try {
                    limit = Integer.parseInt(entry.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                params.addBodyParameter("limit", String.valueOf(limit));
            } else {
                params.addBodyParameter(entry.getKey(), entry.getValue());
            }
        }
        if (!paramMap.containsKey("limit")) {
            params.addBodyParameter("limit", String.valueOf(limit));
        }
        return params;
    }

    public class MyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<String> titleList = new ArrayList<>();
        private List<Object> beanList = new ArrayList<>();

        private int selectPos = -1;

        public void addData(List<String> titleList, List<Object> beanList) {
            if (titleList != null) {
                this.titleList.addAll(titleList);
                this.beanList.addAll(beanList);
            }
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
//                case 0: {
//                    return new MyHolder(LayoutInflater.from(getActivity()).inflate(R.layout.activity_foreign_select_item, parent, false));
//                }
//                default: {
//                    return new EmptyHolder(Utils.getEmptyViewForListView(getActivity()));
//                }
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {

            if (0 == getItemViewType(position)) {

                MyHolder myHolder = (MyHolder) holder;

                myHolder.checkBox.setChecked(position == selectPos);
                myHolder.nameText.setText(titleList.get(position));

                if (position == beanList.size() - 1) {
                    myHolder.line.setVisibility(View.GONE);
                } else {
                    myHolder.line.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public int getItemCount() {
            if (beanList == null || beanList.size() == 0) {
                return 1;
            }
            return beanList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return (beanList == null || beanList.size() == 0) ? 1 : 0;
        }

        public class MyHolder extends RecyclerView.ViewHolder {
            private CheckBox checkBox;
            private TextView nameText;
            private View line;

            MyHolder(View itemView) {
                super(itemView);
//                checkBox = (CheckBox) itemView.findViewById(R.id.activity_foreign_select_item_checkBox);
//                nameText = (TextView) itemView.findViewById(R.id.activity_foreign_select_item_nameText);
//                line = itemView.findViewById(R.id.activity_foreign_select_item_line);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = getAdapterPosition() - 1;
                        recyclerAdapter.selectPos = index;
                        recyclerAdapter.notifyDataSetChanged();

                        if (index >= 0) {
                            Intent intent = new Intent();
                            intent.putExtra(KEY_BEAN, (Serializable) recyclerAdapter.beanList.get(index));
                            setResult(RESULT_OK, intent);
                        }
                        finish();
                    }
                });
            }
        }

        public class EmptyHolder extends RecyclerView.ViewHolder {
            EmptyHolder(View view) {
                super(view);
            }
        }
    }
}
