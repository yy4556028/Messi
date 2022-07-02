package com.yuyang.messi.kotlinui.gank

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.yuyang.lib_base.ui.header.HeaderLayout
import com.yuyang.messi.R
import com.yuyang.messi.databinding.ActivityGankBinding
import com.yuyang.messi.ui.base.AppBaseActivity

class GankActivity : AppBaseActivity() {

    private val titleList = listOf("all", "休息视频", "福利", "Android", "iOS", "拓展资源", "前端", "瞎推荐")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityGankBinding = ActivityGankBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            val headerLayout = findViewById<HeaderLayout>(R.id.headerLayout)
            headerLayout.showTitle("Gank")

            viewPager2.apply {

                adapter = object : FragmentStateAdapter(this@GankActivity) {
                    override fun createFragment(position: Int): Fragment {
                        return GankChildFragment.newInstance(titleList[position]) as Fragment
                    }

                    override fun getItemCount(): Int {
                        return titleList.size
                    }
                }

                registerOnPageChangeCallback(object : OnPageChangeCallback() {})

                TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
                    // tab:当前处于选中状态的Tab对象
                    // position:当前Tab所处的位置
                }.attach()
            }

            tabLayout.apply {
                for (i in titleList.indices) {
                    getTabAt(i)?.text = titleList[i]
                }

                addOnTabSelectedListener(object : OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab) {

                        tab.orCreateBadge.apply {
                            backgroundColor = Color.RED
                            badgeTextColor = Color.WHITE
                            number = 100
                        }

                        val tabView = tab.view
                        for (i in 0 until tabView.childCount) {
                            if (tabView.getChildAt(i) is TextView) {
                                val textView = tabView.getChildAt(i) as TextView
                                textView.typeface = Typeface.DEFAULT_BOLD
                            }
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab) {
//                        tab.orCreateBadge.apply {
//                            backgroundColor = Color.RED
//                            badgeTextColor = Color.WHITE
//                            clearNumber()
//                        }
                        tab.removeBadge()
                        val tabView = tab.view
                        for (i in 0 until tabView.childCount) {
                            if (tabView.getChildAt(i) is TextView) {
                                val textView = tabView.getChildAt(i) as TextView
                                textView.typeface = Typeface.DEFAULT
                            }
                        }
                    }

                    override fun onTabReselected(tab: TabLayout.Tab) {}
                })
            }
        }
    }
}