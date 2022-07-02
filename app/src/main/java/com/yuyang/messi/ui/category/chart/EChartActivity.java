package com.yuyang.messi.ui.category.chart;

import android.os.Bundle;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.Nullable;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;

public class EChartActivity extends AppBaseActivity {

    private WebView webView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_echart;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("EChart");

        webView = findViewById(R.id.activity_echart_webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setSupportZoom(false);
        webSettings.setBlockNetworkImage(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webView.addJavascriptInterface(new JavaScriptInterface(), "listener");
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("file:///android_asset/echart/index.html");
    }

    public class JavaScriptInterface {

        public JavaScriptInterface() {
        }

        @JavascriptInterface
        public void calltime(String buttonId) {
            Message message = new Message();
            message.what = 0;
            message.obj = buttonId;
        }

        @JavascriptInterface
        public void refreshTime(String time) {
            Message message = new Message();
            message.what = 1;
            message.obj = time;
        }
    }
}
