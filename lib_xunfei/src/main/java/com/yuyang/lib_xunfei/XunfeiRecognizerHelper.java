package com.yuyang.lib_xunfei;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.yuyang.lib_xunfei.bean.ResultBean;

/**
 * 语音识别
 */
public class XunfeiRecognizerHelper {

    private static final String TAG = XunfeiRecognizerHelper.class.getSimpleName();

    public enum LANGUAGE_ACCENT {
        EN,//英语
        CN_MANDARIN,//汉语普通话
        CN_CANTONESE//粤语
    }

    private Activity activity;
    private LANGUAGE_ACCENT languageAccent = LANGUAGE_ACCENT.CN_MANDARIN;
    private boolean showPunctuation = true;//显示标点
    private String audioDir = Environment.getExternalStorageDirectory() + "/msc/iat.wav";

    // 语音听写对象
    private SpeechRecognizer recognizer;
    // 语音听写UI
    private RecognizerDialog recognizerDialog;

    // 听写结果
    private StringBuilder sentenceBuilder = new StringBuilder();

    public interface OnRecognizerListener {
        void onResult(String result);

        void onStart();

        void onEnd();

        void onError(String errorMsg);
    }

    private OnRecognizerListener listener;

    private XunfeiRecognizerHelper(Activity activity) {
        this.activity = activity;
    }

    public static XunfeiRecognizerHelper with(Activity activity) {
        return new XunfeiRecognizerHelper(activity);
    }

    public XunfeiRecognizerHelper setLanguageAccent(LANGUAGE_ACCENT la) {
        languageAccent = la;
        return this;
    }

    public boolean isShowPunctuation() {
        return showPunctuation;
    }

    public void setShowPunctuation(boolean showPunctuation) {
        this.showPunctuation = showPunctuation;
    }

    public XunfeiRecognizerHelper setAudioDir(String audioDir) {
        this.audioDir = audioDir;
        return this;
    }

    public XunfeiRecognizerHelper setOnXunfeiListener(OnRecognizerListener listener) {
        this.listener = listener;
        return this;
    }

    public void startRecognizer() {
        if (recognizer == null) {
            recognizer = SpeechRecognizer.createRecognizer(activity, null);
        }

        sentenceBuilder.delete(0, sentenceBuilder.length());//清空内容
        setParams();// 设置参数
        int ret = recognizer.startListening(recognizerListener);
        if (listener == null) return;
        if (ret != ErrorCode.SUCCESS) {
            listener.onError("听写失败,错误码：" + ret);
        } else {
            listener.onStart();
        }
    }

    public void startRecognizerDialog() {
        if (recognizerDialog == null) {
            recognizerDialog = new RecognizerDialog(activity, null);
        }
        sentenceBuilder.delete(0, sentenceBuilder.length());//清空内容
        recognizerDialog.setListener(dialogListener);
        recognizerDialog.show();
    }

    private void setParams() {
        // 清空参数
        recognizer.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎
        recognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        recognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");

        switch (languageAccent) {
            case EN: {
                recognizer.setParameter(SpeechConstant.LANGUAGE, "en_us");
                break;
            }
            case CN_MANDARIN: {
                recognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                recognizer.setParameter(SpeechConstant.ACCENT, "mandarin");//普通话
                break;
            }
            case CN_CANTONESE: {
                recognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                recognizer.setParameter(SpeechConstant.ACCENT, "cantonese");//粤语
                break;
            }
        }
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        recognizer.setParameter(SpeechConstant.VAD_BOS, "4000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        recognizer.setParameter(SpeechConstant.VAD_EOS, "2000");
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        recognizer.setParameter(SpeechConstant.ASR_PTT, showPunctuation ? "1" : "0");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        recognizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        recognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH, audioDir);
    }

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener dialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            parseResult(results);
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            if (listener != null) {
                listener.onError(error.getPlainDescription(true));
            }
        }

    };

    /**
     * 听写监听器。
     */
    private RecognizerListener recognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            if (listener != null) {
                listener.onStart();
            }
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            if (listener != null) {
                listener.onError(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            if (listener != null) {
                listener.onEnd();
            }
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            parseResult(results);

            if (isLast) {
                // TODO 最后的结果
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            //"当前正在说话，音量大小：" + volume
            Log.d(TAG, "返回音频数据：" + data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    private void parseResult(RecognizerResult results) {
        if (listener != null) {
            try {

                ResultBean resultBean = JSON.parseObject(results.getResultString(), ResultBean.class);
                sentenceBuilder.append(resultBean.toString());
                listener.onResult(sentenceBuilder.toString());
            } catch (Exception e) {
                e.printStackTrace();
                listener.onError("Json Parse Exception");
            }
        }
    }
}
