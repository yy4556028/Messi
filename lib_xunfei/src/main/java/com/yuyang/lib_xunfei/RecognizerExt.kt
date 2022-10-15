package com.yuyang.lib_xunfei

import android.os.Bundle
import androidx.annotation.IntDef
import com.alibaba.fastjson.JSON
import com.iflytek.cloud.*
import com.iflytek.cloud.ui.RecognizerDialog
import com.iflytek.cloud.ui.RecognizerDialogListener
import com.yuyang.lib_base.utils.LogUtil
import com.yuyang.lib_base.utils.ToastUtil
import com.yuyang.lib_xunfei.bean.ResultBean

@Retention(AnnotationRetention.SOURCE)
@IntDef(RegLanguage.ENGLISH, RegLanguage.CHINESE, RegLanguage.CHINESE_GUANHDONG)
annotation class RegLanguage {
    companion object {
        const val ENGLISH = 1
        const val CHINESE = 2
        const val CHINESE_GUANHDONG = 3
    }
}

fun SpeechRecognizer.setLanguage(@RegLanguage language: Int) {
    when (language) {
        RegLanguage.ENGLISH -> {
            setParameter(SpeechConstant.LANGUAGE, "en_us")
        }
        RegLanguage.CHINESE -> {
            setParameter(SpeechConstant.LANGUAGE, "zh_cn")
            setParameter(SpeechConstant.ACCENT, "mandarin") //普通话
        }
        RegLanguage.CHINESE_GUANHDONG -> {
            setParameter(SpeechConstant.LANGUAGE, "zh_cn")
            setParameter(SpeechConstant.ACCENT, "cantonese") //粤语
        }
    }
}

fun SpeechRecognizer.showPunctuation(showPunctuation: Boolean) {
    setParameter(SpeechConstant.ASR_PTT, if (showPunctuation) "1" else "0")
}

fun SpeechRecognizer.startRecognizer(simpleListener: SimpleListener?) {
    val recognizerListener = SimpleRecognizerListener(simpleListener)
    val ret: Int = startListening(recognizerListener)
    if (ret != ErrorCode.SUCCESS) {
        simpleListener?.onError("听写失败,错误码：$ret")
    } else {
        simpleListener?.onStart()
    }
}

fun RecognizerDialog.setListener(simpleListener: SimpleListener?) {
    val recognizerListener = SimpleRecognizerListener(simpleListener)
    setListener(recognizerListener)
}

open class SimpleListener {
    open fun onResult(result: String?) {}
    fun onStart() {}
    fun onEnd() {}
    fun onError(errorMsg: String?) {
        ToastUtil.showToast(errorMsg)
    }
}

open class SimpleRecognizerListener constructor(val listener: SimpleListener?) :
    RecognizerListener, RecognizerDialogListener {

    companion object {
        const val TAG = "SimpleRecognizerListener"
    }

    override fun onVolumeChanged(volume: Int, data: ByteArray) {
        //"当前正在说话，音量大小：" + volume
        LogUtil.d(TAG, "返回音频数据：" + data.size)

    }

    override fun onBeginOfSpeech() {
        // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
        listener?.onStart()
    }

    override fun onEndOfSpeech() {
        // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
        listener?.onEnd()
    }

    override fun onResult(results: RecognizerResult?, isLast: Boolean) {
        results?.apply {
            LogUtil.d(TAG, results.resultString)
            try {
                val resultBean: ResultBean = JSON.parseObject(
                    results.resultString,
                    ResultBean::class.java
                )
                listener?.onResult(resultBean.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                listener?.onError("Json Parse Exception")
            }
        }
        if (isLast) {
            // TODO 最后的结果
        }
    }

    override fun onError(error: SpeechError) {
        // Tips：
        // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
        // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
        listener?.onError(error.getPlainDescription(true))
    }

    override fun onEvent(eventType: Int, arg1: Int, arg2: Int, obj: Bundle?) {
        // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
        // 若使用本地能力，会话id为null
//        if (SpeechEvent.EVENT_SESSION_ID == eventType) {
//            val sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
//            LogUtil.d(TAG, "session id =$sid");
//        }
    }
}

