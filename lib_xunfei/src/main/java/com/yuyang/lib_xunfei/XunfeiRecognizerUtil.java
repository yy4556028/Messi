package com.yuyang.lib_xunfei;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.yuyang.lib_xunfei.bean.ResultBean;

public class XunfeiRecognizerUtil {

    private static final String TAG = XunfeiRecognizerUtil.class.getSimpleName();

    // 语音听写对象
    private static SpeechRecognizer recognizer;

    private static final StringBuilder sentenceBuilder = new StringBuilder();

    public static void bind(final TextView textView, View touchView) {
        touchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recognizer == null) {
                    recognizer = SpeechRecognizer.createRecognizer(textView.getContext().getApplicationContext(), null);
//                    // 清空参数
//                    recognizer.setParameter(SpeechConstant.PARAMS, null);
//                    // 设置听写引擎
//                    recognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
//                    // 设置返回结果格式
//                    recognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");
//                    recognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
//                    recognizer.setParameter(SpeechConstant.ACCENT, "mandarin");//普通话
//                    // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
//                    recognizer.setParameter(SpeechConstant.VAD_BOS, "2000");
//                    // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
//                    recognizer.setParameter(SpeechConstant.VAD_EOS, "1000");
//                    // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
                    recognizer.setParameter(SpeechConstant.ASR_PTT, "0");
//                    // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//                    // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//                    recognizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//            recognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH, audioDir);
                }
                sentenceBuilder.delete(0, sentenceBuilder.length());//清空内容

                final XunfeiDialog dialog = new XunfeiDialog(textView.getContext());

                int ret = recognizer.startListening(new RecognizerListener() {
                    @Override
                    public void onVolumeChanged(int i, byte[] bytes) {
                        dialog.updateAmplitude((float) Math.sqrt(i/10));
                    }

                    @Override
                    public void onBeginOfSpeech() {

                    }

                    @Override
                    public void onEndOfSpeech() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onResult(RecognizerResult recognizerResult, boolean b) {
                        try {
                            ResultBean resultBean = JSON.parseObject(recognizerResult.getResultString(), ResultBean.class);
                            sentenceBuilder.append(resultBean.toString());
                            textView.setText(sentenceBuilder.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(SpeechError speechError) {

                    }

                    @Override
                    public void onEvent(int i, int i1, int i2, Bundle bundle) {

                    }
                });
                if (ret != ErrorCode.SUCCESS) {
                    Log.e(TAG, "听写失败,错误码：" + ret);
                } else {
                    dialog.show();
                }
            }
        });
    }
}
