package com.yuyang.messi.ui.category

import android.os.Bundle
import com.yuyang.lib_base.ui.header.HeaderLayout

import com.yuyang.lib_base.utils.AndroidBug5497Workaround_Compatible
import com.yuyang.messi.R
import com.yuyang.messi.ui.base.AppBaseActivity

class FontActivity : AppBaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_font
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidBug5497Workaround_Compatible.assistActivity(this)
        initView()
        initEvent()
    }

    private fun initView() {
        val headerLayout = findViewById<HeaderLayout>(R.id.headerLayout)
        headerLayout.showLeftBackButton()
        headerLayout.showTitle("字体")
    }

    private fun initEvent() {}
}
