package com.yuyang.messi.kotlinui.fund

import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.NumberPicker
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yuyang.lib_base.ui.header.HeaderLayout
import com.yuyang.lib_base.utils.FileUtil
import com.yuyang.lib_base.utils.StorageUtil
import com.yuyang.lib_base.utils.ToastUtil
import com.yuyang.messi.R
import com.yuyang.messi.databinding.ActivityFundSetBinding
import com.yuyang.messi.kotlinui.fund.adapter.FundSetAdapter
import com.yuyang.messi.recycler.DragItemTouchCallBack
import com.yuyang.messi.ui.base.AppBaseActivity
import java.util.*
import kotlin.collections.ArrayList

class FundSetActivity : AppBaseActivity() {

    private lateinit var mBinding: ActivityFundSetBinding

    private val saveFilePath = StorageUtil.getExternalFile("/Fund/基金组合.txt").absolutePath

    private lateinit var fundSetAdapter: FundSetAdapter

    private var dataChanged = false

    private val launcher =
        registerForActivityResult(StartActivityForResult(), ActivityResultCallback { result ->
            if (RESULT_OK != result.resultCode) return@ActivityResultCallback
            val intentBean: FundSetBean =
                result.data?.getParcelableExtra(FundSetEditActivity.RETURN_FUND_SET_BEAN)!!
            fundSetAdapter.updateItem(intentBean)
            dataChanged = true
        })

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFundSetBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initView()
    }

    private fun initView() {
        val headerLayout = findViewById<HeaderLayout>(R.id.headerLayout)
        headerLayout.showTitle("基金组合")
        headerLayout.showLeftBackButton { onBackPressed() }

        mBinding.rvFundSetList.apply {
            layoutManager = LinearLayoutManager(this@FundSetActivity)

            var fundSetBeanList: List<FundSetBean>? = null
            val data = FileUtil.readFile(saveFilePath)
            if (!TextUtils.isEmpty(data)) {
                try {
                    fundSetBeanList = Gson().fromJson<List<FundSetBean>>(
                        data,
                        object : TypeToken<List<FundSetBean>>() {}.type
                    )
                } catch (ignored: Exception) {
                    ToastUtil.showToast("数据错误")
                }
            }
            adapter = FundSetAdapter(fundSetBeanList).also { fundSetAdapter = it }
            val callback: ItemTouchHelper.Callback = DragItemTouchCallBack(fundSetAdapter)
            val touchHelper = ItemTouchHelper(callback)
            touchHelper.attachToRecyclerView(this)

            fundSetAdapter.setOnItemClickListener { _, _, position ->
                val fundSetBean = fundSetAdapter.getItem(position)
                if (fundSetBean != null) {
                    launcher.launch(
                        FundSetEditActivity.getLaunchIntent(
                            this@FundSetActivity,
                            fundSetBean
                        )
                    )
                }
            }
            fundSetAdapter.setOnItemChildClickListener { _, view, position ->
                val fundSetBean = fundSetAdapter.getItem(position)
                    ?: return@setOnItemChildClickListener

                when (view.id) {
                    R.id.tvInvestMoney -> {
                        val mWeightPicker = NumberPicker(context)
                        mWeightPicker.minValue = 0
                        mWeightPicker.maxValue = 100
                        mWeightPicker.value = fundSetBean.investMoney
                        mWeightPicker.wrapSelectorWheel = false

                        AlertDialog.Builder(context)
                            .setTitle("选择投资金额")
                            .setMessage(fundSetBean.fundSetName)
                            .setView(mWeightPicker)
                            .setPositiveButton("确定") { _, _ ->
                                fundSetBean.investMoney = mWeightPicker.value
                                fundSetAdapter.notifyItemChanged(position)
                                dataChanged = true
                            }
                            .setNegativeButton("取消", null)
                            .show()
                    }
                    R.id.ivDelete -> {
                        AlertDialog.Builder(this@FundSetActivity)
                            .setTitle(String.format("确定删除%s？", fundSetBean.fundSetName))
                            .setPositiveButton("确定") { _, _ ->
                                fundSetAdapter.remove(position)
                                dataChanged = true
                            }
                            .setNegativeButton("取消", null)
                            .create()
                            .show()
                    }
                }
            }
            fundSetAdapter.itemListener = object : FundSetAdapter.OnItemListener {
                override fun onItemCheck(position: Int) {
                    dataChanged = true
                }

                override fun onItemMove() {
                    dataChanged = true
                }
            }
        }

        mBinding.btnSave.setOnClickListener {
            FileUtil.writeFile(saveFilePath, Gson().toJson(fundSetAdapter.data))
            dataChanged = false
        }

        mBinding.btnCreate.setOnClickListener {
            val etName = EditText(this)
            etName.setTextColor(ContextCompat.getColor(activity, R.color.textPrimary))
            etName.hint = "请输入基金组合名称"
            AlertDialog.Builder(this)
                .setTitle("新建基金组合")
                .setView(etName)
                .setPositiveButton("确定") { _, _ ->
                    val name = etName.text.toString()
                    if (!TextUtils.isEmpty(name)) {
                        fundSetAdapter.addData(
                            FundSetBean(
                                System.currentTimeMillis(),
                                name,
                                0,
                                true,
                                mutableListOf()
                            )
                        )
                        dataChanged = true
                    }
                }
                .setNegativeButton("取消", null)
                .create()
                .show()
        }

        mBinding.btnCalc.setOnClickListener {
            val fundSetList = ArrayList<FundSetBean>()
            for (fundSet in fundSetAdapter.data) {
                if (fundSet.isSelect && fundSet.investMoney > 0) {
                    fundSetList.add(fundSet)
                }
            }
            startActivity(FundCalcActivity.getLaunchIntent(this, fundSetList))
        }
    }

    override fun onBackPressed() {
        if (dataChanged) {
            AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("基金数据已变更，退出将丢失改动数据，是否继续退出")
                .setCancelable(true)
                .setPositiveButton("退出") { _, _ ->
                    super@FundSetActivity.onBackPressed()
                }
                .setNegativeButton("取消", null)
                .create()
                .show()
            return
        }
        super.onBackPressed()
    }
}