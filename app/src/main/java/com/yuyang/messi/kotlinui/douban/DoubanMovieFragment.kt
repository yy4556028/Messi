package com.yuyang.messi.kotlinui.douban

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.yuyang.lib_base.ui.base.BaseFragment
import com.yuyang.messi.R
import com.yuyang.messi.bean.douban.DoubanMovieBean
import com.yuyang.messi.databinding.FragmentDoubanMovieBinding
import com.yuyang.messi.ui.douban.MovieActivity
import com.yuyang.messi.ui.douban.adapter.DoubanMovieAdapter

class DoubanMovieFragment : BaseFragment() {

    private lateinit var mBinding: FragmentDoubanMovieBinding

    private val viewModel by lazy {
        createViewModel_scopeActivity(DoubanViewModel::class.java)
    }

    private var movieAdapter: DoubanMovieAdapter? = null
    private var mETInput: EditText? = null
    private var mInputDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentDoubanMovieBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun doOnViewCreated() {
        initViews()
        initInputDialog()
    }

    private fun initViews() {
        viewModel.apply {
            initMyStatusModel(DoubanMovieFragment::class.java.simpleName)
            movieList.observe(
                this@DoubanMovieFragment,
                Observer { doubanMovieBeans: List<DoubanMovieBean> ->
                    movieAdapter?.setNewData(doubanMovieBeans)
                })

            getStatusModel(DoubanMovieFragment::class.java.simpleName)?.apply {
                refresh.observe(this@DoubanMovieFragment, Observer { aBoolean: Boolean? ->
                    if (!aBoolean!!) {
                        mBinding.smartRefreshLayout.finishRefresh()
                    }
                })
                hasMoreData.observe(this@DoubanMovieFragment, Observer { aBoolean: Boolean ->
                    if (aBoolean) {
                        mBinding.smartRefreshLayout.setNoMoreData(false)
                        mBinding.smartRefreshLayout.finishLoadMore()
                    } else {
                        mBinding.smartRefreshLayout.finishLoadMoreWithNoMoreData()
                    }
                })
            }
        }

        mBinding.smartRefreshLayout.apply {
            setEnableRefresh(true)
            setEnableLoadMore(true)
            setEnableAutoLoadMore(false)
            setDisableContentWhenRefresh(true) //是否在刷新的时候禁止列表的操作
            setDisableContentWhenLoading(true) //是否在加载的时候禁止列表的操作

            setOnRefreshListener { viewModel.loadMovieHot(true) }
            setOnLoadMoreListener { viewModel.loadMovieHot(false) }

            autoRefresh()
        }

        mBinding.recycleView.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = DoubanMovieAdapter(null).also {
                movieAdapter = it
                movieAdapter?.onItemClickListener =
                    BaseQuickAdapter.OnItemClickListener { _, view, position ->
                        MovieActivity.launchActivity(
                            activity,
                            view.findViewById(R.id.fragment_douban_movie_recycler_item_image),
                            movieAdapter?.getItem(position)
                        )
                    }
            }
        }

        mBinding.floatingActionButton.setOnClickListener { mInputDialog?.show() }
    }

    private fun initInputDialog() {
        mETInput = EditText(activity)
        mETInput?.setTextColor(ContextCompat.getColor(requireActivity(), R.color.textPrimary))
        mInputDialog = AlertDialog.Builder(requireActivity())
            .setTitle("请输入关键字")
            .setView(mETInput)
            .setPositiveButton("确定") { _, _ ->
                val keyword = mETInput?.text.toString()
                if (TextUtils.isEmpty(keyword)) {
                    viewModel.getStatusModel(DoubanBookFragment::class.java.simpleName)?.keyword =
                        keyword
                    mETInput?.text = null
                    mBinding.smartRefreshLayout.autoRefresh()
                }
            }
            .setNegativeButton("取消") { _, _ -> mETInput?.text = null }
            .create()
    }
}