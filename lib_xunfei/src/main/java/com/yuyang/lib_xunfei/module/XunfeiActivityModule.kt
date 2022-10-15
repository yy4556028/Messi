package com.yuyang.lib_xunfei.module

import android.content.Context
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.ui.RecognizerDialog
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
class XunfeiActivityModule {

    @Provides
    fun provideXunfeiSpeechRecognizerDialog(@ActivityContext context: Context): RecognizerDialog {
        // 语音听写UI
        val recognizerDialog = RecognizerDialog(context, null)
        // 清空参数
        recognizerDialog.setParameter(SpeechConstant.PARAMS, null)
        // 设置听写引擎
        recognizerDialog.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD)
        // 设置返回结果格式
        recognizerDialog.setParameter(SpeechConstant.RESULT_TYPE, "json")
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        recognizerDialog.setParameter(SpeechConstant.VAD_BOS, "4000")
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        recognizerDialog.setParameter(SpeechConstant.VAD_EOS, "2000")
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        recognizerDialog.setParameter(SpeechConstant.ASR_PTT, "1")
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        // recognizerDialog.setParameter(SpeechConstant.AUDIO_FORMAT, "wav")
        // recognizerDialog.setParameter(SpeechConstant.ASR_AUDIO_PATH, audioDir);
        return recognizerDialog
    }

}