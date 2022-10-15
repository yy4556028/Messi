package com.yuyang.messi.kotlinui.category.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yuyang.messi.R
import com.yuyang.messi.kotlinui.category.bean.IngredientsResultBean

class IngredientsResultAdapter(data: List<IngredientsResultBean>?) :
    BaseQuickAdapter<IngredientsResultBean, BaseViewHolder>(
        R.layout.activity_ingredients_result_item,
        data
    ) {

    override fun convert(helper: BaseViewHolder, resultBean: IngredientsResultBean) {
        helper.setText(R.id.tvName, resultBean.name)
            .setText(R.id.tvResult, resultBean.result)
            .setTextColor(R.id.tvName, resultBean.getColor())
            .setTextColor(R.id.tvResult, resultBean.getColor())
    }
}