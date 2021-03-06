package com.yuyang.messi.ui.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yuyang.lib_base.helper.SelectImageUtil;
import com.yuyang.lib_base.myglide.GlideApp;
import com.yuyang.lib_base.ui.base.BaseFragment;
import com.yuyang.lib_base.ui.view.CommonDialog;
import com.yuyang.lib_base.utils.FileUtil;
import com.yuyang.lib_base.utils.StorageUtil;
import com.yuyang.lib_base.utils.SystemBarUtil;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.threadPool.ThreadPool;
import com.yuyang.messi.utils.DateUtil;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * http://blog.csdn.net/zhangxin09/article/details/8750586
 */
public class WebViewFragment extends BaseFragment {

    private static final String URL_KEY = "url";

    private SwipeRefreshLayout refreshLayout;
    protected WebView mWebView;
    private ProgressBar mProgressBar;

    private int mOriginalOrientation;
    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;

    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;

    private String url;

    public static WebViewFragment newInstance(String url) {
        WebViewFragment webViewFragment = new WebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(URL_KEY, url);
        webViewFragment.setArguments(bundle);
        return webViewFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_webview;
    }

    @SuppressLint("JavascriptInterface")
    @Override
    protected void doOnViewCreated() {
        url = getArguments().getString(URL_KEY);

        refreshLayout = $(R.id.fragment_webView_refresh);
        mWebView = $(R.id.fragment_webView_webView);
        mProgressBar = $(R.id.fragment_webView_progressbar);

        refreshLayout.setEnabled(true);
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        refreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(getContext(), R.color.white));
        refreshLayout.setDistanceToTriggerSync(200);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.loadUrl(url);
            }
        });
        setCookies(url);
        initWebViewSettings();
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());

        /**
         * ????????????????????????
         */
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult hitTestResult = mWebView.getHitTestResult();
                if (null == hitTestResult)
                    return false;
                int type = hitTestResult.getType();
                switch (type) {
                    case WebView.HitTestResult.EDIT_TEXT_TYPE: // ?????????????????????
                        break;
                    case WebView.HitTestResult.PHONE_TYPE: // ????????????
                        break;
                    case WebView.HitTestResult.EMAIL_TYPE: // ??????Email
                        break;
                    case WebView.HitTestResult.GEO_TYPE: // ???????????????
                        break;
                    case WebView.HitTestResult.SRC_ANCHOR_TYPE: // ?????????
                        break;
                    case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE: // ???????????????????????????
                    case WebView.HitTestResult.IMAGE_TYPE: // ??????????????????????????????
                        // ??????????????????????????????
                        CommonDialog commonDialog = new CommonDialog(getActivity());
                        commonDialog.show();
                        commonDialog.setTitle("????????????");
                        commonDialog.setSubtitle("??????????????????????????????\n(?????????????????????)");
                        commonDialog.setBtnText("??????", "??????");
                        commonDialog.setOnBtnRightClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                commonDialog.dismiss();
                                String picUrl = hitTestResult.getExtra();//????????????
                                ThreadPool.getInstance().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        GlideApp.with(getActivity())
                                                .asBitmap()
                                                .diskCacheStrategy(DiskCacheStrategy.NONE) //????????????SD???
                                                .skipMemoryCache(true)
                                                .load(picUrl)
                                                .into(new CustomTarget<Bitmap>() {
                                                    @Override
                                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                        Uri uri = FileUtil.saveImageV29(resource, StorageUtil.getPublicPath("/WebImg/" + DateUtil.formatDataToString(new Date(), "yyyy-MM-dd_HH:mm:ss") + ".png"));
                                                        if (uri == null) {
                                                            ToastUtil.showToast("??????????????????");
                                                        } else {
                                                            ToastUtil.showToast("??????????????????" + SelectImageUtil.getPath(getActivity(), uri));
                                                        }
                                                    }

                                                    @Override
                                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                                    }
                                                });
                                    }
                                });
                            }
                        });
                        return true;
                    case WebView.HitTestResult.UNKNOWN_TYPE: //??????
                        break;
                }
                return false;
            }
        });

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(url));
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        ComponentName componentName = intent.resolveActivity(getActivity().getPackageManager());
                        startActivity(Intent.createChooser(intent, "??????????????????"));
                    } else {
                        ToastUtil.showToast("?????????????????????");
                    }
                }
            }
        });

        mWebView.addJavascriptInterface(getHtmlObject(), "interfaceName");


        mWebView.loadUrl(url);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mWebView != null)
            mWebView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWebView != null)
            mWebView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveCookies();
        if (mWebView != null) {
            //?????? webView ??????
            mWebView.clearCache(true);
            mWebView.destroy();
        }
    }

    private void initWebViewSettings() {
        WebSettings webSettings = mWebView.getSettings();

        webSettings.setDefaultFontSize(16);
        webSettings.setDefaultFixedFontSize(16);

        webSettings.setNeedInitialFocus(true); //???webview??????requestFocus??????webview????????????

        // enable content url access
        webSettings.setAllowContentAccess(true);

        // HTML5 API flags
        webSettings.setAppCacheEnabled(true);
        //        String appCacheDir = App.getAppContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        //        webSettings.setAppCachePath(appCacheDir);// /data/data/com.example.zk.android/app_cache
        webSettings.setDatabaseEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//????????????
        webSettings.setDomStorageEnabled(true);//??????DOM storage API??????    ??????localStorage???????????????

        webSettings.setGeolocationEnabled(true);
        webSettings.setJavaScriptEnabled(true);  //??????js
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //????????????JS???????????????
        webSettings.setDefaultTextEncodingName("utf-8");//??????????????????
        webSettings.setLoadsImagesAutomatically(true);  //????????????????????????
        //????????????????????????????????????
        webSettings.setUseWideViewPort(true);  //????????????????????????webview?????????
        webSettings.setLoadWithOverviewMode(true); // ????????????????????????

        webSettings.setSupportZoom(true);  //????????????????????????true??????????????????????????????
        webSettings.setBuiltInZoomControls(true); // ??????????????????
        //????????????false?????????WebView?????????????????????????????????????????????????????????

        webSettings.setDisplayZoomControls(false); //???????????????????????????
        webSettings.setSupportMultipleWindows(false);

        webSettings.setAllowFileAccess(true);  //????????????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);  //????????????????????????
        }
        // webview???5.0?????????????????????????????????,https???????????????http??????,??????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        //????????????????????????????????????????????????????????????
        mWebView.requestFocusFromTouch();
        mWebView.setVerticalScrollBarEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);//?????????unify??????????????????bug???????????????????????????
//        }

        // ?????????
        mWebView.setInitialScale(100);

        webSettings.setPluginState(WebSettings.PluginState.ON);

        webSettings.setBlockNetworkImage(false);//???????????????????????????????????????

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //????????????????????????
        webSettings.supportMultipleWindows();  //?????????

        webSettings.setAllowUniversalAccessFromFileURLs(true);//????????????

        webSettings.setTextZoom(100);//??????????????????????????????(????????????????????????,setTextSize api14?????????)
    }

    /**
     * Html ??? Android ????????????
     * http://blog.csdn.net/it1039871366/article/details/46372207
     *
     * @return
     */
    private Object getHtmlObject() {
        Object insertObj = new Object() {

            public String HtmlcallJava() {//
                return "Html call Java";
            }

            public void HtmlcallJava2(final String game, final String id) {//
//                if (!TextUtils.isEmpty(game) && game.equals("game")) {
//                    Intent intent = new Intent(getActivity(), GameDetailActivity.class);
//                    intent.putExtra(GameDetailActivity.GAME_ID, id);
//                    startActivity(intent);
//                }
            }

            public void JavacallHtml() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript: showFromHtml()");
                        ToastUtil.showToast("clickBtn");
                    }
                });
            }

            public void JavacallHtml2() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript: showFromHtml2('IT-homer blog')");
                        ToastUtil.showToast("clickBtn2");
                    }
                });
            }
        };

        return insertObj;
    }

    public boolean canGoBack() {
        return mWebView != null && mWebView.canGoBack();
    }

    public void goBack() {
        if (mWebView != null) {
            mWebView.goBack();
        }
    }

    //WebViewClient????????????WebView???????????????????????????????????????
    private class MyWebViewClient extends WebViewClient {
        /**
         * ?????????????????????????????????????????????
         * ?????????????????????true???????????????????????????????????????????????????webview????????????
         * ???????????????????????????
         * ??????????????????????????????????????????????????????????????????????????????URL????????????????????????????????????
         * ????????????????????????????????????????????????????????????????????????????????????????????????
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return shouldOverrideUrlLoadingImpl(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();//webview??????https??????
        }

        // ????????????????????????????????????url????????????????????????url???????????????????????????????????????
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);

        }

//        shouldOverrideUrlLoading(WebView view, String url)  ?????????????????????????????????
//        //????????????????????????????????????????????????,??????????????????????????????????????????
//        //????????????url?????????url.contains(???add???)?????????????????????
//
//        shouldOverrideKeyEvent(WebView view, KeyEvent event)
//        //???????????????????????????????????????????????????????????????
//
//        onPageStarted(WebView view, String url, Bitmap favicon)
//        //????????????????????????????????????????????????????????????????????????loading??????????????????????????????????????????????????????
//
//        onPageFinished(WebView view, String url)
//        //??????????????????????????????????????????????????????????????????loading ???????????????????????????
//
//        onLoadResource(WebView view, String url)
//        // ???????????????????????????????????????????????????????????????????????????????????????????????????
//
//        onReceivedError(WebView view, int errorCode, String description, String failingUrl)
//        // (??????????????????)
//
//        doUpdateVisitedHistory(WebView view, String url, boolean isReload)
//        //(??????????????????)
//
//        onFormResubmission(WebView view, Message dontResend, Message resend)
//        //(????????????????????????????????????)
//
//        onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host,String realm)
//        //????????????????????????????????????
//
//        onScaleChanged(WebView view, float oldScale, float newScale)
//        // (WebView?????????????????????)
//
//        onUnhandledKeyEvent(WebView view, KeyEvent event)
//        //???Key??????????????????????????????
    }

    //WebChromeClient?????????WebView??????Javascript????????????????????????????????????title??????????????????
    class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mProgressBar.setProgress(newProgress);
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
            } else {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
            if (getActivity() == null) {
                return;
            }
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mOriginalOrientation = getActivity().getRequestedOrientation();
            FrameLayout decor = (FrameLayout) getActivity().getWindow().getDecorView();
            decor.addView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mCustomView = view;
            SystemBarUtil.fullScreen_immersive(getActivity(), false, false, false, false, true);
            mCustomViewCallback = callback;
//            mWebView.setVisibility(View.GONE);
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }

        @Override
        public void onHideCustomView() {
            if (getActivity() == null) {
                return;
            }
//            mWebView.setVisibility(View.VISIBLE);
            if (mCustomView == null) {
                return;
            }

            SystemBarUtil.fullScreen_immersive(getActivity(), true, false, true, false, true);
            ((WebViewActivity) getActivity()).setStatusBar();
            FrameLayout decor = (FrameLayout) getActivity().getWindow().getDecorView();
            decor.removeView(mCustomView);
            mCustomView = null;
            mCustomViewCallback.onCustomViewHidden();
            getActivity().setRequestedOrientation(mOriginalOrientation);
            super.onHideCustomView();
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView newWebView = new WebView(view.getContext());
            newWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                    return true;
                }
            });
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(newWebView);
            resultMsg.sendToTarget();
            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
        }

        // For 3.0+ Devices (Start)
        protected void openFileChooser(ValueCallback uploadMsg, String acceptType) {
            openFileChooser(uploadMsg, acceptType, null);
        }

        // For Android 4.1 only
        protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            uploadMessage = uploadMsg;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(acceptType);
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (null == uploadMessage && null == uploadMessageAboveL) return;

                    if (uploadMessageAboveL != null) {
                        Uri[] results = null;
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            if (result.getData() != null) {
                                String dataString = result.getData().getDataString();
                                ClipData clipData = result.getData().getClipData();
                                if (clipData != null) {
                                    results = new Uri[clipData.getItemCount()];
                                    for (int i = 0; i < clipData.getItemCount(); i++) {
                                        ClipData.Item item = clipData.getItemAt(i);
                                        results[i] = item.getUri();
                                    }
                                }
                                if (dataString != null)
                                    results = new Uri[]{Uri.parse(dataString)};
                            }
                        }
                        uploadMessageAboveL.onReceiveValue(results);
                        uploadMessageAboveL = null;
                    } else if (uploadMessage != null) {
                        uploadMessage.onReceiveValue(result.getData() == null || result.getResultCode() != Activity.RESULT_OK ? null : result.getData().getData());
                        uploadMessage = null;
                    }
                }
            }).launch(Intent.createChooser(intent, "File Browser"));
        }

        // For Lollipop 5.0+ Devices
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (uploadMessageAboveL != null) {
                uploadMessageAboveL.onReceiveValue(null);
                uploadMessageAboveL = null;
            }
            uploadMessageAboveL = filePathCallback;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (null == uploadMessage && null == uploadMessageAboveL) return;

                    if (uploadMessageAboveL != null) {
                        Uri[] results = null;
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            if (result.getData() != null) {
                                String dataString = result.getData().getDataString();
                                ClipData clipData = result.getData().getClipData();
                                if (clipData != null) {
                                    results = new Uri[clipData.getItemCount()];
                                    for (int i = 0; i < clipData.getItemCount(); i++) {
                                        ClipData.Item item = clipData.getItemAt(i);
                                        results[i] = item.getUri();
                                    }
                                }
                                if (dataString != null)
                                    results = new Uri[]{Uri.parse(dataString)};
                            }
                        }
                        uploadMessageAboveL.onReceiveValue(results);
                        uploadMessageAboveL = null;
                    } else if (uploadMessage != null) {
                        uploadMessage.onReceiveValue(result.getData() == null || result.getResultCode() != Activity.RESULT_OK ? null : result.getData().getData());
                        uploadMessage = null;
                    }
                }
            }).launch(Intent.createChooser(intent, "File Browser"));
            return true;
        }

        //??????Web?????????title??????????????????????????????title
        //???????????????????????????????????????????????????onReceiveTitle????????????????????? ??????????????????,
        //?????????????????????onReceiveError??????????????????????????????title
        @Override
        public void onReceivedTitle(WebView view, String title) {
//            if (getActivity() instanceof WebViewActivity) {
//                ((WebViewActivity) getActivity()).headerLayout.setTitle(title);
//            }
        }
//
//        @Override
//        public void onReceivedIcon(WebView view, Bitmap icon) {
//            //
//        }
//
//        //??????alert????????????html ?????????????????????
//        @Override
//        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//            //
//            return true;
//        }
//
//        //??????confirm?????????
//        @Override
//        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult
//                result) {
//            //
//            return true;
//        }
//
//        //??????prompt?????????
//        @Override
//        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
//            //
//            return true;
//        }
    }

    //https://www.jianshu.com/p/c9a9c4e1756d

    private void setCookies(String cookiesPath) {
        String cookie = getActivity().getSharedPreferences("cookie", Context.MODE_PRIVATE).getString("cookies", "");// ???SharedPreferences???????????????Cookie???
        if (!TextUtils.isEmpty(cookie)) {
            String[] cookieArray = cookie.split(";");// ??????Cookie????????????????????????
            for (int i = 0; i < cookieArray.length; i++) {
                int position = cookieArray[i].indexOf("=");// ???Cookie???????????????????????????
                String cookieName = cookieArray[i].substring(0, position);// ?????????
                String cookieValue = cookieArray[i].substring(position + 1);// ?????????

                String value = cookieName + "=" + cookieValue;// ?????????????????? value
                Log.i("cookie", value);
//                cookieManager.setAcceptCookie(true);
//                cookieManager.removeSessionCookie();// ????????????[????????????]
                CookieManager.getInstance().setCookie(getDomain(cookiesPath), value);// ?????? Cookie
            }
        }
    }

    private void saveCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        String cookieStr = cookieManager.getCookie(getDomain(url));
        SharedPreferences preferences = getActivity().getSharedPreferences("cookie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("cookies", cookieStr);
        editor.commit();
    }

    /**
     * ??????URL?????????
     */
    private String getDomain(String url) {
        url = url.replace("http://", "").replace("https://", "");
        if (url.contains("/")) {
            url = url.substring(0, url.indexOf('/'));
        }
        return url;
    }

    private boolean shouldOverrideUrlLoadingImpl(WebView view, String url) {
        if (view.isPrivateBrowsingEnabled()) {
            return false;
        }
        String SCHEME_WTAI = "wtai://wp/";
        String SCHEME_WTAI_MC = "wtai://wp/mc;";
        String SCHEME_WTAI_SD = "wtai://wp/sd;";
        String SCHEME_WTAI_AP = "wtai://wp/ap;";
        if (url.startsWith(SCHEME_WTAI)) {
            if (url.startsWith(SCHEME_WTAI_MC)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(WebView.SCHEME_TEL + url.substring(SCHEME_WTAI_MC.length())));
                startActivity(intent);
                return true;
            }
            if (url.startsWith(SCHEME_WTAI_SD)) {
                return false;
            }
            if (url.startsWith(SCHEME_WTAI_AP)) {
                return false;
            }
        }

        Intent intent;
        try {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
        } catch (URISyntaxException ex) {
            return false;
        }
        if (getActivity() == null) {
            return false;
        }
        if (getActivity().getPackageManager().resolveActivity(intent, 0) == null) {
            return false;
        }

        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setComponent(null);
        intent.setSelector(null);
        Pattern ACCEPTED_URI_SCHEMA = Pattern.compile("(?i)" + "(" + "(?:http|https|file):\\/\\/" + "|(?:inline|data|about|javascript):" + ")" + "(.*)");
        Matcher m = ACCEPTED_URI_SCHEMA.matcher(url);
        if (m.matches()) {
            return false;
        }

        try {
            if (getActivity().startActivityIfNeeded(intent, -1)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }

//        view.loadUrl(url);
        return false;
    }
}
