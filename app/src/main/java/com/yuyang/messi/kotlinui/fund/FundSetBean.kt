package com.yuyang.messi.kotlinui.fund

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class FundSetBean(var createTime: Long,//创建时间，以此key区分是否同一bean
                  var fundSetName: String,//基金组合名
                  var investMoney: Int,//投入资金
                  var isSelect: Boolean,
                  var fundSet: MutableList<FundBean>
) : Parcelable {

    @Parcelize
    class FundBean(var fundName: String,//基金名
                   var fundCode: String,//基金编码
                   var weight: Float//基金在组合中占比
    ) : Parcelable
}