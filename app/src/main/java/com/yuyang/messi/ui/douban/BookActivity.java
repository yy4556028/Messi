package com.yuyang.messi.ui.douban;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import com.bumptech.glide.Glide;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.douban.DoubanBookBean;
import com.yuyang.messi.net.retrofit.Apis;
import com.yuyang.messi.net.okhttp.OkHttpUtil;
import com.yuyang.messi.net.okhttp.callback.StringCallback;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.utils.SnackBarUtil;

import okhttp3.Call;

public class BookActivity extends AppBaseActivity {

    public static final String KEY_BOOK_BEAN = "book_bean";
    private DoubanBookBean bookBean;

    private ImageView iconImage;

    public static void launchActivity(Activity activity, ImageView iconImage, DoubanBookBean bookBean) {

        Intent intent = new Intent(activity, BookActivity.class);
        intent.putExtra(KEY_BOOK_BEAN, bookBean);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, iconImage, bookBean.getImage());
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_book;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookBean = getIntent().getParcelableExtra(KEY_BOOK_BEAN);
        initView();
        loadBook();
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle(bookBean.getTitle());

        iconImage = findViewById(R.id.activity_book_icon);
        ViewCompat.setTransitionName(iconImage, bookBean.getImage());
        Glide.with(this)
                .load(bookBean.getImage())
                .into(iconImage);
    }

    private void loadBook() {
        OkHttpUtil
                .get()
                .url(Apis.GetBookInfoApi + bookBean.getId())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        SnackBarUtil.makeShort(iconImage, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {

                    }
                });
    }

}
