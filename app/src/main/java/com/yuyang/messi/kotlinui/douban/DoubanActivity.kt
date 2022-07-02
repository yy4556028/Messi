package com.yuyang.messi.kotlinui.douban

import android.os.Bundle
import com.yuyang.lib_base.ui.header.HeaderLayout
import com.yuyang.messi.R
import com.yuyang.messi.databinding.ActivityDoubanBinding
import com.yuyang.messi.ui.base.AppBaseActivity
import com.yuyang.messi.ui.douban.adapter.DoubanPagerAdapter

class DoubanActivity : AppBaseActivity() {

    private lateinit var mBinding: ActivityDoubanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDoubanBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val headerLayout = findViewById<HeaderLayout>(R.id.headerLayout)
        headerLayout.showTitle( "豆瓣")

        mBinding.viewPager.apply {
            adapter = DoubanPagerAdapter(supportFragmentManager,
                    listOf("找书籍", "找电影", "找音乐"),
                    listOf(DoubanBookFragment(), DoubanMovieFragment(), DoubanMusicFragment()),
                    listOf(R.drawable.book, R.drawable.video, R.drawable.music))
            mBinding.livingTabLayout.setupWithViewPager(this)
            offscreenPageLimit = (adapter as DoubanPagerAdapter).count - 1
        }
    }
}