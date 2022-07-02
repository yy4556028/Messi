package com.yuyang.lib_xunfei;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AlertDialog;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

/**
 * 语音合成
 */
public class XunfeiSynthesizerHelper {

    private static final String TAG = XunfeiSynthesizerHelper.class.getSimpleName();

    private Activity activity;

    private String audioDir = Environment.getExternalStorageDirectory() + "/msc/tts.wav";

    private String streamType = "3";

    // 默认发音人
    private String voicer = "xiaoyan";
    private int selectedVoicerIndex = 0;
    private String[] mCloudVoicersEntries;
    private String[] mCloudVoicersValue;

    // 语音合成对象
    private SpeechSynthesizer synthesizer;

    private XunfeiSynthesizerHelper(Activity activity) {
        this.activity = activity;
        // 云端发音人名称列表
        mCloudVoicersEntries = activity.getResources().getStringArray(R.array.voicer_cloud_entries);
        mCloudVoicersValue = activity.getResources().getStringArray(R.array.voicer_cloud_values);
    }

    public static XunfeiSynthesizerHelper with(Activity activity) {
        return new XunfeiSynthesizerHelper(activity);
    }

    public XunfeiSynthesizerHelper setAudioDir(String audioDir) {
        this.audioDir = audioDir;
        return this;
    }

    //设置播放器音频流类型 0通话  1系统  2铃声  3音乐  4闹铃  5通知
    public XunfeiSynthesizerHelper setStreamType(String streamType) {
        this.streamType = streamType;
        return this;
    }

    public void selectVoicer() {
        new AlertDialog.Builder(activity).setTitle("在线合成发音人选项")
                .setSingleChoiceItems(mCloudVoicersEntries, // 单选框有几项,各是什么名字
                        selectedVoicerIndex, // 默认的选项
                        new DialogInterface.OnClickListener() { // 点击单选框后的处理
                            public void onClick(DialogInterface dialog,
                                                int which) { // 点击了哪一项
                                voicer = mCloudVoicersValue[which];
                                if ("catherine".equals(voicer) || "henry".equals(voicer) || "vimary".equals(voicer)) {
                                    //英文
                                } else {
                                    //汉语
                                }
                                selectedVoicerIndex = which;
                                dialog.dismiss();
                            }
                        }).show();
    }

    public void startSynthesizerAndSpeak(String text) {
        if (synthesizer == null) {
            synthesizer = SpeechSynthesizer.createSynthesizer(activity, null);
        }

        setParams();// 设置参数
        int code = synthesizer.startSpeaking(text, mTtsListener);

        if (code != ErrorCode.SUCCESS) {
            Toast.makeText(activity, "语音合成失败,错误码: " + code, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 只保存音频不进行播放接口
     *
     * @param text 要合成的文本
     * @param path 需要保存的音频全路径
     */
    public void startSynthesizerAndSave(String text, String path) {
        if (synthesizer == null) {
            synthesizer = SpeechSynthesizer.createSynthesizer(activity, null);
        }

        setParams();// 设置参数
        int code = synthesizer.synthesizeToUri(text, path, mTtsListener);

        if (code != ErrorCode.SUCCESS) {
            Toast.makeText(activity, "语音合成失败,错误码: " + code, Toast.LENGTH_SHORT).show();
        }
    }

    private void setParams() {
        //清空参数
        synthesizer.setParameter(SpeechConstant.PARAMS, null);
        synthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);//在线合成
        //设置在线合成发音人
        synthesizer.setParameter(SpeechConstant.VOICE_NAME, voicer);
        //设置合成语速
        synthesizer.setParameter(SpeechConstant.SPEED, "50");
        //设置合成音调
        synthesizer.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        synthesizer.setParameter(SpeechConstant.VOLUME, "50");
        //设置播放器音频流类型 0通话  1系统  2铃声  3音乐  4闹铃  5通知
        synthesizer.setParameter(SpeechConstant.STREAM_TYPE, streamType);
        // 设置播放合成音频打断音乐播放，默认为true
        synthesizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        synthesizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        synthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, audioDir);
    }

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            //开始播放
        }

        @Override
        public void onSpeakPaused() {
            //暂停播放
        }

        @Override
        public void onSpeakResumed() {
            //继续播放
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            // 合成进度 percent;
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度 percent;
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                //播放完成
            } else {
//                showTip(error.getPlainDescription(true));
            }
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
}
