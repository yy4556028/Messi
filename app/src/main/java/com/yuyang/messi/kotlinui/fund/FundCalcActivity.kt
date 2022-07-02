package com.yuyang.messi.kotlinui.fund

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuyang.lib_base.ui.header.HeaderLayout
import com.yuyang.lib_base.utils.CalcUtil
import com.yuyang.messi.R
import com.yuyang.messi.databinding.ActivityFundCalcBinding
import com.yuyang.messi.kotlinui.fund.adapter.FundCalcAdapter
import com.yuyang.messi.ui.base.AppBaseActivity
import java.util.*

class FundCalcActivity : AppBaseActivity() {

    private lateinit var mBinding: ActivityFundCalcBinding

    companion object {

        const val KEY_FUND_SET_LIST = "key_fund_set_list"

        fun getLaunchIntent(context: Context, fundSetList: ArrayList<FundSetBean>): Intent {
            val intent = Intent(context, FundCalcActivity::class.java)
            intent.putParcelableArrayListExtra(KEY_FUND_SET_LIST, fundSetList)
            return intent
        }
    }

    private lateinit var fundAdapter: FundCalcAdapter

    private val fundList = mutableListOf<FundSetBean.FundBean>()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFundCalcBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initView()
        initData()
    }

    private fun initView() {
        val headerLayout = findViewById<HeaderLayout>(R.id.headerLayout)
        headerLayout.showTitle("基金持仓")

        mBinding.rvFundSetList.apply {
            layoutManager = LinearLayoutManager(this@FundCalcActivity)

            adapter = FundCalcAdapter(null).also { fundAdapter = it }
        }
    }

    private fun initData() {
        val fundSetList: ArrayList<FundSetBean> =
            intent.getParcelableArrayListExtra(KEY_FUND_SET_LIST)!!
        var totalWeight = 0f
        for (fundSetBean in fundSetList) {
            for (fundBean in fundSetBean.fundSet) {
                var searchBean: FundSetBean.FundBean? = null
                for (inBean in fundList) {
                    if (inBean.fundCode == fundBean.fundCode) {
                        searchBean = inBean
                        break
                    }
                }

                val currentWeightStr = CalcUtil.multiply(
                    fundBean.weight.toString(),
                    fundSetBean.investMoney.toString()
                ).toString()
                totalWeight = CalcUtil.add(totalWeight.toString(), currentWeightStr).toFloat()
                if (searchBean == null) {
                    fundBean.weight = currentWeightStr.toFloat()
                    fundList.add(fundBean)
                } else {
                    searchBean.weight =
                        CalcUtil.add(searchBean.weight.toString(), currentWeightStr).toFloat()
                }
            }
        }
        fundAdapter.totalWeight = totalWeight
        fundList.sortWith { lBean, rBean -> rBean.weight.compareTo(lBean.weight) }
        fundAdapter.setNewData(fundList)
    }
}