package com.yuyang.messi.kotlinui.category

import android.os.Bundle
import android.view.View
import com.yuyang.lib_base.ui.header.HeaderLayout
import com.yuyang.messi.R
import com.yuyang.messi.databinding.ActivityUnlockBinding
import com.yuyang.messi.ui.base.AppBaseActivity
import com.yuyang.messi.view.Progress.ProgressDialog
import com.yuyang.messi.view.unLock.UnLockAdapter

class UnLockActivity : AppBaseActivity() {

    private lateinit var mBinding: ActivityUnlockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityUnlockBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val headerLayout = findViewById<HeaderLayout>(R.id.headerLayout)
        headerLayout.showTitle("UnLock")

        mBinding.unLockView.password = arrayListOf(9, 5, 2, 7)
        mBinding.unLockView.adapter = UnLockAdapter()
    }
}