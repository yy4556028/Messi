package com.yuyang.messi.ui.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuyang.lib_base.utils.SystemBarUtil;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.lib_share.ShareSDKAuthHelper;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.utils.ActSwitchAnimTool;
import com.yuyang.messi.utils.FontUtil;
import com.yuyang.messi.view.Progress.CustomProgressDialog;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppBaseActivity {

    private TextView phoneLoginText;

    private ImageView weiboLoginButton;
    private ImageView wechatLoginButton;
    private ImageView facebookLoginButton;
    private ImageView twitterLoginButton;

    private CustomProgressDialog progressDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        SystemBarUtil.configBar(getActivity(), true, false, false, false, true);

        initView();
        initEvent();
        new ActSwitchAnimTool(this)
                .receiveIntent(getIntent())
                .setAnimType(ActSwitchAnimTool.MODE_SHRINK)
                .target(wechatLoginButton)
                .build();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new ActSwitchAnimTool(this)
                    .setAnimType(ActSwitchAnimTool.MODE_SPREAD)
                    .target(wechatLoginButton)
                    .setmColorStart(Color.RED)
                    .setmColorEnd(Color.RED)
                    .setCustomEndCallBack(new ActSwitchAnimTool.SwitchAnimCallback() {
                        @Override
                        public void onAnimationStart() {
                        }

                        @Override
                        public void onAnimationEnd() {
                            finish();
                        }

                        @Override
                        public void onAnimationUpdate(int progress) {

                        }
                    })
                    .build();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    protected void initView() {
        phoneLoginText = findViewById(R.id.activity_login_phoneLoginText);
        phoneLoginText.setTypeface(FontUtil.getRegularTypeFace());
        weiboLoginButton = findViewById(R.id.activity_login_sina);
        wechatLoginButton = findViewById(R.id.activity_login_wechat);
        facebookLoginButton = findViewById(R.id.activity_login_facebook);
        twitterLoginButton = findViewById(R.id.activity_login_twitter);

        Linkify.TransformFilter filter = new Linkify.TransformFilter() {
            public final String transformUrl(final Matcher match, String url) {
                return "";
            }
        };
        Pattern termsMatcher = Pattern.compile("隐私政策|服务条款|Privacy Policy|Terms and Conditions|隱私政策|服務條款");
        String termsURL = "http://sobrr.life";
        Linkify.addLinks((TextView) findViewById(R.id.activity_login_terms_conditions), termsMatcher, termsURL, null, filter);

        progressDialog = new CustomProgressDialog(this);
    }

    private void initEvent() {
        phoneLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginPhoneActivity.class));
            }
        });

        weiboLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thirdLogin(ShareSDKAuthHelper.PLATFORM_SinaWeibo);
            }
        });
        wechatLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thirdLogin(ShareSDKAuthHelper.PLATFORM_Wechat);
            }
        });
        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thirdLogin(ShareSDKAuthHelper.PLATFORM_Facebook);
            }
        });
        twitterLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thirdLogin(ShareSDKAuthHelper.PLATFORM_Twitter);
            }
        });
    }

    private void thirdLogin(String platformName) {
        ShareSDKAuthHelper authHelper = new ShareSDKAuthHelper();
        authHelper.setOnAuthListener(new ShareSDKAuthHelper.OnAuthListener() {
            @Override
            public void onCancel() {
                ToastUtil.showToast("取消授权");
                progressDialog.dismiss();
            }

            @Override
            public void onError() {
                ToastUtil.showToast("授权失败");
                progressDialog.dismiss();
            }

            @Override
            public void onComplete(String platform, HashMap<String, Object> res) {
                ToastUtil.showToast("授权成功");
//                loginFromThird(platName, hashMap);
            }
        });
        authHelper.auth(this, platformName);
    }
}
