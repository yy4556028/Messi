//package com.yuyang.messi.ui.common;
//
//import java.net.URISyntaxException;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.Locale;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import com.suning.imengine.CookieManagerWrapper;
//import com.suning.imengine.LoginAccountManager;
//import com.suning.imengine.http.executor.HttpExecutorManager;
//import com.suning.imengine.http.util.DownloaderConstant;
//import com.suning.imengine.model.SmallStaffBean;
//import com.suning.imengine.util.LogX;
//import com.suning.imengine.util.MimeTypeUtils;
//import com.suning.imengine.util.ProtocolUtils;
//import com.suning.imsecure.widget.WatermarkView;
//import com.suning.logic.cache.SmallStaffCache;
//import com.suning.logic.threadpool.BusinessRunnable;
//import com.suning.logic.threadpool.ThreadPoolManager;
//import com.suning.sastatistics.StatisticsProcessor;
//import com.suning.snmessenger.R;
//import com.suning.snmessenger.SnApp;
//import com.suning.snmessenger.miniprogram.SMPUtils;
//import com.suning.snmessenger.tools.GlobalTool;
//import com.suning.snmessenger.util.ActivityUtils;
//import com.suning.snmessenger.util.FileUtil;
//import com.suning.snmessenger.widget.CustomDialogFragment;
//import com.suning.snmessenger.workbench.WorkbenchAppTools;
//
//import android.annotation.TargetApi;
//import android.app.Activity;
//import android.app.DownloadManager;
//import android.app.Fragment;
//import android.content.ActivityNotFoundException;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.ActivityInfo;
//import android.content.pm.PackageManager;
//import android.content.pm.ResolveInfo;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Picture;
//import android.net.Uri;
//import android.net.http.SslError;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.DisplayMetrics;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewStub;
//import android.view.Window;
//import android.view.WindowManager;
//import android.webkit.DownloadListener;
//import android.webkit.GeolocationPermissions;
//import android.webkit.JsPromptResult;
//import android.webkit.MimeTypeMap;
//import android.webkit.PermissionRequest;
//import android.webkit.SslErrorHandler;
//import android.webkit.ValueCallback;
//import android.webkit.WebChromeClient;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.Button;
//import android.widget.FrameLayout;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//
//public class BrowserFragment extends Fragment {
//    private static final String TAG = BrowserFragment.class.getSimpleName();
//    private static final String EXTRA_URL = "url";
//    private static final String EXTRA_APP_CODE = "appcode";
//    private static final String EXTRA_WATER_MARK = "watermark";
//    private static final String EXTRA_ADD_IMAGE_CLICK_LISTENER = "add_image_click_listener";
//    private static final String EXTRA_SECURITY = "security";
//    private final static String SCHEME_WTAI = "wtai://wp/";
//    private final static String SCHEME_WTAI_MC = "wtai://wp/mc;";
//    private final static String SCHEME_WTAI_SD = "wtai://wp/sd;";
//    private final static String SCHEME_WTAI_AP = "wtai://wp/ap;";
//    private static final Pattern ACCEPTED_URI_SCHEMA = Pattern.compile("(?i)" + "("
//            + "(?:http|https|file):\\/\\/" + "|(?:inline|data|about|javascript):" + ")" + "(.*)");
//
//    /** Regex used to parse content-disposition plus headers */
//    private static final Pattern CONTENT_DISPOSITION_PATTERN_PLUS = Pattern.compile(
//            "attachment;\\s*filename\\s*=\\s*(\"?)([^\"]*)\\1\\s*(;\\s*filename\\*=([^\"'\\s]+)''([^\"]+))?",
//            Pattern.CASE_INSENSITIVE);
//
//    /** Regex used to parse content-disposition headers */
//    private static final Pattern CONTENT_DISPOSITION_PATTERN = Pattern.compile(
//            "attachment;\\s*filename\\s*=\\s*(\"?)([^\"]*)\\1\\s*$", Pattern.CASE_INSENSITIVE);
//
//    private static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//
//    private Activity mActivity;
//    private String mUrl;
//    private String mDownloadUrl;
//    private String mAppCode;
//    private boolean mWatermark = false;
//    private boolean mAddImageClickListener = false;
//    private boolean mSecurity = false;
//
//    private ValueCallback<Uri> mUploadMessage;
//    private ValueCallback<Uri[]> mUploadFilePathCallback;
//
//    private FrameLayout mWindowContainer;
//    private ArrayList<BrowserWindowController> mWindowControllerStack = new ArrayList<BrowserWindowController>();
//    private ProgressBar mProgressBar = null;
//    private View mFailureLayout;
//    private Button mRetryBtn;
//
//    private View mContentView;
//    private View mCustomView;
//    private WebChromeClient.CustomViewCallback mCustomViewCallback;
//    private FrameLayout mFullscreenContainer;
//    private int mOriginalOrientation;
//    private WatermarkView mWatermarkView;
//    private UploadHandler mUploadHandler;
//
//    // The Geolocation permissions prompt
//    private GeolocationPermissionsPrompt mGeolocationPermissionsPrompt;
//
//    // The permissions prompt
//    private PermissionsPrompt mPermissionsPrompt;
//    private HashSet<String> mAddImageClickListenerUrlSet = new HashSet<String>();
//    private ArrayList<String> mInsecurityUrls = new ArrayList<>();
//
//    private static class FullScreenHolder extends FrameLayout {
//        public FullScreenHolder(Context ctx) {
//            super(ctx);
//            setBackgroundColor(ctx.getResources().getColor(R.color.black));
//        }
//
//        @Override
//        public boolean onTouchEvent(MotionEvent evt) {
//            return true;
//        }
//    }
//
//    public BrowserFragment() {
//    }
//
//    public static BrowserFragment newInstance(String url, String appCode, boolean watermark,
//            boolean addImageClickListener, boolean security) {
//        BrowserFragment fragment = new BrowserFragment();
//        Bundle args = new Bundle();
//        args.putString(EXTRA_URL, url);
//        args.putString(EXTRA_APP_CODE, appCode);
//        args.putBoolean(EXTRA_WATER_MARK, watermark);
//        args.putBoolean(EXTRA_ADD_IMAGE_CLICK_LISTENER, addImageClickListener);
//        args.putBoolean(EXTRA_SECURITY, security);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            WebView.enableSlowWholeDocumentDraw();
//        }
//        Bundle args = getArguments();
//        if (args != null) {
//            mUrl = args.getString(EXTRA_URL, null);
//            mAppCode = args.getString(EXTRA_APP_CODE, null);
//            mWatermark = args.getBoolean(EXTRA_WATER_MARK);
//            mAddImageClickListener = args.getBoolean(EXTRA_ADD_IMAGE_CLICK_LISTENER, false);
//            mSecurity = args.getBoolean(EXTRA_SECURITY, false);
//        }
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
//            Bundle savedInstanceState) {
//        mContentView = inflater.inflate(R.layout.fragment_browser_layout, null);
//        mWatermarkView = (WatermarkView) mContentView.findViewById(R.id.waterView);
//        SmallStaffBean staff = SmallStaffCache.get(LoginAccountManager.getAccount().getUid());
//        if (mWatermark && staff != null) {
//            String name = staff.getName();
//            String code = staff.getEmployeeCode();
//            mWatermarkView.setVisibility(View.VISIBLE);
//            mWatermarkView.setWatermarkText(
//                    TextUtils.isEmpty(code) ? getString(R.string.watermark_format_1, name)
//                            : getString(R.string.watermark_format_2, name, code));
//        } else {
//            mWatermarkView.setVisibility(View.GONE);
//        }
//        mProgressBar = (ProgressBar) mContentView.findViewById(R.id.progress_bar);
//        mWindowContainer = (FrameLayout) mContentView.findViewById(R.id.window_container);
//        mFailureLayout = mContentView.findViewById(R.id.layout_error);
//        mRetryBtn = mContentView.findViewById(R.id.retry_button);
//        mRetryBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                reload();
//            }
//        });
//        BrowserWindowController controller = createWindow(mContentView.getContext());
//        if (TextUtils.isEmpty(mUrl)) {
//            showToast(R.string.url_is_empty, Toast.LENGTH_LONG);
//            mActivity.finish();
//        } else {
//            controller.getWebView().loadUrl(mUrl);
//
//        }
//        return mContentView;
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        mActivity = getActivity();
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        mActivity = getActivity();
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mActivity = null;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (!mWindowControllerStack.isEmpty()) {
//            BrowserWindowController c = mWindowControllerStack
//                    .get(mWindowControllerStack.size() - 1);
//            c.getWebView().onResume();
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (!mWindowControllerStack.isEmpty()) {
//            BrowserWindowController c = mWindowControllerStack
//                    .get(mWindowControllerStack.size() - 1);
//            c.getWebView().onPause();
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        clearWindow();
//    }
//
//    public boolean goBack() {
//        if (isCustomViewShowing()) {
//            hideCustomView();
//            return true;
//        }
//
//        if (mWindowControllerStack.isEmpty()) {
//            return false;
//        }
//
//        int size = mWindowControllerStack.size();
//        BrowserWindowController controller = mWindowControllerStack.get(size - 1);
//        if (!controller.goBack()) {
//            if (size == 1) {
//                return false;
//            }
//            popWindow();
//        }
//
//        hideFailureLayout();
//
//        return true;
//    }
//
//    public boolean canGoBack() {
//        if (isCustomViewShowing()) {
//            return true;
//        }
//
//        if (mWindowControllerStack.isEmpty()) {
//            return false;
//        }
//
//        int size = mWindowControllerStack.size();
//        BrowserWindowController controller = mWindowControllerStack.get(size - 1);
//        if (!controller.canGoBack()) {
//            if (size == 1) {
//                return false;
//            }
//        }
//
//        return true;
//    }
//
//    public void reload() {
//        hideFailureLayout();
//        BrowserWindowController controller = getTopWindow();
//        if (null == controller) {
//            return;
//        }
//        controller.reload();
//    }
//
//    private void setupWebView(WebView wv) {
//        wv.requestFocus(View.FOCUS_DOWN);
//        if (mSecurity) {
//            wv.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    return true;
//                }
//            });
//        }
//        startManagingSettings(wv.getSettings());
//        wv.setDownloadListener(new DownloadListener() {
//            @Override
//            public void onDownloadStart(String url, String userAgent, String contentDisposition,
//                    String mimeType, long contentLength) {
//                // Activity detached.
//                if (null == mActivity) {
//                    return;
//                }
//
//                mDownloadUrl = url;
//
//                // TODO by chensijia temporarily amend contentDisposition
//                if (!TextUtils.isEmpty(contentDisposition)
//                        && contentDisposition.startsWith("filename=")) {
//                    contentDisposition = "attachment;" + contentDisposition;
//                }
//
//                // if we're dealing wih A/V content that's not explicitly marked
//                // for download, check if it's streamable.
//                if (contentDisposition == null
//                        || !contentDisposition.regionMatches(true, 0, "attachment", 0, 10)) {
//                    // query the package manager to see if there's a registered handler
//                    // that matches.
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    FileUtil.setDataAndTypeForIntent(intent, mActivity, url, mimeType);
//                    ResolveInfo info = mActivity.getPackageManager().resolveActivity(intent,
//                            PackageManager.MATCH_DEFAULT_ONLY);
//                    if (info != null) {
//                        ComponentName myName = mActivity.getComponentName();
//                        // If we resolved to ourselves, we don't want to attempt to
//                        // load the url only to try and download it again.
//                        if (!myName.getPackageName().equals(info.activityInfo.packageName)
//                                || !myName.getClassName().equals(info.activityInfo.name)) {
//                            // someone (other than us) knows how to handle this mime
//                            // type with this scheme, don't download.
//                            try {
//                                mActivity.startActivity(intent);
//                                return;
//                            } catch (ActivityNotFoundException ex) {
//                                LogX.d(TAG, "activity not found for " + mimeType + " over "
//                                        + Uri.parse(url).getScheme());
//                                // Best behavior is to fall back to a download in this
//                                // case
//                            } catch (SecurityException ex) {
//                                LogX.d(TAG, "activity not start for securityException, " + mimeType
//                                        + " over " + Uri.parse(url).getScheme());
//                                // Best behavior is to fall back to a download in this
//                                // case
//                            }
//                        }
//                    }
//                }
//                onDownloadStartNoStream(url, userAgent, contentDisposition, mimeType);
//            }
//        });
//        wv.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture,
//                    Message resultMsg) {
//                BrowserWindowController controller = createWindow(view.getContext());
//                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
//                transport.setWebView(controller.getWebView());
//                resultMsg.sendToTarget();
//                return true;
//            }
//
//            @Override
//            public void onCloseWindow(WebView window) {
//                popWindow();
//
//                if (mWindowControllerStack.size() <= 0) {
//                    if (mActivity != null) {
//                        mActivity.finish();
//                    }
//
//                    return;
//                }
//
//                hideFailureLayout();
//            }
//
//            @Override
//            public void onShowCustomView(View view, CustomViewCallback callback) {
//                if (mActivity != null) {
//                    onShowCustomView(view, mActivity.getRequestedOrientation(), callback);
//                }
//            }
//
//            @Override
//            public void onShowCustomView(View view, int requestedOrientation,
//                    CustomViewCallback callback) {
//                showCustomView(view, requestedOrientation, callback);
//            }
//
//            @Override
//            public void onHideCustomView() {
//                hideCustomView();
//            }
//
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                // newProgress 0 --- 100
//                super.onProgressChanged(view, newProgress);
//                mProgressBar.setProgress(newProgress);
//                if (newProgress == 100) {
//                    mProgressBar.setVisibility(View.GONE);
//                }
//            }
//
//            // For 3.0+ Devices (Start)
//            protected void openFileChooser(ValueCallback uploadMsg, String acceptType) {
//                mUploadMessage = uploadMsg;
//                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//                i.addCategory(Intent.CATEGORY_OPENABLE);
//                i.setType(acceptType);
//                ActivityUtils.startActivityForResultSafely(BrowserFragment.this,
//                        Intent.createChooser(i, "File Browser"),
//                        ActivityUtils.REQUEST_CODE_FILE_CHOOSER);
//            }
//
//            // For Android 4.1 only
//            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType,
//                    String capture) {
//                mUploadMessage = uploadMsg;
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setType(acceptType);
//                ActivityUtils.startActivityForResultSafely(BrowserFragment.this,
//                        Intent.createChooser(intent, "File Browser"),
//                        ActivityUtils.REQUEST_CODE_FILE_CHOOSER);
//            }
//
//            // For Lollipop 5.0+ Devices
//            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePathCallback,
//                    FileChooserParams fileChooserParams) {
//                if (mUploadFilePathCallback != null) {
//                    mUploadFilePathCallback.onReceiveValue(null);
//                    mUploadFilePathCallback = null;
//                }
//
//                mUploadFilePathCallback = filePathCallback;
//                mUploadHandler = new UploadHandler(BrowserFragment.this);
//                if (!mUploadHandler.openFileChooser(filePathCallback, fileChooserParams)) {
//                    mUploadFilePathCallback = null;
//                    mUploadHandler = null;
//                    return false;
//                }
//                return true;
//            }
//
//            protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
//                mUploadMessage = uploadMsg;
//                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//                i.addCategory(Intent.CATEGORY_OPENABLE);
//                ActivityUtils.startActivityForResultSafely(BrowserFragment.this,
//                        Intent.createChooser(i, "File Chooser"),
//                        ActivityUtils.REQUEST_CODE_FILE_CHOOSER);
//            }
//
//            @Override
//            public void onReceivedTitle(WebView view, String title) {
//                super.onReceivedTitle(view, title);
//                if (mActivity != null && mActivity instanceof IWebViewCallback) {
//                    ((IWebViewCallback) mActivity).updateTitle(title, canGoBack());
//                }
//            }
//
//            @Override
//            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
//                    JsPromptResult result) {
//                result.cancel();
//                StatisticsProcessor.onSDkJSPromt(message, defaultValue);
//                return true;
//            }
//
//            /**
//             * Instructs the browser to show a prompt to ask the user to set the Geolocation
//             * permission state for the specified origin.
//             *
//             * @param origin
//             *            The origin for which Geolocation permissions are requested.
//             * @param callback
//             *            The callback to call once the user has set the Geolocation permission
//             *            state.
//             */
//            @Override
//            public void onGeolocationPermissionsShowPrompt(String origin,
//                    GeolocationPermissions.Callback callback) {
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//                    return;
//                }
//
//                getGeolocationPermissionsPrompt().show(origin, callback);
//            }
//
//            /**
//             * Instructs the browser to hide the Geolocation permissions prompt.
//             */
//            @Override
//            public void onGeolocationPermissionsHidePrompt() {
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//                    return;
//                }
//
//                if (mGeolocationPermissionsPrompt != null) {
//                    mGeolocationPermissionsPrompt.hide();
//                }
//            }
//
//            @Override
//            public void onPermissionRequest(PermissionRequest request) {
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//                    return;
//                }
//
//                getPermissionsPrompt().show(request);
//            }
//
//            @Override
//            public void onPermissionRequestCanceled(PermissionRequest request) {
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//                    return;
//                }
//
//                if (mPermissionsPrompt != null) {
//                    mPermissionsPrompt.hide();
//                }
//            }
//        });
//
//        wv.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                handler.proceed();
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                mProgressBar.setVisibility(View.VISIBLE);
//                mProgressBar.setProgress(15);
//                if (mActivity != null && mActivity instanceof IWebViewCallback) {
//                    ((IWebViewCallback) mActivity).setInsecurityIconVisibility(View.GONE);
//                }
//                checkInsecurityUrl(url);
//            }
//
//            @Override
//            public void onLoadResource(WebView view, String url) {
//                super.onLoadResource(view, url);
//                checkInsecurityUrl(url);
//            }
//
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                return shouldOverrideUrlLoadingImpl(view, url);
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                LogX.d(TAG, "onPageFinished() ---- url=" + url);
//                super.onPageFinished(view, url);
//                mProgressBar.setVisibility(View.GONE);
//                if (mActivity != null && mActivity instanceof IWebViewCallback) {
//                    String title = view.getTitle();
//                    if (TextUtils.equals("http://" + title, url)
//                            || TextUtils.equals("https://" + title, url)) {
//                        title = null;
//                    }
//                    ((IWebViewCallback) mActivity).updateTitle(title, canGoBack());
//                    CookieManagerWrapper.sync();
//                    if (mAddImageClickListener) {
//                        addImageClickListener(url, view);
//                    }
//
//                    if (!mInsecurityUrls.isEmpty()) {
//                        ArrayList<String> list = new ArrayList<>(mInsecurityUrls);
//                        mInsecurityUrls.clear();
//                        String tmpTitle = title;
//                        ThreadPoolManager.execute(new BusinessRunnable() {
//                            @Override
//                            public void run() {
//                                HttpExecutorManager.submitInsecurityUrlLog(tmpTitle, list);
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onReceivedError(WebView view, int errorCode, String description,
//                    String failingUrl) {
//                if (TextUtils.equals(mDownloadUrl, failingUrl)) {
//                    return;
//                }
//
//                super.onReceivedError(view, errorCode, description, failingUrl);
//                showFailureLayout();
//            }
//
//            @Override
//            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
//                super.doUpdateVisitedHistory(view, url, isReload);
//                view.loadUrl(StatisticsProcessor.NAVIGATOR_SAKEY);
//            }
//        });
//
//        CookieManagerWrapper.setAcceptThirdPartyCookies(wv, true);
//    }
//
//    public boolean shouldOverrideUrlLoadingImpl(WebView view, String url) {
//        if (SMPUtils.PATTERN_SMP_URL.matcher(url).matches()) {
//            WorkbenchAppTools.loadMPWithCurrentApp(view.getContext(), url);
//            return true;
//        }
//        if (view.isPrivateBrowsingEnabled()) {
//            return false;
//        }
//        if (url.startsWith(SCHEME_WTAI)) {
//            if (url.startsWith(SCHEME_WTAI_MC)) {
//                Intent intent = new Intent(Intent.ACTION_VIEW,
//                        Uri.parse(WebView.SCHEME_TEL + url.substring(SCHEME_WTAI_MC.length())));
//                startActivity(intent);
//                return true;
//            }
//            if (url.startsWith(SCHEME_WTAI_SD)) {
//                return false;
//            }
//            if (url.startsWith(SCHEME_WTAI_AP)) {
//                return false;
//            }
//        }
//
//        if (url.startsWith("about:")) {
//            return false;
//        }
//        if (startActivityForUrl(url)) {
//            return true;
//        }
//        return false;
//    }
//
//    private void addImageClickListener(String url, WebView wv) {
//        if (mAddImageClickListenerUrlSet.contains(url)) {
//            return;
//        }
//        mAddImageClickListenerUrlSet.add(url);
//
//        wv.loadUrl(
//                "javascript:(function(){document.addEventListener('click',function(event){ if(event.target.tagName==='IMG' && event.target.onclick != 'function'){window.imJsInterface.openImage(event.target.src); }}) })()");
//    }
//
//    private void onDownloadStartNoStream(String url, String userAgent, String contentDisposition,
//            String mimeType) {
//        String fileName = guessFileNamePlus(url, contentDisposition, mimeType);
//        fileName = FileUtil.getValidFileName(fileName);
//        // Check to see if we have an SDCard
//        String status = Environment.getExternalStorageState();
//        if (!status.equals(Environment.MEDIA_MOUNTED)) {
//            int title;
//            String msg;
//
//            // Check to see if the SDCard is busy, same as the music app
//            if (status.equals(Environment.MEDIA_SHARED)) {
//                msg = mActivity.getString(R.string.download_sdcard_busy_dlg_msg);
//                title = R.string.download_sdcard_busy_dlg_title;
//            } else {
//                msg = mActivity.getString(R.string.download_no_sdcard_dlg_msg, fileName);
//                title = R.string.download_no_sdcard_dlg_title;
//            }
//
//            CustomDialogFragment dialog = new CustomDialogFragment.SingleButtonBuilder(mActivity)
//                    .setTitle(title).setMessage(msg).setNegativeButton(R.string.ok, null).create();
//            dialog.show(getFragmentManager(), null);
//            return;
//        }
//
//        // java.net.URI is a lot stricter than KURL so we have to encode some
//        // extra characters. Fix for b 2538060 and b 1634719
//        WebAddress webAddress;
//        try {
//            webAddress = new WebAddress(url);
//            webAddress.setPath(encodePath(webAddress.getPath()));
//        } catch (Exception e) {
//            // This only happens for very bad urls, we want to chatch the
//            // exception here
//            LogX.e(TAG, e);
//            return;
//        }
//
//        String addressString = webAddress.toString();
//        Uri uri = Uri.parse(addressString);
//        final DownloadManager.Request request;
//        try {
//            request = new DownloadManager.Request(uri);
//        } catch (IllegalArgumentException e) {
//            Toast.makeText(mActivity, R.string.cannot_download, Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        request.setMimeType(MimeTypeUtils.amendMimeType(fileName, mimeType));
//        try {
//            request.setDestinationInExternalPublicDir(DownloaderConstant.DIRECTORY_DOWNLOADS,
//                    fileName);
//        } catch (IllegalStateException ex) {
//            LogX.w(TAG, "Exception trying to create Download dir:" + ex.toString());
//            Toast.makeText(mActivity, R.string.download_sdcard_busy_dlg_title, Toast.LENGTH_SHORT)
//                    .show();
//            return;
//        }
//        // let this downloaded file be scanned by MediaScanner - so that it can
//        // show up in Gallery app, for example.
//        request.allowScanningByMediaScanner();
//        request.setDescription(webAddress.getHost());
//        if (!TextUtils.isEmpty(fileName)) {
//            request.setTitle(fileName);
//        }
//        // XXX: Have to use the old url since the cookies were stored using the
//        // old percent-encoded url.
//        String cookies = CookieManagerWrapper.getCookie(url);
//        request.addRequestHeader("Cookie", cookies);
//        request.addRequestHeader("User-Agent", userAgent);
//        request.setNotificationVisibility(
//                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        if (mimeType == null) {
//            if (TextUtils.isEmpty(addressString)) {
//                return;
//            }
//            // We must have long pressed on a link or image to download it. We
//            // are not sure of the mimeType in this case, so do a head request
//            new FetchUrlMimeType(mActivity, request, addressString, null, userAgent).start();
//        } else {
//            final DownloadManager manager = (DownloadManager) mActivity
//                    .getSystemService(Context.DOWNLOAD_SERVICE);
//            new Thread("Browser download") {
//                public void run() {
//                    manager.enqueue(request);
//                }
//            }.start();
//        }
//        Toast.makeText(mActivity, R.string.download_pending, Toast.LENGTH_SHORT).show();
//    }
//
//    /*
//     * Parse the encoded filename as per https://tools.ietf.org/html/rfc5987
//     */
//    static String parseEncodedFileName(String contentDisposition) {
//        try {
//            Matcher m = CONTENT_DISPOSITION_PATTERN_PLUS.matcher(contentDisposition);
//            if (m.find()) {
//                return m.group(5);
//            }
//        } catch (IllegalStateException ex) {
//            // This function is defined as returning null when it can't parse the header
//        }
//        return null;
//    }
//
//    /**
//     * recognize filename*=utf-8''%e2%82%ac%20exchange%20rates
//     */
//    public static final String guessFileNamePlus(String url, String contentDisposition,
//            String mimeType) {
//        if (contentDisposition != null) {
//            String filename = parseEncodedFileName(contentDisposition);
//            if (filename != null) {
//                int index = filename.lastIndexOf('/') + 1;
//                if (index > 0) {
//                    filename = filename.substring(index);
//                }
//                return Uri.decode(filename);
//            }
//        }
//
//        return guessFileName(url, contentDisposition, mimeType);
//    }
//
//    /*
//     * Parse the Content-Disposition HTTP Header. The format of the header is defined here:
//     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec19.html This header provides a filename for
//     * content that is going to be downloaded to the file system. We only support the attachment
//     * type. Note that RFC 2616 specifies the filename value must be double-quoted. Unfortunately
//     * some servers do not quote the value so to maintain consistent behaviour with other browsers,
//     * we allow unquoted values too.
//     */
//    static String parseContentDisposition(String contentDisposition) {
//        try {
//            Matcher m = CONTENT_DISPOSITION_PATTERN.matcher(contentDisposition);
//            if (m.find()) {
//                return m.group(2);
//            }
//        } catch (IllegalStateException ex) {
//            // This function is defined as returning null when it can't parse the header
//        }
//        return null;
//    }
//
//    /**
//     * Guesses canonical filename that a download would have, using the URL and contentDisposition.
//     * File extension, if not defined, is added based on the mimetype
//     *
//     * @param url
//     *            Url to the content
//     * @param contentDisposition
//     *            Content-Disposition HTTP header or null
//     * @param mimeType
//     *            Mime-type of the content or null
//     *
//     * @return suggested filename
//     */
//    public static final String guessFileName(String url, String contentDisposition,
//            String mimeType) {
//        String filename = null;
//
//        // If we couldn't do anything with the hint, move toward the content disposition
//        if (contentDisposition != null) {
//            filename = parseContentDisposition(contentDisposition);
//            if (filename != null) {
//                int index = filename.lastIndexOf('/') + 1;
//                if (index > 0) {
//                    filename = filename.substring(index);
//                }
//            }
//        }
//
//        // If all the other http-related approaches failed, use the plain uri
//        if (filename == null) {
//            String decodedUrl = Uri.decode(url);
//            if (decodedUrl != null) {
//                int queryIndex = decodedUrl.indexOf('?');
//                // If there is a query string strip it, same as desktop browsers
//                if (queryIndex > 0) {
//                    decodedUrl = decodedUrl.substring(0, queryIndex);
//                }
//                if (!decodedUrl.endsWith("/")) {
//                    int index = decodedUrl.lastIndexOf('/') + 1;
//                    if (index > 0) {
//                        filename = decodedUrl.substring(index);
//                    }
//                }
//            }
//        }
//
//        // Finally, if couldn't get filename from URI, get a generic filename
//        if (filename == null) {
//            filename = "downloadfile";
//        }
//
//        // Split filename between base and extension
//        // Add an extension if filename does not have one
//        int dotIndex = filename.indexOf('.');
//        String extension = null;
//        if (dotIndex < 0) {
//            if (mimeType != null) {
//                extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
//                if (extension != null) {
//                    extension = "." + extension;
//                }
//            }
//            if (extension == null) {
//                if (mimeType != null && mimeType.toLowerCase(Locale.ROOT).startsWith("text/")) {
//                    if (mimeType.equalsIgnoreCase("text/html")) {
//                        extension = ".html";
//                    } else {
//                        extension = ".txt";
//                    }
//                } else {
//                    extension = ".bin";
//                }
//            }
//
//            filename += extension;
//        } else {
//            // Do nothing.
//        }
//
//        return filename;
//    }
//
//    private static String encodePath(String path) {
//        char[] chars = path.toCharArray();
//
//        boolean needed = false;
//        for (char c : chars) {
//            if (c == '[' || c == ']' || c == '|') {
//                needed = true;
//                break;
//            }
//        }
//        if (needed == false) {
//            return path;
//        }
//
//        StringBuilder sb = new StringBuilder("");
//        for (char c : chars) {
//            if (c == '[' || c == ']' || c == '|') {
//                sb.append('%');
//                sb.append(Integer.toHexString(c));
//            } else {
//                sb.append(c);
//            }
//        }
//
//        return sb.toString();
//    }
//
//    private void startManagingSettings(WebSettings settings) {
//        settings.setUserAgentString(settings.getUserAgentString() + String.format(" douya/%s/%s",
//                GlobalTool.getCurrentVersionName(), ProtocolUtils.PLATFORM));
//
//        settings.setDefaultFontSize(16);
//        settings.setDefaultFixedFontSize(13);
//
//        // WebView inside Browser doesn't want initial focus to be set.
//        settings.setNeedInitialFocus(false);
//
//        // disable content url access
//        settings.setAllowContentAccess(false);
//
//        // HTML5 API flags
//        settings.setAppCacheEnabled(true);
//        settings.setDatabaseEnabled(true);
//        settings.setDomStorageEnabled(true);
//
//        settings.setGeolocationEnabled(true);
//        settings.setJavaScriptEnabled(true);
//        settings.setJavaScriptCanOpenWindowsAutomatically(true);
//        settings.setDefaultTextEncodingName(null);
//        settings.setLoadsImagesAutomatically(true);
//        settings.setLoadWithOverviewMode(true);
//        settings.setUseWideViewPort(true);
//        settings.setBuiltInZoomControls(true);
//        settings.setSupportZoom(true);
//        settings.setDisplayZoomControls(false);
//        settings.setSupportMultipleWindows(true);
//        settings.setAllowFileAccess(false);
//
//        // Security policy
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
//        }
//
//        if (mActivity != null) {
//            DisplayMetrics metrics = new DisplayMetrics();
//            mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//            int density = metrics.densityDpi;
//            if (density == DisplayMetrics.DENSITY_LOW) {
//                settings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
//            } else if (density == DisplayMetrics.DENSITY_MEDIUM) {
//                settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
//            } else if (density == DisplayMetrics.DENSITY_HIGH) {
//                settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
//            }
//        }
//    }
//
//    private BrowserWindowController createWindow(Context context) {
//        BrowserWindowController controller = new BrowserWindowController(mActivity, context,
//                mAppCode);
//        pushWindow(controller);
//        setupWebView(controller.getWebView());
//
//        return controller;
//    }
//
//    private void pushWindow(BrowserWindowController controller) {
//        hideAllPrompts();
//
//        mWindowControllerStack.add(controller);
//        mWindowContainer.addView(controller.getWebView());
//    }
//
//    private void popWindow() {
//        hideAllPrompts();
//
//        BrowserWindowController c = mWindowControllerStack
//                .remove(mWindowControllerStack.size() - 1);
//        mWindowContainer.removeView(c.getWebView());
//        if (mActivity != null && mActivity instanceof IWebViewCallback
//                && mWindowControllerStack.size() >= 1) {
//            BrowserWindowController controller = mWindowControllerStack
//                    .get(mWindowControllerStack.size() - 1);
//            ((IWebViewCallback) mActivity).updateTitle(controller.getWebView().getTitle(),
//                    canGoBack());
//        }
//        c.release();
//    }
//
//    private BrowserWindowController getTopWindow() {
//        if (mWindowControllerStack.isEmpty()) {
//            return null;
//        }
//        return mWindowControllerStack.get(mWindowControllerStack.size() - 1);
//    }
//
//    private void clearWindow() {
//        for (BrowserWindowController c : mWindowControllerStack) {
//            c.release();
//        }
//        mWindowControllerStack.clear();
//    }
//
//    public void showCustomView(View view, int requestedOrientation,
//            WebChromeClient.CustomViewCallback callback) {
//        if (null == mActivity) {
//            return;
//        }
//
//        // if a view already exists then immediately terminate the new one
//        if (mCustomView != null) {
//            callback.onCustomViewHidden();
//            return;
//        }
//
//        mOriginalOrientation = mActivity.getRequestedOrientation();
//        FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
//        mFullscreenContainer = new FullScreenHolder(mActivity);
//        mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
//        decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
//        mCustomView = view;
//        setFullscreen(true);
//        mWindowContainer.setVisibility(View.INVISIBLE);
//        mCustomViewCallback = callback;
//        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//    }
//
//    public void hideCustomView() {
//        if (null == mActivity) {
//            return;
//        }
//
//        mWindowContainer.setVisibility(View.VISIBLE);
//        if (mCustomView == null) {
//            return;
//        }
//        setFullscreen(false);
//        FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
//        decor.removeView(mFullscreenContainer);
//        mFullscreenContainer = null;
//        mCustomView = null;
//        mCustomViewCallback.onCustomViewHidden();
//        // Show the content view.
//        mActivity.setRequestedOrientation(mOriginalOrientation);
//    }
//
//    private boolean isCustomViewShowing() {
//        return mCustomView != null;
//    }
//
//    private void setFullscreen(boolean enabled) {
//        if (null == mActivity) {
//            return;
//        }
//
//        Window win = mActivity.getWindow();
//        WindowManager.LayoutParams winParams = win.getAttributes();
//        final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
//        if (enabled) {
//            winParams.flags |= bits;
//        } else {
//            winParams.flags &= ~bits;
//            if (mCustomView != null) {
//                mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//            } else {
//                mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//            }
//        }
//        win.setAttributes(winParams);
//    }
//
//    private boolean startActivityForUrl(String url) {
//        if (mActivity == null) {
//            LogX.w(TAG, "startActivityForUrl failed.mActivity is null.");
//            return false;
//        }
//        Intent intent;
//        try {
//            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
//        } catch (URISyntaxException ex) {
//            return false;
//        }
//        if (mActivity.getPackageManager().resolveActivity(intent, 0) == null) {
//            return false;
//        }
//
//        intent.addCategory(Intent.CATEGORY_BROWSABLE);
//        intent.setComponent(null);
//        intent.setSelector(null);
//        Matcher m = ACCEPTED_URI_SCHEMA.matcher(url);
//        if (m.matches()) {
//            return false;
//        }
//
//        if (startActivitySafetyIfNeeded(intent, -1)) {
//            return true;
//        }
//
//        return false;
//    }
//
//    private boolean startActivitySafetyIfNeeded(Intent intent, int resultCode) {
//        Activity activity = mActivity.getParent();
//        if (activity == null) {
//            activity = mActivity;
//        }
//        try {
//            if (activity.startActivityIfNeeded(intent, resultCode)) {
//                return true;
//            }
//        } catch (ActivityNotFoundException ex) {
//            Toast.makeText(activity, R.string.no_app_can_open, Toast.LENGTH_LONG).show();
//            return false;
//        } catch (SecurityException ex) {
//            Toast.makeText(activity, R.string.no_app_can_open, Toast.LENGTH_LONG).show();
//            return false;
//        }
//        return false;
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        if (requestCode == ActivityUtils.REQUEST_CODE_SELECT_FILE) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                if (mUploadFilePathCallback == null) {
//                    return;
//                }
//                mUploadHandler.onResult(resultCode, intent);
//                mUploadFilePathCallback = null;
//                mUploadHandler = null;
//            }
//        } else if (requestCode == ActivityUtils.REQUEST_CODE_FILE_CHOOSER) {
//            if (null == mUploadMessage)
//                return;
//            Uri result = intent == null || resultCode != Activity.RESULT_OK ? null
//                    : intent.getData();
//            mUploadMessage.onReceiveValue(result);
//            mUploadMessage = null;
//        }
//    }
//
//    private void showToast(int resId, int duration) {
//        Toast.makeText(SnApp.getAppContext(), resId, duration).show();
//    }
//
//    private void showFailureLayout() {
//        mFailureLayout.setVisibility(View.VISIBLE);
//        mWindowContainer.setVisibility(View.GONE);
//    }
//
//    private void hideFailureLayout() {
//        mFailureLayout.setVisibility(View.GONE);
//        mWindowContainer.setVisibility(View.VISIBLE);
//    }
//
//    public Bitmap generateTopWindowImageByH5() {
//        BrowserWindowController controller = getTopWindow();
//        if (controller == null || controller.getWebView() == null) {
//            return null;
//        }
//        WebView wv = controller.getWebView();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            float scale = wv.getScale();
//            int width = wv.getWidth();
//            int height = (int) (wv.getContentHeight() * scale + 0.5);
//            if (width > 0 && height > 0) {
//                Bitmap bp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
//                Canvas canvas = new Canvas(bp);
//                wv.draw(canvas);
//                return bp;
//            }
//            return null;
//        } else {
//            Picture picture = wv.capturePicture();
//            int width = picture.getWidth();
//            int height = picture.getHeight();
//            if (width > 0 && height > 0) {
//                Bitmap bp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
//                Canvas canvas = new Canvas(bp);
//                picture.draw(canvas);
//                return bp;
//            }
//            return null;
//        }
//    }
//
//    /**
//     * @return The geolocation permissions prompt for this tab.
//     */
//    GeolocationPermissionsPrompt getGeolocationPermissionsPrompt() {
//        if (mGeolocationPermissionsPrompt == null) {
//            ViewStub stub = (ViewStub) mContentView
//                    .findViewById(R.id.geolocation_permissions_prompt);
//            mGeolocationPermissionsPrompt = (GeolocationPermissionsPrompt) stub.inflate();
//        }
//        return mGeolocationPermissionsPrompt;
//    }
//
//    /**
//     * @return The permissions prompt for this tab.
//     */
//    PermissionsPrompt getPermissionsPrompt() {
//        if (mPermissionsPrompt == null) {
//            ViewStub stub = (ViewStub) mContentView.findViewById(R.id.permissions_prompt);
//            mPermissionsPrompt = (PermissionsPrompt) stub.inflate();
//        }
//        return mPermissionsPrompt;
//    }
//
//    public void hideAllPrompts() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            return;
//        }
//
//        // If the WebView is changing, the page will be reloaded, so any ongoing
//        // Geolocation permission requests are void.
//        if (mGeolocationPermissionsPrompt != null) {
//            mGeolocationPermissionsPrompt.hide();
//        }
//
//        if (mPermissionsPrompt != null) {
//            mPermissionsPrompt.hide();
//        }
//    }
//
//    private void checkInsecurityUrl(String url) {
//        if (TextUtils.isEmpty(url)) {
//            return;
//        }
//        if (url.toLowerCase().startsWith("http://")) {
//            LogX.d(TAG, url);
//            mInsecurityUrls.add(url);
//            if (mActivity != null && mActivity instanceof IWebViewCallback) {
//                ((IWebViewCallback) mActivity).setInsecurityIconVisibility(View.VISIBLE);
//            }
//        }
//    }
//}
