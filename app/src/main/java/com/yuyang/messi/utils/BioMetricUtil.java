package com.yuyang.messi.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.yuyang.lib_base.BaseApp;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.messi.MessiApp;
import com.yuyang.messi.bean.AddrPicker.ProvinceBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * <a href="https://source.android.google.cn/docs/security/features/biometric?hl=sl">...</a>
 */
public class BioMetricUtil {

    /**
     * 检查设备是否支持生物识别
     */
    public static boolean checkBiometric(Activity activity) {

        BiometricManager biometricManager = BiometricManager.from(BaseApp.getInstance());

        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                // 设备支持生物识别，准备好进行身份验证
                return true;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                // 设备没有生物识别硬件
                ToastUtil.showToast("No biometric hardware");
                return false;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                // 生物识别硬件不可用
                ToastUtil.showToast("Biometric hardware unavailable");
                return false;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // 用户未注册您的应用所支持的生物特征认证，请提示用户进行注册
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                    enrollIntent.putExtra(
                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    );
                    activity.startActivityForResult(enrollIntent, 0);
                }
                return false;
            default:
                return false;
        }
    }

    public static void authenticate(FragmentActivity activity) {
        BiometricPrompt biometricPrompt = new BiometricPrompt(
                activity,
                ContextCompat.getMainExecutor(BaseApp.getInstance()),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        ToastUtil.showToast("Authentication error: $errString");
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        ToastUtil.showToast("Authentication succeeded!");
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        ToastUtil.showToast("Authentication failed");
                    }
                });


        // 在需要的时候显示指纹识别对话框
        biometricPrompt.authenticate(new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Title")
                .setSubtitle("Biometric Subtitle")
                .setDescription("Description")
                .setNegativeButtonText("Use account password")
                .build());
    }

}
