package com.yuyang.messi.kotlinui.fund.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yuyang.messi.R
import com.yuyang.messi.kotlinui.fund.FundSetBean
import com.yuyang.messi.recycler.DragItemTouchCallBack
import java.util.*

class FundAdapter(data: List<FundSetBean.FundBean>?) :
        BaseQuickAdapter<FundSetBean.FundBean, BaseViewHolder>(R.layout.activity_fund_item, data), DragItemTouchCallBack.ItemTouchHelperAdapter {

    override fun convert(helper: BaseViewHolder, fundBean: FundSetBean.FundBean) {
        helper.setText(R.id.tvFundName, fundBean.fundName)
                .setText(R.id.tvFundCode, fundBean.fundCode)
                .setText(R.id.tvWeight, String.format("%.2f%%", fundBean.weight))
                .addOnClickListener(R.id.tvWeight, R.id.ivDelete)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(data, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        itemMoveListener?.onItemMove()
    }

    interface OnItemMoveListener {
        fun onItemMove()
    }

    var itemMoveListener: OnItemMoveListener? = null
}