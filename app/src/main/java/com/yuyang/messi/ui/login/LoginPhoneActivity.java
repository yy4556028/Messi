package com.yuyang.messi.ui.login;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.yamap.lib_chat.keyboard.utils.KeyboardUtil;
import com.yuyang.lib_base.config.Config;
import com.yuyang.lib_base.utils.CommonUtil;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.lib_base.utils.statusbar.MyStatusBarUtil;
import com.yuyang.messi.R;
import com.yuyang.messi.bean.UserBean;
import com.yuyang.messi.receiver.SMSReceiver;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.messi.utils.AccountUtil;
import com.yuyang.messi.utils.CaptchaUtil;
import com.yuyang.messi.utils.SharedPreferencesUtil;
import com.yuyang.messi.view.Picker.countrypicker.Country;
import com.yuyang.messi.view.Picker.countrypicker.CountryPickerPop;
import com.yuyang.messi.view.Picker.countrypicker.CountryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LoginPhoneActivity extends AppBaseActivity {

    private TextView countryCodeText;
    private EditText countryCodeEdit;

    private TextInputLayout phoneEditWrapper;
    private EditText phoneEdit;

    private TextInputLayout pwdEditWrapper;
    private EditText pwdEdit;
//    private AppCompatCheckBox checkBox;

    private EditText localCodeEdit;
    private ImageView localCodeImage;

    private EditText netCodeEdit;
    private TextView netCodeText;

    private TextView loginBtn;

    private CountryPickerPop countryPickerPop;
    private List<Country> countryList;

    private SMSReceiver smsReceiver;

    private CountDownTimer countDownTimer;

    private final ActivityResultLauncher<String[]> permissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                List<String> deniedAskList = new ArrayList<>();
                List<String> deniedNoAskList = new ArrayList<>();
                for (Map.Entry<String, Boolean> stringBooleanEntry : result.entrySet()) {
                    if (!stringBooleanEntry.getValue()) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), stringBooleanEntry.getKey())) {
                            deniedAskList.add(stringBooleanEntry.getKey());
                        } else {
                            deniedNoAskList.add(stringBooleanEntry.getKey());
                        }
                    }
                }

                if (deniedAskList.size() == 0 && deniedNoAskList.size() == 0) {//全通过
                    phoneEdit.setText(CommonUtil.getPhoneNumber());
                } else if (deniedNoAskList.size() > 0) {
                    //do nothing
                } else {
                    //do nothing
                }
            });

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_phone;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        countryList = CountryUtil.getAllCountries(getActivity());
        initView();
        initEvent();

        // 注册收短信广播
        if (smsReceiver == null) {
            smsReceiver = new SMSReceiver(getActivity(), netCodeEdit, "发送号码 sender");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            permissionsLauncher.launch(new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.READ_SMS,
                    Manifest.permission.READ_PHONE_NUMBERS});
        }

        try {
            AccountUtil.createMyAccount();
        } catch (Exception e) {
            e.printStackTrace();
//            ToastUtil.showToast("未登录");
        }

        initCountDownTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (smsReceiver != null) {  // 注销收短信广播
            unregisterReceiver(smsReceiver);
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void initView() {
        countryCodeText = findViewById(R.id.activity_login_phone_country_code_text);
        ((LinearLayout.LayoutParams) countryCodeText.getLayoutParams()).topMargin = MyStatusBarUtil.getStatusBarHeight();
        countryCodeEdit = findViewById(R.id.activity_login_country_code_edit);

        phoneEditWrapper = findViewById(R.id.activity_login_phoneEditWrapper);
        phoneEditWrapper.setHintAnimationEnabled(true);
        phoneEditWrapper.setHintTextAppearance(R.style.TextInputLayoutTextAppearance);
        phoneEdit = findViewById(R.id.activity_login_phoneEdit);
        phoneEdit.setSelection(phoneEdit.getText().length());

        pwdEditWrapper = findViewById(R.id.activity_login_pwdEditWrapper);
        pwdEditWrapper.setHintAnimationEnabled(true);
        pwdEditWrapper.setHintTextAppearance(R.style.TextInputLayoutTextAppearance);
        pwdEdit = findViewById(R.id.activity_login_pwdEdit);
//        checkBox = findViewById(R.id.activity_login_edit_password_check);

        netCodeEdit = findViewById(R.id.activity_login_netCodeEdit);
        netCodeText = findViewById(R.id.activity_login_netCodeText);

        localCodeEdit = findViewById(R.id.activity_login_localCodeEdit);
        localCodeImage = findViewById(R.id.activity_login_localCodeImage);
        localCodeImage.setImageBitmap(CaptchaUtil.createBitmap());

        loginBtn = findViewById(R.id.activity_login_login);

        String currentCountryCode = CountryUtil.getCurrentCountryCode();
        String numCode = CountryUtil.getCountryCode(currentCountryCode);
        countryCodeEdit.setText(numCode);
        String country = Locale.getDefault().getDisplayCountry();
        setupCountryCodeText(country, numCode);
    }

    private void initEvent() {
        countryCodeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countryPickerPop == null) {
                    countryPickerPop = new CountryPickerPop(getActivity());
                    countryPickerPop.setCountryPickerListener(new CountryPickerPop.CountryPickerListener() {
                        @Override
                        public void onSelectCountry(String name, String code) {
                            String numCode = CountryUtil.getCountryCode(code);
                            countryCodeEdit.setText(numCode);
                            setupCountryCodeText(name, numCode);
                        }
                    });
                }

                countryPickerPop.showAsDropDown(countryCodeText, 0, 0);
                KeyboardUtil.showHideIme(getActivity(), false);
            }
        });

        countryCodeEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().startsWith("+")) {
                    countryCodeEdit.setText("+" + s);
                }

                if (countryCodeText.getSelectionStart() == 0) {
                    countryCodeEdit.setSelection(1);
                }

                if (phoneEdit.getText() != null && !phoneEdit.getText().toString().equals("")) {
                    phoneEdit.setText(null);
                    phoneEdit.setSelection(0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (countryCodeEdit.getText() != null) {
                    String isoCode = CountryUtil.getISOCode(countryCodeEdit.getText().toString());
                    if (!TextUtils.isEmpty(isoCode)) {
                        for (Country country : countryList) {
                            if (isoCode.equals(country.getCode())) {
                                setupCountryCodeText(country.getName(), countryCodeEdit.getText().toString());
                                break;
                            }
                        }
                    }
                }
            }
        });

        phoneEdit.addTextChangedListener(new TextWatcher() {

            /**
             * 括号内为 (输入单个字符，删除单个字符) 的数值
             * @param s         改变之前的文本
             * @param start     从 start 处开始改变   (0, 0)
             * @param count     被替换的字符的长度   (0, 1)
             * @param after     新的文本的长度     (1, 0)
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            /**
             * 括号内为 (输入单个字符，删除单个字符) 的数值
             * @param s         改变之后的文本
             * @param start     从 start 处开始改变   (0, 0)
             * @param before    被替换的旧文本长度   (0. 1)
             * @param count     替换的字符的长度     (1. 0)
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateGetCodeBtn();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

//        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                int start = pwdEdit.getSelectionStart();
//                int end = pwdEdit.getSelectionEnd();
//                if(isChecked){
//                    //如果选中，显示密码
//                    pwdEdit.setTransformationMethod(HideReturnsTransformationMethod.getAppContext());
//                }else{
//                    //否则隐藏密码
//                    pwdEdit.setTransformationMethod(PasswordTransformationMethod.getAppContext());
//                }
//                pwdEdit.setSelection(start, end);
//            }
//        });

        netCodeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNum = phoneEdit.getText().toString().trim();

                if (TextUtils.isEmpty(phoneNum)) {
                    phoneEditWrapper.setError("手机号码不能为空！");
                } else if (phoneNum.length() < 11) {
                    phoneEditWrapper.setError("请输入11位手机号码！");
                } else {
                    phoneEditWrapper.setErrorEnabled(false);

                    SharedPreferencesUtil.setCodeTimeInMills(LoginPhoneActivity.class.getSimpleName(),
                            System.currentTimeMillis() + Config.CODE_WAIT_TIME);
                    initCountDownTimer();

//                    Retrofit retrofit = new Retrofit.Builder()
//                            .baseUrl(MuzhikeService.BASE_URL)
////                            .addConverterFactory(GsonConverterFactory.create())
////                            .addConverterFactory(FastJsonConverterFactory.create())
//                            .client(OkHttpUtil.getInstance().getOkHttpClient())
//                            .build();
//                    MuzhikeService githubService = retrofit.create(MuzhikeService.class);
//                    Call<String> repos = githubService.getCode(phoneNum);
//                    repos.enqueue(new Callback<String>() {
//                        @Override
//                        public void onResponse(Call<String> call, Response<String> response) {
//                            String string = response.body();
//                        }
//
//                        @Override
//                        public void onFailure(Call<String> call, Throwable t) {
//                            t.printStackTrace();
//                        }
//                    });
                }
            }
        });

        localCodeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localCodeImage.setImageBitmap(CaptchaUtil.createBitmap());
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneNum = phoneEdit.getText().toString().replaceAll("\\D+", "");
                if (countryCodeEdit.getText().toString().equals("+1") && phoneNum.length() != 10) {
                    ToastUtil.showToast("Your phone number should have 10 digits.");
                    phoneEdit.requestFocus();
                    return;
                }

                if (countryCodeEdit.getText().toString().equals("+86") && phoneNum.length() != 11) {
                    phoneEditWrapper.setError("请输入正确的手机号！");
                    phoneEdit.setError("请输入正确的手机号！");
                    phoneEdit.requestFocus();
                    return;
                }
                phoneEditWrapper.setErrorEnabled(false);

                final String password = pwdEdit.getText().toString();
                if (password.length() < 3) {
                    Snackbar.make(loginBtn, "Password needs to be at least 3 characters.", Snackbar.LENGTH_LONG)
                            .setAction("知道了", null)
                            .show();
                    ToastUtil.showToast("Password needs to be at least 3 characters.");
                    pwdEdit.requestFocus();
                    return;
                }

                if (!localCodeEdit.getText().toString().equalsIgnoreCase(CaptchaUtil.getCaptcha())) {
                    localCodeEdit.setText(null);
                    localCodeEdit.requestFocus();
                    ToastUtil.showToast("图片验证码错误");
                    return;
                }

                loginBtn.setEnabled(false);
                KeyboardUtil.showHideIme(getActivity(), false);

                phoneNum = String.format("%s%s", countryCodeEdit.getText(), phoneNum);
                UserBean userBean = new UserBean();
                userBean.setPhone_number(phoneNum);
                userBean.setUsername("yy4556028");
                userBean.setAlias("呵呵哒");
                userBean.setAvatar_url(null);
                userBean.setBackground_url(null);
                userBean.setToken("");
                userBean.setGender((short) 1);
                userBean.setRemote_id("0");
                userBean.setIs_official(true);

                userBean.setAccountType(AccountUtil.ACCOUNT_TYPE_PHONE);
                AccountUtil.addAccount(userBean);
                try {
                    AccountUtil.createMyAccount();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                finish();
            }
        });
    }

    private void initCountDownTimer() {
        long timeInMills = SharedPreferencesUtil.getCodeTimeInMills(LoginPhoneActivity.class.getSimpleName());
        countDownTimer = new CountDownTimer(timeInMills - System.currentTimeMillis(), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                netCodeText.setText(millisUntilFinished / 1000 + "秒");
                updateGetCodeBtn();
            }

            @Override
            public void onFinish() {
                netCodeText.setText("获取验证码");
                countDownTimer = null;
                updateGetCodeBtn();
            }
        };
        countDownTimer.start();
    }

    private void updateGetCodeBtn() {
        if (phoneEdit.getText().length() == 11 && countDownTimer == null) {
            netCodeText.setEnabled(true);
        } else {
            netCodeText.setEnabled(false);
        }
    }

    private void setupCountryCodeText(String countryName, String numCode) {
        if (numCode.equals("+")) {
            countryCodeText.setText(countryName);
        } else {
            countryCodeText.setText(countryName + " (" + numCode + ")");
        }
    }
}
