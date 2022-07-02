package com.yuyang.messi.ui.suning;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.net.okhttp.OkHttpUtil;
import com.yuyang.messi.net.okhttp.callback.StringCallback;
import com.yuyang.messi.ui.base.AppBaseActivity;

import okhttp3.Call;

public class DouyaLoginActivity extends AppBaseActivity {

    private EditText phoneEdit;
    private EditText pwdEdit;
    private TextView loginBtn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_douya_login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
    }

    private void initView() {
        phoneEdit = findViewById(R.id.activity_douya_login_phoneEdit);
        pwdEdit = findViewById(R.id.activity_douya_login_pwdEdit);
        loginBtn = findViewById(R.id.activity_douya_login_login);
    }

    private void initEvent() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username = phoneEdit.getText().toString();//18130015@sn
                final String password = pwdEdit.getText().toString();//18130015

//                String service = "http://supsit.cnsuning.com/sup-web/auth?targetUrl=http://supsit.cnsuning.com/sup-web/index.html?templateCode=20190529170410384001#/suningStoreHome";
                String service = "http://supsit.cnsuning.com/sup-web/auth?targetUrl=http://supsit.cnsuning.com/sup-web/index.html?templateCode=20190529170410384001%23/suningStoreHome";

                OkHttpUtil.post()
//                    .url("https://impassport.suning.com/ids/login?jsonViewType=true")
                    .url("https://impassportsit.cnsuning.com/ids/login")
                    .addParams("viewType", "json")
                    .addParams("username", username)
                    .addParams("password", password)
                    .addParams("uuid", "")
                    .addParams("verifyCode", "")
                    .addParams("smsCode", "")
                    .addParams("service", service)
                    .addParams("deviceNo", "3dc1ff91-9d1c-8bcc-773d-072a2cedba71")
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            try {
                                DouyaLoginBean douyaLoginBean = new Gson().fromJson(response, DouyaLoginBean.class);
                                if (douyaLoginBean.getRes_code() == 0) {
                                    String url = douyaLoginBean.getService() + "&ticket=" + douyaLoginBean.getSt();
//                                    SharedPreferencesUtil.setString(DouyaLoginActivity.class.getSimpleName(), url);
                                    DouyaWebActivity.launchActivity(getActivity(), url);
                                } else if (douyaLoginBean.getRes_code() == -1) {
                                    ToastUtil.showToast("未提交登录凭据");
                                } else {
                                    ToastUtil.showToast(douyaLoginBean.getErrorMessage());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                ToastUtil.showToast(e.getMessage());
                            }
                        }
                    });
            }
        });
    }
}
