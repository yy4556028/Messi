package com.yuyang.messi.kotlinui.diet

import android.os.Bundle
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.baidu.ocr.sdk.OCR
import com.baidu.ocr.sdk.OnResultListener
import com.baidu.ocr.sdk.exception.OCRError
import com.baidu.ocr.sdk.model.AccessToken
import com.yuyang.lib_baidu.utils.BaiduRecognizeService
import com.yuyang.lib_base.utils.ToastUtil
import com.yuyang.messi.bean.ImageBean
import com.yuyang.messi.databinding.ActivityIngredientsBinding
import com.yuyang.messi.ui.base.AppBaseActivity
import com.yuyang.messi.ui.media.AlbumActivity

class IngredientsActivity : AppBaseActivity() {

    private lateinit var binding: ActivityIngredientsBinding

    private lateinit var ocrToken: String

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback { result ->
            if (RESULT_OK != result.resultCode) return@ActivityResultCallback
            val allImgList =
                result.data?.getParcelableArrayListExtra<ImageBean>(AlbumActivity.SELECTED_PHOTOS)
            allImgList?.let {
                val imageBean = it.first()

                BaiduRecognizeService.recGeneralBasic(
                    this, imageBean.path
                ) { result ->
                    binding.etInput.append("\n" + result)
                }
            }
        })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIngredientsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAccessTokenWithAkSk()
        initView()
    }

    private fun initAccessTokenWithAkSk() {
        OCR.getInstance(this).initAccessTokenWithAkSk(object : OnResultListener<AccessToken> {
            override fun onResult(result: AccessToken) {
                ocrToken = result.accessToken
            }

            override fun onError(error: OCRError) {
                error.printStackTrace()
                ToastUtil.showToast("AK，SK方式获取token失败${error.message}".trimIndent())
            }
        }, applicationContext, "mBploLaojMSe02yq7pQvOB0O", "q4NBIyoxOM5MpBaxAoh9KFj5ntuWmGF2")
    }

    private fun initView() {
        binding.btnSelectPhoto.setOnClickListener {
            if (!this::ocrToken.isInitialized) {
                ToastUtil.showToast("token还未成功获取")
                return@setOnClickListener
            }
            launcher.launch(
                AlbumActivity.getLaunchIntent(
                    activity,
                    1,
                    false,
                    true,
                    true,
                    null
                )
            )
        }
        binding.btnAnalyse.setOnClickListener{
            IngredientsResultActivity.start(this, binding.etInput.text.toString())
        }
    }
}