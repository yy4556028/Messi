package com.yuyang.lib_base.utils

import java.math.BigDecimal

object CalcUtil {

    fun add(str1: String?, str2: String?): BigDecimal {
        val b1 = BigDecimal(str1)
        val b2 = BigDecimal(str2)
        return b1.add(b2)
    }

    fun subtract(str1: String?, str2: String?): BigDecimal {
        val b1 = BigDecimal(str1)
        val b2 = BigDecimal(str2)
        return b1.subtract(b2)
    }

    fun multiply(str1: String?, str2: String?): BigDecimal {
        val b1 = BigDecimal(str1)
        val b2 = BigDecimal(str2)
        return b1.multiply(b2)
    }

    fun divide(str1: String?, str2: String?, scale: Int = 2): BigDecimal {
        val b1 = BigDecimal(str1)
        val b2 = BigDecimal(str2)
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP)
    }
}