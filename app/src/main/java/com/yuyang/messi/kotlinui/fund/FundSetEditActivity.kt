package com.yuyang.messi.kotlinui.fund

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuyang.lib_base.ui.header.HeaderLayout
import com.yuyang.lib_base.utils.CalcUtil
import com.yuyang.lib_base.utils.PixelUtils
import com.yuyang.lib_base.utils.ToastUtil
import com.yuyang.messi.R
import com.yuyang.messi.databinding.ActivityFundSetEditBinding
import com.yuyang.messi.editfilter.MoneyInputTextWatcher
import com.yuyang.messi.kotlinui.fund.adapter.FundAdapter
import com.yuyang.messi.recycler.DragItemTouchCallBack
import com.yuyang.messi.ui.base.AppBaseActivity

class FundSetEditActivity : AppBaseActivity() {

    private lateinit var mBinding: ActivityFundSetEditBinding

    companion object {

        const val KEY_FUND_SET_BEAN = "key_fund_set_bean"
        const val RETURN_FUND_SET_BEAN = "return_fund_set_bean"

        fun getLaunchIntent(context: Context, fundSetBean: FundSetBean): Intent {
            val intent = Intent(context, FundSetEditActivity::class.java)
            intent.putExtra(KEY_FUND_SET_BEAN, fundSetBean)
            return intent
        }

        fun start(context: Context) {
            val starter = Intent(context, FundSetEditActivity::class.java)
            context.startActivity(starter)
        }
    }

    private lateinit var fundAdapter: FundAdapter

    private lateinit var fundSetBean: FundSetBean
    private var remain = 100f//剩余仓位

    private var dataChanged = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFundSetEditBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initView()
    }

    private fun initView() {
        fundSetBean = intent.getParcelableExtra(KEY_FUND_SET_BEAN)!!

        val headerLayout = findViewById<HeaderLayout>(R.id.headerLayout)
        headerLayout.showTitle(fundSetBean.fundSetName)
        headerLayout.showLeftBackButton { onBackPressed() }

        mBinding.rvFundList.apply {
            layoutManager = LinearLayoutManager(this@FundSetEditActivity)

            adapter = FundAdapter(fundSetBean.fundSet).also { fundAdapter = it }
            val callback: ItemTouchHelper.Callback = DragItemTouchCallBack(fundAdapter)
            val touchHelper = ItemTouchHelper(callback)
            touchHelper.attachToRecyclerView(this)

            fundAdapter.setOnItemChildClickListener { _, view, position ->
                val fundBean = fundAdapter.getItem(position) ?: return@setOnItemChildClickListener

                when (view.id) {
                    R.id.tvWeight -> {
                        val etWeight = EditText(this@FundSetEditActivity)
                        val maxRemain =
                            CalcUtil.add(remain.toString(), fundBean.weight.toString()).toFloat()
                        etWeight.setTextColor(ContextCompat.getColor(activity, R.color.textPrimary))
                        etWeight.hint = String.format("剩余仓位：%s", maxRemain)
                        MoneyInputTextWatcher.bindEdit(etWeight)

                        val dialog = AlertDialog.Builder(this@FundSetEditActivity)
                            .setTitle("请输入基金占比")
                            .setView(etWeight)
                            .setPositiveButton("确定") { _, _ ->
                                try {
                                    val weight = etWeight.text.toString().toFloat()
                                    if (weight > maxRemain) {
                                        ToastUtil.showToast("总仓位不能大于100")
                                        return@setPositiveButton
                                    }
                                    fundBean.weight = weight
                                    fundAdapter.notifyItemChanged(position)
                                    updateRemain()
                                    dataChanged = true
                                } catch (e: Exception) {
                                    ToastUtil.showToast("输入有误")
                                }
                            }
                            .setNegativeButton("取消", null)
                            .create()
                        dialog.show()
                    }
                    R.id.ivDelete -> {
                        AlertDialog.Builder(this@FundSetEditActivity)
                            .setTitle("确定从持仓中移除该基金？")
                            .setPositiveButton("确定") { _, _ ->
                                fundAdapter.remove(position)
                                dataChanged = true
                            }
                            .setNegativeButton("取消", null)
                            .create()
                            .show()
                    }
                }
            }

            fundAdapter.itemMoveListener = object : FundAdapter.OnItemMoveListener {
                override fun onItemMove() {
                    dataChanged = true
                }
            }
        }

        mBinding.btnAdd.setOnClickListener {
            showAddFundDialog()
        }
        mBinding.btnSave.setOnClickListener {
            setResult(RESULT_OK, Intent().putExtra(RETURN_FUND_SET_BEAN, fundSetBean))
            ToastUtil.showToast("保存成功")
            dataChanged = false
        }

        updateRemain()
    }

    private fun showAddFundDialog() {
        val linearLayout = LinearLayout(this)
        linearLayout.setPadding(
            PixelUtils.dp2px(8f),
            PixelUtils.dp2px(8f),
            PixelUtils.dp2px(8f),
            PixelUtils.dp2px(8f)
        )
        linearLayout.orientation = LinearLayout.VERTICAL

        val etName = EditText(this)
        etName.setTextColor(ContextCompat.getColor(activity, R.color.textPrimary))
        etName.hint = "请输入基金名称"
        linearLayout.addView(etName)

        val etCode = EditText(this)
        etCode.setTextColor(ContextCompat.getColor(activity, R.color.textPrimary))
        etCode.hint = "请输入基金代码"
        etCode.inputType = InputType.TYPE_CLASS_NUMBER
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.topMargin = PixelUtils.dp2px(8f)
        linearLayout.addView(etCode, params)

        val dialog = AlertDialog.Builder(this)
            .setTitle("添加基金")
            .setView(linearLayout)
            .setPositiveButton("确定") { _, _ ->
                val name = etName.text.toString()
                val code = etCode.text.toString()
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(code)) {
                    ToastUtil.showToast("请填写完整")
                    return@setPositiveButton
                }
                for (oldFund in fundAdapter.data) {
                    if (oldFund.fundCode == code) {
                        ToastUtil.showToast(String.format("%s已在持仓中", name))
                        return@setPositiveButton
                    }
                }
                fundAdapter.addData(FundSetBean.FundBean(name, code, 0f))
                fundSetBean.fundSet = fundAdapter.data
                dataChanged = true
            }
            .setNegativeButton("取消", null)
            .create()
        dialog.show()
    }

    private fun updateRemain() {
        remain = 100f
        for (fundBean in fundSetBean.fundSet) {
            remain = CalcUtil.subtract(remain.toString(), fundBean.weight.toString()).toFloat()
        }
        mBinding.remainProgress.progress = remain.toInt()
        mBinding.tvProgress.text = String.format("%s%%", remain)
    }

    override fun onBackPressed() {
        if (dataChanged) {
            AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("持仓数据已变更，退出将丢失改动数据，是否继续退出")
                .setCancelable(true)
                .setPositiveButton("退出") { _, _ ->
                    super@FundSetEditActivity.onBackPressed()
                }
                .setNegativeButton("取消", null)
                .create()
                .show()
            return
        }
        super.onBackPressed()
    }
}