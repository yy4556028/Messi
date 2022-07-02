package com.yuyang.messi.kotlinui.beauty

import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.yuyang.lib_base.ui.header.HeaderLayout
import com.yuyang.lib_base.utils.CommonUtil
import com.yuyang.lib_base.utils.PixelUtils
import com.yuyang.messi.R
import com.yuyang.messi.databinding.ActivityBeautyBinding
import com.yuyang.messi.ui.base.AppBaseActivity
import com.yuyang.messi.ui.common.photo.PhotoShowActivity

class BeautyActivity : AppBaseActivity() {

    private lateinit var mBinding: ActivityBeautyBinding

    private val viewModel by lazy {
        createViewModel(BeautyViewModel::class.java)
    }

    private var beautyAdapter: BeautyAdapter? = null
    private var beauty1Adapter: Beauty1Adapter? = null
    private var searchDialog: AlertDialog? = null
    private var etSearch: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBeautyBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initView()
        initViewModel()
        initSearchDialog()
        mBinding.smartRefreshLayout.autoRefresh()
    }

    private fun initView() {
        val headerLayout = findViewById<HeaderLayout>(R.id.headerLayout)
        headerLayout.showTitle("百度美女bugfix")

        mBinding.smartRefreshLayout.apply {
            setEnableRefresh(true)
            setEnableLoadMore(true)
            setDisableContentWhenRefresh(true)//是否在刷新的时候禁止列表的操作
            setDisableContentWhenLoading(true)//是否在加载的时候禁止列表的操作
            setOnRefreshListener { viewModel.loadImageOther(true) }
            setOnLoadMoreListener { viewModel.loadImageOther(false) }
        }
        mBinding.recyclerView.let {
            val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            it.layoutManager = layoutManager
            it.itemAnimator = DefaultItemAnimator()

            val itemWidth = ((CommonUtil.getScreenWidth() - PixelUtils.dp2px(16f)) / layoutManager.spanCount)
//            it.adapter = BeautyAdapter(itemWidth, null).apply {
//                beautyAdapter = this
//                beautyAdapter?.onItemClickListener = BaseQuickAdapter.OnItemClickListener { _, view, position ->
//                    PhotoShowActivity.launchActivity(activity, beautyAdapter?.getItem(position)?.middleURL, view)
//                }
//            }
            it.adapter = Beauty1Adapter(itemWidth, null).apply {
                beauty1Adapter = this
                beauty1Adapter?.onItemClickListener = BaseQuickAdapter.OnItemClickListener { _, view, position ->
                    PhotoShowActivity.launchActivity(activity, beauty1Adapter?.getItem(position)?.imageUrl, view)
                }
            }
        }

        mBinding.floatingActionButton.let {
            it.setOnClickListener { searchDialog?.show() }
        }
    }

    private fun initViewModel() {
//        viewModel.beanList.observe(this, Observer { beautyBeanList ->
//            beautyAdapter?.setNewData(beautyBeanList)
//        })
        viewModel.bean1List.observe(this, Observer { beautyBeanList ->
            beauty1Adapter?.setNewData(beautyBeanList)
        })
        viewModel.refresh.observe(this, Observer { aBoolean: Boolean? ->
            if (!aBoolean!!) {
                mBinding.smartRefreshLayout.finishRefresh()
            }
        })
        viewModel.hasMoreData.observe(this, Observer { aBoolean: Boolean ->
            if (aBoolean) {
                mBinding.smartRefreshLayout.setNoMoreData(false)
                mBinding.smartRefreshLayout.finishLoadMore()
            } else {
                mBinding.smartRefreshLayout.finishLoadMoreWithNoMoreData()
            }
        })
    }

    private fun initSearchDialog() {
        etSearch = EditText(this)
        etSearch?.setTextColor(ContextCompat.getColor(activity, R.color.textPrimary))
        searchDialog = AlertDialog.Builder(this)
                .setTitle("请输入关键字")
                .setView(etSearch)
                .setPositiveButton("确定") { _, _ ->
                    val str = etSearch?.text.toString()
                    if (!TextUtils.isEmpty(str)) {
                        viewModel.setKeyword(str)
                        etSearch?.setText("")
                        mBinding.smartRefreshLayout.autoRefresh()
                    }
                }
                .setNegativeButton("取消") { _, _ ->
                    etSearch?.text = null
                }
                .create()
    }
}