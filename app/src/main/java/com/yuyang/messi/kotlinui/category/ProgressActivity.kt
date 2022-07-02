package com.yuyang.messi.kotlinui.category

import android.os.Bundle
import android.view.View
import com.yuyang.lib_base.ui.header.HeaderLayout
import com.yuyang.messi.R
import com.yuyang.messi.databinding.ActivityProgressBinding
import com.yuyang.messi.ui.base.AppBaseActivity
import com.yuyang.messi.view.Progress.ProgressDialog

class ProgressActivity : AppBaseActivity() {

    private var pd: ProgressDialog? = null

    private lateinit var mBinding: ActivityProgressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityProgressBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val headerLayout = findViewById<HeaderLayout>(R.id.headerLayout)
        headerLayout.showTitle("Progress")

        mBinding.circleProgressBarWithBg.setColorSchemeResources(android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light)
        mBinding.circleProgressBarWithArrow.setColorSchemeResources(android.R.color.holo_orange_light)
        mBinding.circleProgressBarWithArrow.setOnClickListener {
            if (pd == null) {
                pd = ProgressDialog(activity, true)
                pd?.showArrow(true)
                pd?.setProgress(52)
                pd?.setCanceledOnTouchOutside(false)
                pd?.setDimAmount(0.5f)
                pd?.setColorSchemeResources(R.color.blue, R.color.yellow, R.color.green, R.color.red, R.color.black)
            }
            pd?.show()
        }

        mBinding.circleProgressWithoutBg.setColorSchemeResources(android.R.color.holo_red_light)
        mBinding.magicProgressCircle.setSmoothPercent(1f, 3000)

        mBinding.magicProgressBarFlat.setSmoothPercent(1f, 5000)
        mBinding.magicProgressBarFlat.setOnClickListener(View.OnClickListener {
            mBinding.magicProgressBarFlat.percent = 0f
            mBinding.magicProgressBarFlat.setSmoothPercent(1f, 2000)
        })

        mBinding.magicProgressBarUnFlat.setSmoothPercent(1f, 5000)
        mBinding.magicProgressBarUnFlat.setOnClickListener(View.OnClickListener {
            mBinding.magicProgressBarUnFlat.percent = 0f
            mBinding.magicProgressBarUnFlat.setSmoothPercent(1f, 2000)
        })
    }
}