package com.yuyang.messi.kotlinui.main

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.google.android.material.navigation.NavigationBarView
import com.liulishuo.filedownloader.FileDownloader
import com.yuyang.lib_baidu.utils.BaiduLocationUtil
import com.yuyang.lib_baidu.utils.BaiduLocationUtil.OnLocationListener
import com.yuyang.lib_base.ui.header.HeaderLayout
import com.yuyang.lib_base.utils.ToastUtil
import com.yuyang.lib_share.ShareSDKUtil
import com.yuyang.messi.MessiApp
import com.yuyang.messi.R
import com.yuyang.messi.databinding.ActivityMainBinding
import com.yuyang.messi.kotlinui.beauty.BeautyActivity
import com.yuyang.messi.kotlinui.category.CategoryActivity
import com.yuyang.messi.kotlinui.circle_menu.CircleMenuActivity
import com.yuyang.messi.kotlinui.douban.DoubanActivity
import com.yuyang.messi.kotlinui.gank.GankActivity
import com.yuyang.messi.kotlinui.main.tools.ToolsFragment
import com.yuyang.messi.ui.base.AppBaseActivity
import com.yuyang.messi.ui.card.MeipaiFragment
import com.yuyang.messi.ui.common.MyPagerAdapter
import com.yuyang.messi.ui.common.NullActivity
import com.yuyang.messi.ui.football.FootballActivity
import com.yuyang.messi.ui.game.MindCameraActivity
import com.yuyang.messi.ui.home.TestFragment
import com.yuyang.messi.ui.home.overwork.OverworkFlexFragment
import com.yuyang.messi.ui.main.AboutActivity
import com.yuyang.messi.ui.setting.SettingActivity
import com.yuyang.messi.utils.SnackBarUtil
import com.yuyang.messi.view.GravityViewUtil
import com.yuyang.messi.widget.watermark.WaterMarkManager
import kotlin.system.exitProcess


class MainActivity : AppBaseActivity() {

    private lateinit var mBinding: ActivityMainBinding

    private var headerLayout: HeaderLayout? = null

    private val fragmentList =
        listOf<Fragment>(MeipaiFragment(), ToolsFragment(), OverworkFlexFragment(), TestFragment())

    private var lastBackKeyDownTick: Long = 0
    private val maxDoubleClickDuration: Long = 2000

    private val permissionsLauncher =
        registerForActivityResult(RequestMultiplePermissions()) { result ->
            val deniedAskList: MutableList<String> = arrayListOf()
            val deniedNoAskList: MutableList<String> = arrayListOf()
            for ((key, value) in result) {
                if (!value) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, key)) {
                        deniedAskList.add(key)
                    } else {
                        deniedNoAskList.add(key)
                    }
                }
            }
            if (deniedAskList.size == 0 && deniedNoAskList.size == 0) { //全通过
                BaiduLocationUtil.getInstance()
                    .registerLocationListener(object : OnLocationListener {
                        override fun onLocation(
                            bdLocation: BDLocation,
                            mLocationClient: LocationClient
                        ) {
                            val tvAddress: TextView = mBinding.navigationView.getHeaderView(0)
                                .findViewById(R.id.tvAddress)
                            tvAddress.text = bdLocation.addrStr
                            BaiduLocationUtil.getInstance().unregisterLocationListener(this)
                        }
                    })
                BaiduLocationUtil.getInstance().startLocation(0)
            } else if (deniedNoAskList.size > 0) {
                finish()
            } else {
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initView()
        WaterMarkManager.setText("于洋" + "    " + "13921400723")
        permissionsLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (closeDrawers()) return

                val currentTick = System.currentTimeMillis()
                if (currentTick - lastBackKeyDownTick > maxDoubleClickDuration) {
                    ToastUtil.showToast("再按一次退出程序")
                    SnackBarUtil.makeShort(activity, "再按一次退出").warning()
                    lastBackKeyDownTick = currentTick
                } else { //            moveTaskToBack(true)
                    FileDownloader.getImpl().pauseAll()
                    finish()
                    exitProcess(0)
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        BaiduLocationUtil.getInstance().stopLocation()
    }

    private fun initView() {
//        val jankFrameListener = JankStats.OnFrameListener { frameData ->
//            // 真正的应用程序会做一些比记录这个更有趣的事情...
//            Log.v("JankStatsSample", frameData.toString())
//        }
//        val jankStats = JankStats.createAndTrack(
//        window,
//        Dispatchers.Default.asExecutor(),
//        jankFrameListener,
//        )

        headerLayout = findViewById(R.id.headerLayout)
        headerLayout?.apply {
            hideLeftBackButton()
            showTitle("主页")
        }
        mBinding.drawerLayout.apply {
            setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END)
            //ActionBarDrawerToggle配合Toolbar，实现Toolbar上菜单按钮开关效果。
            val mActionBarDrawerToggle =
                ActionBarDrawerToggle(activity, this, headerLayout, R.string.open, R.string.close)
            mActionBarDrawerToggle.syncState()
            addDrawerListener(mActionBarDrawerToggle)
            addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                    val mContent: View = getChildAt(0)
                    val scale = 1 - slideOffset
                    val endScale = 0.8f + scale * 0.2f
                    if (drawerView.tag == "LEFT") {
                        //do nothing
                    } else if (drawerView.tag == "RIGHT") {
                        mContent.translationX = -drawerView.measuredWidth * slideOffset
                        mContent.pivotX = mContent.measuredWidth.toFloat()
                        mContent.pivotY = mContent.measuredHeight / 2.toFloat()
                        mContent.invalidate()
                        mContent.scaleX = endScale
                        mContent.scaleY = endScale
                    }
                }

                override fun onDrawerClosed(drawerView: View) {
                    setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END)
                }
            })
        }
        mBinding.navigationView.apply {
            GravityViewUtil.addGravityMonitor(
                activity,
                getHeaderView(0).findViewById(R.id.gravityCircleImageView)
            )
//            val avatar: GravityShapeableImageView =
//                getHeaderView(0).findViewById(R.id.gravityCircleImageView)
//            avatar.setOnClickListener { avatar.toggle() }
            setNavigationItemSelectedListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.nav_menu_douban -> {
                        startActivity(Intent(activity, DoubanActivity::class.java))
                    }
                    R.id.nav_menu_category -> {
                        startActivity(Intent(activity, CategoryActivity::class.java))
                    }
                    R.id.nav_menu_gank -> {
                        startActivity(Intent(activity, GankActivity::class.java))
                    }
                    R.id.nav_menu_media -> {
                        startActivity(Intent(activity, CircleMenuActivity::class.java))
                    }
                    R.id.nav_menu_game -> {
                        startActivity(Intent(activity, MindCameraActivity::class.java))
                    }
                    R.id.nav_menu_setting -> {
                        startActivity(Intent(activity, SettingActivity::class.java))
                    }
                    R.id.nav_menu_beauty -> {
                        startActivity(Intent(activity, BeautyActivity::class.java))
                    }
                    R.id.nav_menu_football -> {
                        startActivity(Intent(activity, FootballActivity::class.java))
                    }
                    R.id.nav_menu_other -> {
                        startActivity(Intent(activity, NullActivity::class.java))
                    }
                    else -> {
                        ToastUtil.showToast(item.title)
                    }
                }
                item.isChecked = false
                true
            }
        }

        mBinding.viewPager.apply {
            offscreenPageLimit = fragmentList.size - 1
            addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    when (position) {
                        0 -> mBinding.bottomNavView.selectedItemId = R.id.nav_menu_home
                        1 -> mBinding.bottomNavView.selectedItemId = R.id.nav_menu_tools
                        2 -> mBinding.bottomNavView.selectedItemId = R.id.nav_menu_gank
                        3 -> mBinding.bottomNavView.selectedItemId = R.id.nav_menu_profile
                    }
                }
            })
            adapter = MyPagerAdapter(supportFragmentManager, null, fragmentList)
        }
        mBinding.bottomNavView.apply {

            getOrCreateBadge(R.id.nav_menu_home).apply {
                backgroundColor = Color.RED
                badgeTextColor = Color.WHITE
                number = 99999
                maxCharacterCount = 3
                removeBadge(R.id.nav_menu_home)
            }

            setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { menuItem ->
                headerLayout?.showTitle(menuItem.title.toString())
                when (menuItem.itemId) {
                    R.id.nav_menu_home -> mBinding.viewPager.setCurrentItem(0, false)
                    R.id.nav_menu_tools -> mBinding.viewPager.setCurrentItem(1, false)
                    R.id.nav_menu_work -> mBinding.viewPager.setCurrentItem(2, false)
                    R.id.nav_menu_profile -> mBinding.viewPager.setCurrentItem(3, false)
                    else -> return@OnItemSelectedListener false
                }
                return@OnItemSelectedListener true
            }
            )
            itemIconTintList = null
//            menu.getItem(0).icon = null

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> MessiApp.getInstance().showDialog("全局Dialog", "Message")
            R.id.action_settings -> {
                mBinding.drawerLayout.openDrawer(GravityCompat.END)
                mBinding.drawerLayout.setDrawerLockMode(
                    DrawerLayout.LOCK_MODE_UNLOCKED,
                    GravityCompat.END
                )
            }
            R.id.action_share -> ShareSDKUtil.showShare(
                activity,
                null,
                "如果奇迹有颜色,那么一定是红蓝...",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=148976" +
                        "4411&di=3f8dac2787cb37dcdc7dc0667efc1446&imgtype=jpg&er=1&src=http%3A%" +
                        "2F%2Ffwimage.cnfanews.com%2Fwebsiteimg%2F2017%2F20170310%2F28885%2Fzx7z-fychhuq3714169.jpg",
                "http://sports.le.com/video/28114685.html"
            )
            R.id.action_about -> startActivity(Intent(activity, AboutActivity::class.java))

        }
        return super.onOptionsItemSelected(item)
    }

    fun closeDrawers(): Boolean {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) { //当前抽屉是打开的，则关闭
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
            return true
        }

        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) { //当前抽屉是打开的，则关闭
            mBinding.drawerLayout.closeDrawer(GravityCompat.END)
            return true
        }
        return false
    }
}