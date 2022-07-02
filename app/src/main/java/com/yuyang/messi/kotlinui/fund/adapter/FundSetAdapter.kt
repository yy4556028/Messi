package com.yuyang.messi.kotlinui.fund.adapter

import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.yuyang.messi.R
import com.yuyang.messi.kotlinui.fund.FundSetBean
import com.yuyang.messi.recycler.DragItemTouchCallBack
import java.util.*

class FundSetAdapter(data: List<FundSetBean>?) :
        BaseQuickAdapter<FundSetBean, BaseViewHolder>(R.layout.activity_fund_set_item, data), DragItemTouchCallBack.ItemTouchHelperAdapter {

    override fun convert(helper: BaseViewHolder, fundSetBean: FundSetBean) {
        helper.setText(R.id.tvFundName, fundSetBean.fundSetName)
                .setText(R.id.tvInvestMoney, String.format("投资金额：%s", fundSetBean.investMoney))
                .addOnClickListener(R.id.tvInvestMoney, R.id.ivDelete)

        val switch = helper.getView<SwitchMaterial>(R.id.switchMaterial)
        switch.setOnCheckedChangeListener(null)
        switch.isChecked = fundSetBean.isSelect
        if (fundSetBean.isSelect) {
            helper.setTextColor(R.id.tvFundName, ContextCompat.getColor(mContext, R.color.textPrimary))
                    .setTextColor(R.id.tvInvestMoney, ContextCompat.getColor(mContext, R.color.textPrimary))
        } else {
            helper.setTextColor(R.id.tvFundName, ContextCompat.getColor(mContext, R.color.textSecondary))
                    .setTextColor(R.id.tvInvestMoney, ContextCompat.getColor(mContext, R.color.textSecondary))
        }
        switch.setOnCheckedChangeListener { _, isChecked ->
            fundSetBean.isSelect = isChecked
            if (fundSetBean.isSelect) {
                helper.setTextColor(R.id.tvFundName, ContextCompat.getColor(mContext, R.color.textPrimary))
                        .setTextColor(R.id.tvInvestMoney, ContextCompat.getColor(mContext, R.color.textPrimary))
            } else {
                helper.setTextColor(R.id.tvFundName, ContextCompat.getColor(mContext, R.color.textSecondary))
                        .setTextColor(R.id.tvInvestMoney, ContextCompat.getColor(mContext, R.color.textSecondary))
            }

            itemListener?.onItemCheck(helper.adapterPosition)
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(data, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        itemListener?.onItemMove()
    }

    fun updateItem(fundSetBean: FundSetBean) {
        var index = -1
        for (i in data.indices) {
            if (data[i].createTime == fundSetBean.createTime) {
                index = i
                break
            }
        }

        if (index == -1) {
            addData(fundSetBean)
        } else {
            setData(index, fundSetBean)
        }
    }

    interface OnItemListener {
        fun onItemCheck(position: Int)
        fun onItemMove()
    }

    var itemListener: OnItemListener? = null
}