package com.yuyang.messi.ui.category

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatCheckBox
import com.yuyang.messi.ui.base.AppBaseActivity
import com.iflytek.cloud.ui.RecognizerDialog
import com.yuyang.lib_base.ui.header.HeaderLayout
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechRecognizer
import com.iflytek.cloud.SpeechSynthesizer
import com.yuyang.lib_xunfei.*
import com.yuyang.messi.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class XunfeiActivity : AppBaseActivity() {

    private var recognizerDialogBtn: Button? = null
    private var synthesizerBtn: Button? = null
    private var synthesizerVoicer: Button? = null
    private var editText0: EditText? = null
    private var recognizerBtn: Button? = null
    private var punctuationCheckBox: AppCompatCheckBox? = null
    private var editText1: EditText? = null

    @Inject
    lateinit var mSpeechRecognizer: SpeechRecognizer
    @Inject
    lateinit var mRecognizerDialog: RecognizerDialog
    @Inject
    lateinit var mSpeechSynthesizer: SpeechSynthesizer

    override fun getLayoutId(): Int {
        return R.layout.activity_xunfei
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initEvent()
    }

    private fun initView() {
        val headerLayout = findViewById<HeaderLayout>(R.id.headerLayout)
        headerLayout.showLeftBackButton()
        headerLayout.showTitle("讯飞")
        recognizerDialogBtn = findViewById(R.id.activity_xunfei_recognizerDialogBtn)
        synthesizerBtn = findViewById(R.id.activity_xunfei_synthesizerBtn)
        synthesizerVoicer = findViewById(R.id.activity_xunfei_synthesizerVoicer)
        editText0 = findViewById(R.id.activity_xunfei_editText0)
        recognizerBtn = findViewById(R.id.activity_xunfei_recognizerBtn)
        punctuationCheckBox = findViewById(R.id.activity_xunfei_recognizerPunctuationCheckBox)
        editText1 = findViewById(R.id.activity_xunfei_editText1)
        mRecognizerDialog.setListener(object : SimpleListener() {
            override fun onResult(result: String?) {
                editText0?.append(result)
            }
        })
        punctuationCheckBox?.isChecked =
            mSpeechSynthesizer.getParameter(SpeechConstant.ASR_PTT) == "1"
    }

    private fun initEvent() {
        recognizerDialogBtn!!.setOnClickListener { mRecognizerDialog.show() }
        synthesizerBtn!!.setOnClickListener { mSpeechSynthesizer.startSynthesizerAndSpeak(editText0?.text.toString()) }
        synthesizerVoicer!!.setOnClickListener { mSpeechSynthesizer.selectVoicer(this) }
        recognizerBtn!!.setOnClickListener {
            mSpeechRecognizer.startRecognizer(object : SimpleListener() {
                override fun onResult(result: String?) {
                    editText1?.append(result)
                }
            });
        }
        punctuationCheckBox!!.setOnCheckedChangeListener { buttonView, isChecked ->
            mSpeechRecognizer.showPunctuation(isChecked);
        }
        XunfeiRecognizerUtil.bind(
            findViewById(R.id.activity_xunfei_editText2),
            findViewById(R.id.activity_xunfei_image2)
        )
    }
}