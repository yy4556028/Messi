package com.yuyang.messi.ui.category;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.yuyang.lib_base.ui.header.HeaderLayout;
import com.yuyang.lib_base.utils.ToastUtil;
import com.yuyang.lib_xunfei.XunfeiRecognizerHelper;
import com.yuyang.lib_xunfei.XunfeiRecognizerUtil;
import com.yuyang.lib_xunfei.XunfeiSynthesizerHelper;
import com.yuyang.messi.R;
import com.yuyang.messi.ui.base.AppBaseActivity;
import com.yuyang.lib_base.utils.StorageUtil;

import java.io.File;

public class XunfeiActivity extends AppBaseActivity {

    private static final File SAVE_DIR = StorageUtil.getExternalFilesDir("讯飞");

    private Button recognizerDialogBtn;
    private Button synthesizerBtn;
    private Button synthesizerVoicer;
    private EditText editText0;

    private Button recognizerBtn;
    private AppCompatCheckBox punctuationCheckBox;
    private EditText editText1;

    private XunfeiRecognizerHelper recognizerHelperDialog;
    private XunfeiRecognizerHelper recognizerHelper;
    private XunfeiSynthesizerHelper synthesizerHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_xunfei;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
    }

    private void initView() {
        HeaderLayout headerLayout = findViewById(R.id.headerLayout);
        headerLayout.showLeftBackButton();
        headerLayout.showTitle("讯飞");

        recognizerDialogBtn = findViewById(R.id.activity_xunfei_recognizerDialogBtn);
        synthesizerBtn = findViewById(R.id.activity_xunfei_synthesizerBtn);
        synthesizerVoicer = findViewById(R.id.activity_xunfei_synthesizerVoicer);
        editText0 = findViewById(R.id.activity_xunfei_editText0);

        recognizerBtn = findViewById(R.id.activity_xunfei_recognizerBtn);
        punctuationCheckBox = findViewById(R.id.activity_xunfei_recognizerPunctuationCheckBox);
        editText1 = findViewById(R.id.activity_xunfei_editText1);

        recognizerHelperDialog = XunfeiRecognizerHelper
                .with(this)
                .setLanguageAccent(XunfeiRecognizerHelper.LANGUAGE_ACCENT.CN_MANDARIN)
                .setAudioDir(StorageUtil.getExternalFilesDir("讯飞") + "/iat.wav")
                .setOnXunfeiListener(new XunfeiRecognizerHelper.OnRecognizerListener() {
                    @Override
                    public void onResult(String result) {
                        editText0.setText(result);
                    }

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onEnd() {

                    }

                    @Override
                    public void onError(String errorMsg) {
                        ToastUtil.showToast(errorMsg);
                    }
                });

        recognizerHelper = XunfeiRecognizerHelper
                .with(this)
                .setLanguageAccent(XunfeiRecognizerHelper.LANGUAGE_ACCENT.CN_MANDARIN)
                .setAudioDir(SAVE_DIR + "/iat.wav")
                .setOnXunfeiListener(new XunfeiRecognizerHelper.OnRecognizerListener() {
                    @Override
                    public void onResult(String result) {
                        editText1.setText(result);
                    }

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onEnd() {

                    }

                    @Override
                    public void onError(String errorMsg) {
                        ToastUtil.showToast(errorMsg);
                    }
                });

        synthesizerHelper = XunfeiSynthesizerHelper
                .with(getActivity())
                .setAudioDir(SAVE_DIR + "/tts.wav")
                .setStreamType("3");

        punctuationCheckBox.setChecked(recognizerHelper.isShowPunctuation());
    }

    private void initEvent() {
        recognizerDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recognizerHelperDialog.startRecognizerDialog();
            }
        });
        synthesizerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synthesizerHelper.startSynthesizerAndSpeak(editText0.getText().toString());
            }
        });
        synthesizerVoicer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synthesizerHelper.selectVoicer();
            }
        });
        recognizerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recognizerHelper.startRecognizer();
            }
        });
        punctuationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                recognizerHelper.setShowPunctuation(isChecked);
            }
        });
        XunfeiRecognizerUtil.bind(findViewById(R.id.activity_xunfei_editText2), findViewById(R.id.activity_xunfei_image2));
    }

    /**
     * 根据字体size获取字体高度
     */
    public static int getFontHeightBySize() {
        Paint paint = new Paint();
        paint.setTextSize(18);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) (Math.ceil(fm.descent - fm.ascent));
    }

}
