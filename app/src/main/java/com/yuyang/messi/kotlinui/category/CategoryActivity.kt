package com.yuyang.messi.kotlinui.category

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.lib_bluetooth.BluetoothActivity
import com.google.android.flexbox.*
import com.yuyang.lib_baidu.ui.map.BaiduPoiSearchActivity
import com.yuyang.lib_base.ui.header.HeaderLayout
import com.yuyang.lib_base.utils.CommonUtil
import com.yuyang.lib_scan.monitor.MonitorActivity
import com.yuyang.messi.MessiApp
import com.yuyang.messi.R
import com.yuyang.messi.room.entity.ModuleEntity
import com.yuyang.messi.ui.base.AppBaseActivity
import com.yuyang.messi.ui.camera_crop.IdentityVerifyActivity
import com.yuyang.messi.ui.category.*
import com.yuyang.messi.ui.category.chart.ChartActivity
import com.yuyang.messi.ui.category.nestScroll.NestScrollDemoActivity
import com.yuyang.messi.ui.category.plugin.PluginHostActivity
import com.yuyang.messi.ui.category.scroll.RecyclerViewActivity
import com.yuyang.messi.ui.category.sensor.SensorActivity
import com.yuyang.messi.ui.chat.ChatActivity
import com.yuyang.messi.ui.live.LivePreActivity
import com.yuyang.messi.ui.login.LoginActivity
import com.yuyang.messi.ui.screen_share.ScreenShareActivity
import com.yuyang.messi.ui.shortcut.ShortcutPinActivity
import com.yuyang.messi.utils.ActSwitchAnimTool
import com.yuyang.messi.utils.TransitionUtil
import demo.com.lib_nfc.NfcActivity


/**
 * @see FlexboxLayout
 */
class CategoryActivity : AppBaseActivity() {

    // https://github.com/Yellow5A5/ActSwitchAnimTool
    private var actSwitchAnimTool: ActSwitchAnimTool? = null

    private var categoryAdapter: CategoryAdapter? = null

    public override fun getLayoutId(): Int {
        return R.layout.common_header_recycler
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val headerLayout = findViewById<HeaderLayout>(R.id.headerLayout)
        headerLayout.showTitle("????????????")

        val recyclerView: RecyclerView = findViewById(R.id.common_header_recycler_recyclerView)
        recyclerView.apply {
            layoutManager = FlexboxLayoutManager(activity, FlexDirection.ROW, FlexWrap.WRAP).also {
                it.justifyContent = JustifyContent.FLEX_START   //??????
                it.alignItems = AlignItems.STRETCH  //??????
            }

            adapter = CategoryAdapter(null).also {
                it.onItemClickListener =
                    BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
                        val moduleBean: ModuleEntity = adapter.getItem(position) as ModuleEntity

                        if (TextUtils.equals("?????????", moduleBean.name) ||
                            TextUtils.equals("??????", moduleBean.name)
                        ) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                TransitionUtil.setExitTransition(
                                    activity,
                                    TransitionUtil.explode,
                                    1500
                                )
                                TransitionUtil.setReenterTransition(
                                    activity,
                                    TransitionUtil.explode,
                                    1500
                                )
                            }
                            startActivity(
                                Intent(activity, moduleBean.clazz),
                                ActivityOptionsCompat.makeSceneTransitionAnimation(activity)
                                    .toBundle()
                            )
                            return@OnItemClickListener
                        }
                        if (moduleBean.clazz != null) {
                            actSwitchAnimTool = ActSwitchAnimTool(activity)
                                .setAnimType(ActSwitchAnimTool.MODE_SPREAD)
                                .target(view)
                                .setShrinkBack(true) //????????? activity ?????????
                                .setmColorStart(Color.RED) //?????????????????????
                                .setmColorEnd(Color.RED) //?????????????????????
                                .startActivity(
                                    Intent(activity, moduleBean.clazz),
                                    false
                                ) //if ??????????????? true, ????????????activity
                            actSwitchAnimTool?.setAnimType(ActSwitchAnimTool.MODE_SPREAD)?.build()
                        }
                    }

                categoryAdapter = it
            }
        }
        initData()
    }

    public override fun onResume() {
        super.onResume()
        if (actSwitchAnimTool == null) {
            return
        }
        if (actSwitchAnimTool!!.isWaitingResume) {
            actSwitchAnimTool!!.setAnimType(ActSwitchAnimTool.MODE_SHRINK)
                .setIsWaitingResume(false)
                .build()
        }
    }

    private fun initData() {
        val moduleBeanList = CommonUtil.asList(
            ModuleEntity("????????????", AdaptScreenActivity::class.java),
            ModuleEntity("Progress", ProgressActivity::class.java),
            ModuleEntity("Blur", BlurActivity::class.java),
            ModuleEntity("??????", BadgeActivity::class.java),
            ModuleEntity("????????????", CompressActivity::class.java),
            ModuleEntity("GPS", GpsActivity::class.java),
            ModuleEntity("??????", LoginActivity::class.java),
            ModuleEntity("TouchDemo", TouchActivity::class.java),
            ModuleEntity("??????Poi??????", BaiduPoiSearchActivity::class.java),
            ModuleEntity("??????", DrawActivity::class.java),
            ModuleEntity("Path", PathActivity::class.java),
            ModuleEntity("Socket", SocketActivity::class.java),
            ModuleEntity("??????", TextActivity::class.java),
            ModuleEntity("Span", SpanDemoActivity::class.java),
            ModuleEntity("Lucky", LuckyActivity::class.java),
            ModuleEntity("??????", AvatarActivity::class.java),
            ModuleEntity("?????????", SensorActivity::class.java),
            ModuleEntity("??????", BluetoothActivity::class.java),
            ModuleEntity("??????", RecyclerViewActivity::class.java),
            ModuleEntity("?????????", PickerActivity::class.java),
            ModuleEntity("??????", DownloadActivity::class.java),
            ModuleEntity("??????", ChartActivity::class.java),
            ModuleEntity("??????", KeyboardActivity::class.java),
            ModuleEntity("????????????", XunfeiActivity::class.java),
            ModuleEntity("??????", FontActivity::class.java),
            ModuleEntity("Chat", ChatActivity::class.java),
            ModuleEntity("NFC", NfcActivity::class.java),
            ModuleEntity("??????????????????", IdentityVerifyActivity::class.java),
            ModuleEntity("OpenGL", OpenGLActivity::class.java),
            ModuleEntity("????????????", ShortcutPinActivity::class.java),
            ModuleEntity("BottomSheet", BottomSheetActivity::class.java),
            ModuleEntity("Gif", GifActivity::class.java),
            ModuleEntity("??????", MonitorActivity::class.java),
            ModuleEntity("????????????", ScreenShareActivity::class.java),
            ModuleEntity("WorkManager", WorkManagerActivity::class.java),
            ModuleEntity("????????????", NetStatusActivity::class.java),
            ModuleEntity("????????????", CoroutinesActivity::class.java),
            ModuleEntity("AIDL Demo", AidlDemoActivity::class.java),
            ModuleEntity("??????", LivePreActivity::class.java),
            ModuleEntity("????????????", NestScrollDemoActivity::class.java),
            ModuleEntity("?????????Demo", PluginHostActivity::class.java),
//            ModuleEntity("????????????", FaceLocationActivity::class.java),

        )
        try {
            var clazz = MessiApp.getInstance().classLoader.loadClass("com.example.lpr.LPRActivity")
            moduleBeanList.add(ModuleEntity("????????????", clazz as Class<out Activity>?))
            clazz = MessiApp.getInstance().classLoader.loadClass("com.example.lpr.OpenCvCheckActivity")
            moduleBeanList.add(ModuleEntity("????????????check", clazz as Class<out Activity>?))
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        categoryAdapter?.setNewData(moduleBeanList)
    }
}