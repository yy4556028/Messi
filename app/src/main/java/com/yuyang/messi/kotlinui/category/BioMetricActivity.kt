package com.yuyang.messi.kotlinui.category

import android.os.Bundle
import com.yuyang.lib_base.ui.header.HeaderLayout
import com.yuyang.lib_base.utils.ToastUtil
import com.yuyang.messi.R
import com.yuyang.messi.databinding.ActivityNullBinding
import com.yuyang.messi.ui.base.AppBaseActivity
import com.yuyang.messi.utils.BioMetricUtil

class BiometricActivity : AppBaseActivity() {

    private lateinit var mBinding: ActivityNullBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityNullBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val headerLayout = findViewById<HeaderLayout>(R.id.headerLayout)
        headerLayout.showTitle("生物识别")

        if (BioMetricUtil.checkBiometric(this)) {
            BioMetricUtil.authenticate(this)
        } else {
            ToastUtil.showToast("不支持")
        }
    }
}