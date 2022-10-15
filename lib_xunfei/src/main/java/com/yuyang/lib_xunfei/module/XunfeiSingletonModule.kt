package com.yuyang.lib_xunfei.module

import android.content.Context
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechRecognizer
import com.iflytek.cloud.SpeechSynthesizer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class XunfeiSingletonModule {

    @Singleton
    @Provides
    fun provideXunfeiSpeechRecognizer(@ApplicationContext context: Context): SpeechRecognizer {
        val recognizer = SpeechRecognizer.createRecognizer(context, null)

        // 清空参数
        recognizer.setParameter(SpeechConstant.PARAMS, null)
        // 设置语法ID和 SUBJECT 为空，以免因之前有语法调用而设置了此参数；或直接清空所有参数，具体可参考 DEMO 的示例
        recognizer.setParameter(SpeechConstant.CLOUD_GRAMMAR, null)
        recognizer.setParameter(SpeechConstant.SUBJECT, null)
        // 设置返回结果格式，目前支持json,xml以及plain 三种格式，其中plain为纯听写文本内容
        recognizer.setParameter(SpeechConstant.RESULT_TYPE, "json")
        // 设置听写引擎 此处engineType为“cloud”
        recognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD)

        recognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn") // 汉语
        recognizer.setParameter(SpeechConstant.ACCENT, "mandarin") //普通话

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理 取值范围{1000～10000}
        recognizer.setParameter(SpeechConstant.VAD_BOS, "4000")
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入，自动停止录音，范围{0~10000}
        recognizer.setParameter(SpeechConstant.VAD_EOS, "4000")
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        recognizer.setParameter(SpeechConstant.ASR_PTT, "1")
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        // recognizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        // recognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
        return recognizer
    }

    @Singleton
    @Provides
    fun provideXunfeiSynthesizer(@ApplicationContext context: Context): SpeechSynthesizer {
        val synthesizer = SpeechSynthesizer.createSynthesizer(context, null)

        //清空参数
        synthesizer.setParameter(SpeechConstant.PARAMS, null)
        synthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD) //在线合成
        //设置在线合成发音人
        synthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan")//默认发音人
        //设置合成语速
        synthesizer.setParameter(SpeechConstant.SPEED, "50")
        //设置合成音调
        synthesizer.setParameter(SpeechConstant.PITCH, "50")
        //设置合成音量
        synthesizer.setParameter(SpeechConstant.VOLUME, "50")
        //设置播放器音频流类型 0通话  1系统  2铃声  3音乐  4闹铃  5通知
        synthesizer.setParameter(SpeechConstant.STREAM_TYPE, "3")
        // 设置播放合成音频打断音乐播放，默认为true
        synthesizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true")
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        // synthesizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav")
        // synthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav")

        return synthesizer
    }
}