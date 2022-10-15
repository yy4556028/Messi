package com.yuyang.lib_xunfei

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechError
import com.iflytek.cloud.SpeechSynthesizer
import com.iflytek.cloud.SynthesizerListener

fun SpeechSynthesizer.startSynthesizerAndSpeak(text: String): Int {
    val synthesizerListener = object : SynthesizerListener {
        override fun onSpeakBegin() {
            //开始播放
        }

        override fun onBufferProgress(percent: Int, beginPos: Int, endPos: Int, info: String?) {
            // 合成进度 percent
        }

        override fun onSpeakPaused() {
            //暂停播放
        }

        override fun onSpeakResumed() {
            //继续播放
        }

        override fun onSpeakProgress(percent: Int, beginPos: Int, endPos: Int) {
            // 播放进度 percent
        }

        override fun onCompleted(error: SpeechError?) {
            //ToastUtil.showToast(error?.getPlainDescription(true))
        }

        override fun onEvent(eventType: Int, arg1: Int, arg2: Int, obj: Bundle?) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //	val sid = obj?.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //	LogUtil.d(TAG, "session id =" + sid);
            //}
        }
    }
    val code = startSpeaking(text, synthesizerListener)
//    if (code != ErrorCode.SUCCESS) {
//        ToastUtil.showToast("语音合成失败,错误码: $code")
//    }
    return code
}

fun SpeechSynthesizer.startSynthesizerAndSave(text: String, path: String): Int {
    val synthesizerListener = object : SynthesizerListener {
        override fun onSpeakBegin() {
            //开始播放
        }

        override fun onBufferProgress(percent: Int, beginPos: Int, endPos: Int, info: String?) {
            // 合成进度 percent
        }

        override fun onSpeakPaused() {
            //暂停播放
        }

        override fun onSpeakResumed() {
            //继续播放
        }

        override fun onSpeakProgress(percent: Int, beginPos: Int, endPos: Int) {
            // 播放进度 percent
        }

        override fun onCompleted(error: SpeechError?) {
            //ToastUtil.showToast(error?.getPlainDescription(true))
        }

        override fun onEvent(eventType: Int, arg1: Int, arg2: Int, obj: Bundle?) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //	val sid = obj?.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //	LogUtil.d(TAG, "session id =" + sid);
            //}
        }
    }
    val code = synthesizeToUri(text, path, synthesizerListener)
//    if (code != ErrorCode.SUCCESS) {
//        ToastUtil.showToast("语音合成失败,错误码: $code")
//    }
    return code
}

fun SpeechSynthesizer.selectVoicer(activity: Activity) {
    // 云端发音人名称列表
    val mCloudVoicersEntries = activity.resources.getStringArray(R.array.voicer_cloud_entries)
    val mCloudVoicersValue = activity.resources.getStringArray(R.array.voicer_cloud_values)

    val checkedVoicer = getParameter(SpeechConstant.VOICE_NAME)
    val checkedItem = mCloudVoicersValue.indexOf(checkedVoicer)

    AlertDialog.Builder(activity)
        .setTitle("在线合成发音人选项")
        .setSingleChoiceItems(
            mCloudVoicersEntries,  // 单选框有几项,各是什么名字
            checkedItem
        )  // 默认的选项
        { dialog, which ->
            // 点击单选框后的处理
            // 点击了哪一项
            val voicer = mCloudVoicersValue[which]
            if ("catherine" == voicer || "henry" == voicer || "vimary" == voicer) {
                //英文
            } else {
                //汉语
            }
            setParameter(SpeechConstant.VOICE_NAME, voicer)
            dialog.dismiss()
        }
        .show()
}