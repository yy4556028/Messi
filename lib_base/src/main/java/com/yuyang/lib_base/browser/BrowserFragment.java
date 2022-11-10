package com.yuyang.lib_base.browser;

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
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yuyang.lib_base.R;
import com.yuyang.lib_base.helper.SelectImageUtil;
import com.yuyang.lib_base.myglide.GlideApp;
import com.yuyang.lib_base.ui.base.BaseActivity;
import com.yuyang.lib_base.ui.base.BaseFragment;
import com.yuyang.lib_base.ui.view.CommonDialog;
import com.yuyang.lib_base.utils.FileUtil;
import com.yuyang.lib_base.utils.StorageUtil;
import com.yuyang.lib_base.utils.SystemBarUtil;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.lib_base.threadPool.ThreadPool;
import com.yuyang.lib_base.utils.DateUtil;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * http://blog.csdn.net/zhangxin09/article/details/8750586
 */
public class BrowserFragment extends BaseFragment {

    private static final String URL_KEY = "url";

    private SwipeRefreshLayout refreshLayout;
    public WebView mWebView;
    private ProgressBar mProgressBar;

    private int mOriginalOrientation;
    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;

    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;

    private String url;

    private final ActivityResultLauncher<Intent> fileChooserLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
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
            } else {
                uploadMessage.onReceiveValue(result.getData() == null || result.getResultCode() != Activity.RESULT_OK ? null : result.getData().getData());
                uploadMessage = null;
            }
        }
    });

    public static BrowserFragment newInstance(String url) {
        BrowserFragment browserFragment = new BrowserFragment();
        Bundle bundle = new Bundle();
        bundle.putString(URL_KEY, url);
        browserFragment.setArguments(bundle);
        return browserFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_webview;
    }

    @SuppressLint("JavascriptInterface")
    @Override
    protected void doOnViewCreated() {
        url = getArguments() != null ? getArguments().getString(URL_KEY) : null;
        WebkitCookieUtil.initCookie(requireContext(), url);

        refreshLayout = $(R.id.fragment_webView_refresh);
        mWebView = $(R.id.fragment_webView_webView);
        mProgressBar = $(R.id.fragment_webView_progressbar);

        refreshLayout.setEnabled(true);
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.theme));
        refreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(requireContext(), R.color.white));
        refreshLayout.setDistanceToTriggerSync(200);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.loadUrl(url);
            }
        });
        initWebViewSettings();

        /**
         * 禁止长按出现复制
         */
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult hitTestResult = mWebView.getHitTestResult();
                if (null == hitTestResult)
                    return false;
                int type = hitTestResult.getType();
                switch (type) {
                    case WebView.HitTestResult.EDIT_TEXT_TYPE: // 选中的文字类型
                        break;
                    case WebView.HitTestResult.PHONE_TYPE: // 处理拨号
                        break;
                    case WebView.HitTestResult.EMAIL_TYPE: // 处理Email
                        break;
                    case WebView.HitTestResult.GEO_TYPE: // 　地图类型
                        break;
                    case WebView.HitTestResult.SRC_ANCHOR_TYPE: // 超链接
                        break;
                    case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE: // 带有链接的图片类型
                    case WebView.HitTestResult.IMAGE_TYPE: // 处理长按图片的菜单项
                        // 弹出保存图片的对话框
                        CommonDialog commonDialog = new CommonDialog(requireActivity());
                        commonDialog.show();
                        commonDialog.setTitle("保存图片");
                        commonDialog.setSubtitle("是否保存图片到相册？\n(与展示图片不同)");
                        commonDialog.setBtnText("取消", "确定");
                        commonDialog.setOnBtnRightClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                commonDialog.dismiss();
                                String picUrl = hitTestResult.getExtra();//获取图片
                                ThreadPool.getInstance().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        GlideApp.with(requireActivity())
                                                .asBitmap()
                                                .diskCacheStrategy(DiskCacheStrategy.NONE) //不缓存到SD卡
                                                .skipMemoryCache(true)
                                                .load(picUrl)
                                                .into(new CustomTarget<Bitmap>() {
                                                    @Override
                                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                        Uri uri = FileUtil.saveImageV29(resource, StorageUtil.getPublicPath("/WebImg/" + DateUtil.formatDataToString(new Date(), "yyyy-MM-dd_HH:mm:ss") + ".png"));
                                                        if (uri == null) {
                                                            ToastUtil.showToast("图片保存失败");
                                                        } else {
                                                            ToastUtil.showToast("图片已保存到" + SelectImageUtil.getPath(getActivity(), uri));
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
                    case WebView.HitTestResult.UNKNOWN_TYPE: //未知
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
                        startActivity(Intent.createChooser(intent, "请选择浏览器"));
                    } else {
                        ToastUtil.showToast("没有匹配的程序");
                    }
                }
            }
        });

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
        WebkitCookieUtil.saveCookie(url);
        if (mWebView != null) {
            //clear webView 缓存
            mWebView.clearCache(true);
            mWebView.clearHistory();
            mWebView.removeAllViews();
            mWebView.destroy();
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initWebViewSettings() {
        WebSettings webSettings = mWebView.getSettings();

        webSettings.setDefaultFontSize(16);
        webSettings.setDefaultFixedFontSize(16);

        webSettings.setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点

        // enable content url access
        webSettings.setAllowContentAccess(true);

        // HTML5 API flags
//        webSettings.setAppCacheEnabled(true);
        //        String appCacheDir = App.getAppContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        //        webSettings.setAppCachePath(appCacheDir);// /data/data/com.example.zk.android/app_cache
        webSettings.setDatabaseEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//支持缓存
        webSettings.setDomStorageEnabled(true);//开启DOM storage API功能    使用localStorage则必须打开
        webSettings.setSavePassword(false);

        webSettings.setGeolocationEnabled(true);// 允许访问地址
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webSettings.setLoadsImagesAutomatically(true);  //支持自动加载图片
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true);  //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        webSettings.setSupportZoom(false);  //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(false); // 设置支持缩放
        //若上面是false，则该WebView不可缩放，这个不管设置什么都不能缩放。

        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        webSettings.setSupportMultipleWindows(false);

        webSettings.setAllowFileAccess(true);  //设置可以访问文件
        webSettings.setAllowFileAccessFromFileURLs(true);  //设置可以访问文件
        webSettings.setAllowUniversalAccessFromFileURLs(true);//允许跨域
        // webview从5.0开始默认不允许混合模式,https中不能加载http资源,需要设置开启
//        webSettings.setMixedContentMode(webSettings.getMixedContentMode());
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        //支持获取手势焦点，输入用户名、密码或其他
        mWebView.requestFocusFromTouch();
        mWebView.setVerticalScrollBarEnabled(false); // 垂直滚动条不显示
        mWebView.setVerticalScrollbarOverlay(false);
        mWebView.setHorizontalScrollBarEnabled(false); // 水平滚动条不显示
        mWebView.setHorizontalScrollbarOverlay(false);
        mWebView.setFocusable(true);
//        mWebView.setDrawingCacheEnabled(true);
        mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);//不设置unify时期会有白屏bug，设置了视频会黑屏
//        }

        // 不缩放
        mWebView.setInitialScale(100);

        webSettings.setPluginState(WebSettings.PluginState.ON);

        webSettings.setBlockNetworkImage(false);//页面加载好以后，再放开图片

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
        webSettings.supportMultipleWindows();  //多窗口

        webSettings.setTextZoom(100);//设置字体默认缩放大小(改变网页字体大小,setTextSize api14被弃用)

        webSettings.setJavaScriptEnabled(true);  //支持js
        mWebView.addJavascriptInterface(getHtmlObject(), "interfaceName");

        mWebView.clearCache(true);
        mWebView.clearHistory();

        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
    }

    /**
     * Html 与 Android 互相调用
     * http://blog.csdn.net/it1039871366/article/details/46372207
     *
     * @return
     */
    private Object getHtmlObject() {
        Object insertObj = new Object() {

            @JavascriptInterface
            public String HtmlcallJava() {//
                return "Html call Java";
            }

            @JavascriptInterface
            public void HtmlcallJava2(final String game, final String id) {//
//                if (!TextUtils.isEmpty(game) && game.equals("game")) {
//                    Intent intent = new Intent(getActivity(), GameDetailActivity.class);
//                    intent.putExtra(GameDetailActivity.GAME_ID, id);
//                    startActivity(intent);
//                }
            }

            @JavascriptInterface
            public void JavacallHtml() {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript: showFromHtml()");
                        ToastUtil.showToast("clickBtn");
                    }
                });
            }

            @JavascriptInterface
            public void JavacallHtml2() {
                requireActivity().runOnUiThread(new Runnable() {
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

    //WebViewClient就是帮助WebView处理各种通知、请求事件的。
    private class MyWebViewClient extends WebViewClient {
        /**
         * 在点击请求的是链接时才会调用，
         * 重写此方法返回true表明点击网页里面的链接还是在当前的webview里跳转，
         * 不跳到浏览器那边。
         * 这个函数我们可以做很多操作，比如我们读取到某些特殊的URL，于是就可以不打开地址，
         * 取消这个操作，进行预先定义的其他操作，这对一个程序是非常必要的。
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return shouldOverrideUrlLoadingImpl(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();//webview处理https请求
//            handler.cancel();
        }

        // 此回调是拦截点击要跳转的url链接，并对请求的url链接做修改（添加删除字段）
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
        }

//        shouldOverrideUrlLoading(WebView view, String url)  最常用的，比如上面的。
//        //在网页上的所有加载都经过这个方法,这个函数我们可以做很多操作。
//        //比如获取url，查看url.contains(“add”)，进行添加操作
//
//        shouldOverrideKeyEvent(WebView view, KeyEvent event)
//        //重写此方法才能够处理在浏览器中的按键事件。
//
//        onPageStarted(WebView view, String url, Bitmap favicon)
//        //这个事件就是开始载入页面调用的，我们可以设定一个loading的页面，告诉用户程序在等待网络响应。
//
//        onPageFinished(WebView view, String url)
//        //在页面加载结束时调用。同样道理，我们可以关闭loading 条，切换程序动作。
//
//        onLoadResource(WebView view, String url)
//        // 在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。
//
//        onReceivedError(WebView view, int errorCode, String description, String failingUrl)
//        // (报告错误信息)
//
//        doUpdateVisitedHistory(WebView view, String url, boolean isReload)
//        //(更新历史记录)
//
//        onFormResubmission(WebView view, Message dontResend, Message resend)
//        //(应用程序重新请求网页数据)
//
//        onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host,String realm)
//        //（获取返回信息授权请求）
//
//        onScaleChanged(WebView view, float oldScale, float newScale)
//        // (WebView发生改变时调用)
//
//        onUnhandledKeyEvent(WebView view, KeyEvent event)
//        //（Key事件未被加载时调用）
    }

    //WebChromeClient是辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度等
    class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mProgressBar.setProgress(newProgress);
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
                //注入js代码测量webView高度
                mWebView.loadUrl("javascript:App.resize(document.body.getBoundingClientRect().height)");
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
            ((BaseActivity) getActivity()).setStatusBar();
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

        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        // For 3.0+ Devices (Start)
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            openFileChooser(uploadMsg, acceptType, null);
        }

        // For Android 4.1 only
        protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            uploadMessage = uploadMsg;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(acceptType);
            fileChooserLauncher.launch(Intent.createChooser(intent, "File Browser"));
        }

        // For Lollipop 5.0+ Devices
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (uploadMessageAboveL != null) {
                uploadMessageAboveL.onReceiveValue(null);
            }
            uploadMessageAboveL = filePathCallback;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            fileChooserLauncher.launch(Intent.createChooser(intent, "File Browser"));
            return true;
        }

        //获取Web页中的title用来设置自己界面中的title
        //当加载出错的时候，比如无网络，这时onReceiveTitle中获取的标题为 找不到该网页,
        //因此建议当触发onReceiveError时，不要使用获取到的title
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
//        //处理alert弹出框，html 弹框的一种方式
//        @Override
//        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//            //
//            return true;
//        }
//
//        //处理confirm弹出框
//        @Override
//        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult
//                result) {
//            //
//            return true;
//        }
//
//        //处理prompt弹出框
//        @Override
//        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
//            //
//            return true;
//        }
    }

    private boolean shouldOverrideUrlLoadingImpl(WebView view, String url) {
        if (view.isPrivateBrowsingEnabled()) {//获取是否在此WebView中启用隐私浏览
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

        view.loadUrl(url);
        return false;
    }
}
