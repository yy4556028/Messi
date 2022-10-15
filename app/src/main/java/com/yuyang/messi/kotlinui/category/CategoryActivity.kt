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
        headerLayout.showTitle("分类功能")

        val recyclerView: RecyclerView = findViewById(R.id.common_header_recycler_recyclerView)
        recyclerView.apply {
            layoutManager = FlexboxLayoutManager(activity, FlexDirection.ROW, FlexWrap.WRAP).also {
                it.justifyContent = JustifyContent.FLEX_START   //默认
                it.alignItems = AlignItems.STRETCH  //默认
            }

            adapter = CategoryAdapter(null).also {
                it.onItemClickListener =
                    BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
                        val moduleBean: ModuleEntity = adapter.getItem(position) as ModuleEntity

                        if (TextUtils.equals("传感器", moduleBean.name) ||
                            TextUtils.equals("列表", moduleBean.name)
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
                                .setShrinkBack(true) //返回该 activity 时收缩
                                .setmColorStart(Color.RED) //伸展时的背景色
                                .setmColorEnd(Color.RED) //收缩时的背景色
                                .startActivity(
                                    Intent(activity, moduleBean.clazz),
                                    false
                                ) //if 第二个参数 true, 结束当前activity
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
            ModuleEntity("屏幕适配", AdaptScreenActivity::class.java),
            ModuleEntity("Progress", ProgressActivity::class.java),
            ModuleEntity("Blur", BlurActivity::class.java),
            ModuleEntity("角标", BadgeActivity::class.java),
            ModuleEntity("压缩图片", CompressActivity::class.java),
            ModuleEntity("GPS", GpsActivity::class.java),
            ModuleEntity("登录", LoginActivity::class.java),
            ModuleEntity("TouchDemo", TouchActivity::class.java),
            ModuleEntity("百度Poi检索", BaiduPoiSearchActivity::class.java),
            ModuleEntity("涂鸦", DrawActivity::class.java),
            ModuleEntity("Path", PathActivity::class.java),
            ModuleEntity("Socket", SocketActivity::class.java),
            ModuleEntity("文本", TextActivity::class.java),
            ModuleEntity("Span", SpanDemoActivity::class.java),
            ModuleEntity("Lucky", LuckyActivity::class.java),
            ModuleEntity("头像", AvatarActivity::class.java),
            ModuleEntity("传感器", SensorActivity::class.java),
            ModuleEntity("蓝牙", BluetoothActivity::class.java),
            ModuleEntity("列表", RecyclerViewActivity::class.java),
            ModuleEntity("选择器", PickerActivity::class.java),
            ModuleEntity("下载", DownloadActivity::class.java),
            ModuleEntity("图表", ChartActivity::class.java),
            ModuleEntity("键盘", KeyboardActivity::class.java),
            ModuleEntity("讯飞语音", XunfeiActivity::class.java),
            ModuleEntity("字体", FontActivity::class.java),
            ModuleEntity("Chat", ChatActivity::class.java),
            ModuleEntity("NFC", NfcActivity::class.java),
            ModuleEntity("身份证正反面", IdentityVerifyActivity::class.java),
            ModuleEntity("OpenGL", OpenGLActivity::class.java),
            ModuleEntity("快捷方式", ShortcutPinActivity::class.java),
            ModuleEntity("BottomSheet", BottomSheetActivity::class.java),
            ModuleEntity("Gif", GifActivity::class.java),
            ModuleEntity("监控", MonitorActivity::class.java),
            ModuleEntity("屏幕共享", ScreenShareActivity::class.java),
            ModuleEntity("WorkManager", WorkManagerActivity::class.java),
            ModuleEntity("网络状态", NetStatusActivity::class.java),
            ModuleEntity("协程测试", CoroutinesActivity::class.java),
            ModuleEntity("AIDL Demo", AidlDemoActivity::class.java),
            ModuleEntity("直播", LivePreActivity::class.java),
            ModuleEntity("嵌套滑动", NestScrollDemoActivity::class.java),
            ModuleEntity("插件化Demo", PluginHostActivity::class.java),
//            ModuleEntity("虚拟定位", FaceLocationActivity::class.java),
            ModuleEntity("UnLock", UnLockActivity::class.java),
            ModuleEntity("配料表", IngredientsActivity::class.java),

        )
        try {
            var clazz = MessiApp.getInstance().classLoader.loadClass("com.example.lpr.LPRActivity")
            moduleBeanList.add(ModuleEntity("车牌识别", clazz as Class<out Activity>?))
            clazz = MessiApp.getInstance().classLoader.loadClass("com.example.lpr.OpenCvCheckActivity")
            moduleBeanList.add(ModuleEntity("车牌识别check", clazz as Class<out Activity>?))
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        categoryAdapter?.setNewData(moduleBeanList)
    }
}