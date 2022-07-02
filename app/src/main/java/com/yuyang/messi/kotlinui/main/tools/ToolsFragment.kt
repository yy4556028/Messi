package com.yuyang.messi.kotlinui.main.tools

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.SweepGradient
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.recyclerview.widget.*
import com.yuyang.lib_base.recyclerview.item_decoration.GridItemDecoration
import com.yuyang.lib_base.recyclerview.snaphelper.StartSnapHelper
import com.yuyang.lib_base.ui.base.BaseFragment
import com.yuyang.lib_base.utils.PixelUtils
import com.yuyang.lib_base.utils.RevealUtil
import com.yuyang.lib_base.utils.ToastUtil
import com.yuyang.messi.R
import com.yuyang.messi.databinding.FragmentToolsBinding
import com.yuyang.messi.room.entity.ModuleEntity
import com.yuyang.messi.ui.common.BannerFragment
import com.yuyang.messi.ui.home.taobao.AdvertRecyclerAdapter
import com.yuyang.messi.ui.home.taobao.RecyclerPagerAdapter
import com.yuyang.messi.utils.SharedPreferencesUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class ToolsFragment : BaseFragment() {

    private lateinit var mBinding: FragmentToolsBinding

    private var customTouchHelper: CustomTouchHelper? = null
    private var recyclerAdapter: CustomRecyclerAdapter? = null
    private var launcher: ActivityResultLauncher<Intent>? =
        registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
            initData()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentToolsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun doOnViewCreated() {
        initViews()
        initData()
    }

    private fun initViews() {
        mBinding.rvCustom.apply {
            layoutManager = GridLayoutManager(activity, 4)
            addItemDecoration(GridItemDecoration(PixelUtils.dp2px(4f), false))
            val itemTouchHelper =
                ItemTouchHelper(CustomTouchHelper(false).also { customTouchHelper = it })
            adapter = CustomRecyclerAdapter(activity, itemTouchHelper, false).also {
                recyclerAdapter = it
            }
            itemTouchHelper.attachToRecyclerView(this)

            recyclerAdapter?.setOnItemClickListener(object :
                CustomRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(view: View, moduleEntity: ModuleEntity?) {
                    if (moduleEntity == null) {
                        val intent = Intent(context, CustomSetActivity::class.java)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            launcher?.launch(RevealUtil.wrapIntent(intent, view))
                            activity?.overridePendingTransition(0, 0)
                        } else {
                            launcher?.launch(intent)
                        }
                    } else {
                        ModuleTool.dealModuleClick(activity, moduleEntity)
                    }
                }

                override fun onDelete(moduleEntity: ModuleEntity) {}
            })
        }

        val bannerFragment: BannerFragment? =
            childFragmentManager.findFragmentById(R.id.bannerFragment) as BannerFragment
        bannerFragment?.updateUrl(
            listOf(
                "http://cms-bucket.nosdn.127.net/ddff45c6041c41e48ddc78dd64375d0820170311100045.jpeg?imageView&thumbnail=550x0",
                "http://tv.cnr.cn/jbty/201204/W020120405345155502952.jpg"
            )
        )

        mBinding.rvBanner.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
            adapter = RecyclerPagerAdapter(
                listOf(
                    "http://cms-bucket.nosdn.127.net/ddff45c6041c41e48ddc78dd64375d0820170311100045.jpeg?imageView&thumbnail=550x0",
                    "http://tv.cnr.cn/jbty/201204/W020120405345155502952.jpg",
                    "http://img1.dongqiudi.com/fastdfs/M00/04/43/oYYBAFfB9quAFORnAARqi9mYMpM828.gif",
                )
            )
            PagerSnapHelper().attachToRecyclerView(this)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                var minScale = 0.8f
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (RecyclerView.SCROLL_STATE_IDLE == scrollState) {
                        return
                    }
                    for (i in 0 until (layoutManager as LinearLayoutManager).childCount) {
                        val child: View? = (layoutManager as LinearLayoutManager).getChildAt(i)
                        child?.let {
                            val offset: Int =
                                (layoutManager as LinearLayoutManager).getDecoratedLeft(it)
                            val scale = minScale + (1 - minScale) * (1 - Math.min(
                                abs(offset.toFloat() / measuredWidth),
                                1f
                            ))
                            it.scaleX = scale
                            it.scaleY = scale
                        }
                    }
                }
            })

            smoothScrollBy(1, 0)
        }

        mBinding.advertView.apply {
            setData(
                listOf(
                    "如果奇迹有颜色，那么一定是红蓝",
                    "人面不知何处去 桃花依旧笑春风",
                    "道者深方能言之浅"
                )
            )

            setOnItemClickListener { _, text -> ToastUtil.showToast(text) }
        }

        mBinding.advertRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = AdvertRecyclerAdapter(activity)
            setOnTouchListener { _, _ -> true }
            StartSnapHelper().attachToRecyclerView(this)

            (adapter as AdvertRecyclerAdapter).setData(
                listOf(
                    "如果奇迹有颜色，那么一定是红蓝",
                    "人面不知何处去 桃花依旧笑春风",
                    "道者深方能言之浅",
                    "遇事不决，可问春风"
                )
            )

            scrollToPosition(0)
            if ((adapter as AdvertRecyclerAdapter).itemCount > 2) {
                Observable.interval(2, 2, TimeUnit.SECONDS)
                    .doOnSubscribe {}
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        smoothScrollBy(
                            0,
                            measuredHeight / (adapter as AdvertRecyclerAdapter).showCount
                        )
                    }
            }
        }

        mBinding.meterView.apply {
            val radius = (measuredWidth - paddingLeft - paddingRight) / 2
            val mSweepGradient = SweepGradient(
                radius.toFloat(),
                radius.toFloat(), intArrayOf(
                    Color.parseColor("#F05B52"),
                    Color.parseColor("#F05B52"),
                    Color.parseColor("#F05B52"),
                    Color.parseColor("#F05B52"),
                    Color.parseColor("#F05B52"),
                    Color.parseColor("#F5A623"),
                    Color.parseColor("#00CC74")
                ),
                null
            )
            arcPaint.shader = mSweepGradient
            setArcWidth(PixelUtils.dp2px(12f))
            setOnClickListener { setCurrentValue(75f, true) }
        }
    }

    private fun initData() {
        val beanList = SharedPreferencesUtil.getModuleCustomOrder()
        recyclerAdapter?.setData(beanList)
        customTouchHelper?.beanList = beanList
    }
}