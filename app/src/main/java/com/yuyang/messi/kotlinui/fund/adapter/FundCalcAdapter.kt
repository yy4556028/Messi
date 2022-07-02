package com.yuyang.messi.kotlinui.fund.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yuyang.lib_base.config.CommonConstant
import com.yuyang.messi.R
import com.yuyang.messi.kotlinui.fund.FundSetBean

class FundCalcAdapter(data: List<FundSetBean.FundBean>?) :
        BaseQuickAdapter<FundSetBean.FundBean, BaseViewHolder>(R.layout.activity_fund_calc_item, data) {

    var totalWeight = 0f

    override fun convert(helper: BaseViewHolder, fundBean: FundSetBean.FundBean) {

        helper.setText(R.id.tvFundName, fundBean.fundName)
                .setText(R.id.tvFundCode, fundBean.fundCode)
                .setText(R.id.tvWeight, CommonConstant.twoDecFormat.format(fundBean.weight * 100f / totalWeight) + "%")
    }
}