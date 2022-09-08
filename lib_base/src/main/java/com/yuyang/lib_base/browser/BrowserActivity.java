package com.yuyang.lib_base.browser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.yuyang.lib_base.R;
import com.yuyang.lib_base.ui.base.BaseActivity;
import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.ui.header.HeaderRightBean;
import com.yuyang.lib_base.utils.ClipboardUtil;
import com.yuyang.lib_base.utils.SystemShareUtil;

import java.util.Arrays;

public class BrowserActivity extends BaseActivity {

    private static final String KEY_TITLE = "key_title";
    private static final String KEY_URL = "key_url";
    private static final String KEY_SHARE = "key_share";

    protected HeaderLayout headerLayout;

    private String mUrl;

    private BrowserFragment mBrowserFragment;

    public static void launchActivity(Context context, String title, String url) {
        BrowserActivity.launchActivity(context, title, url, false);
    }

    public static void launchActivity(Context context, String title, String url, boolean share) {
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_SHARE, share);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mUrl = getIntent().getStringExtra(KEY_URL);

        headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle(getIntent().getExtras().getString(KEY_TITLE));
        headerLayout.setMenu(Arrays.asList(new HeaderRightBean("复制", 0),
                new HeaderRightBean("分享", R.drawable.share)), new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString()) {
                    case "复制": {
                        ClipboardUtil.setText(mBrowserFragment.mWebView.getUrl());
                        Snackbar.make(headerLayout, "已复制到剪切板", Snackbar.LENGTH_SHORT).show();
                        break;
                    }
                    case "分享": {
                        SystemShareUtil.shareText(getActivity(), getIntent().getStringExtra(KEY_TITLE), mUrl);
                        break;
                    }
                }
                return true;
            }
        });

        mBrowserFragment = BrowserFragment.newInstance(mUrl);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_fragmentContainerView, mBrowserFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (mBrowserFragment.canGoBack()) {
            mBrowserFragment.goBack();
        } else {
            finish();
        }
    }
}
