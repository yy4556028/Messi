package com.yuyang.lib_pay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.IntDef
import android.content.IntentFilter
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.yuyang.lib_base.utils.LogUtil

class ThirdPartyPayUtil(private val lifecycleOwner: LifecycleOwner) {

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(PayPlatform.WECHAT, PayPlatform.ALIPAY, PayPlatform.PAYPAL)
    annotation class PayPlatform {
        companion object {
            const val WECHAT = 1
            const val ALIPAY = 2
            const val PAYPAL = 3
        }
    }

    companion object {
        private const val TAG = "ThirdPartyPayUtil"
        private const val WXPAY_OK = 0
        private const val WXPAY_CANCEL = -2
        private const val ALIPAY_OK = "9000"
        private const val ALIPAY_CANCEL = "6001"
        private const val PAYPAL_OK = 2
        private const val KEY_STATUS = "key_status"
    }

    init {
        require(lifecycleOwner is ComponentActivity || lifecycleOwner is Fragment) { "Must be ComponentActivity or Fragment" }
        lifecycleOwner
            .lifecycle
            .addObserver(
                object : DefaultLifecycleObserver {
                    override fun onDestroy(owner: LifecycleOwner) {
                        unregisterChina()
                        unregisterOversea()
                        lifecycleOwner.lifecycle.removeObserver(this)
                        payResultCallback = null
                    }
                })
    }

    private var orderNo: String? = null

    /** 微信支付结果回调广播  */
    private val mWxpayReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val status = intent.getIntExtra(KEY_STATUS, -1)
            LogUtil.d(TAG, String.format("onWxpayReceive status: %d", status))
            when (status) {
                WXPAY_OK -> payResultCallback?.onPaySuccess(PayPlatform.WECHAT, orderNo)

                WXPAY_CANCEL -> payResultCallback?.onPayCancel()

                else -> payResultCallback?.onPayFail("支付失败")
            }
            unregisterChina()
        }
    }

    /** 支付宝支付结果回调广播  */
    private val mAlipayReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val status = intent.getStringExtra(KEY_STATUS)
            LogUtil.d(TAG, String.format("onAlipayReceive status: %s", status))
            when (status) {
                ALIPAY_OK -> payResultCallback?.onPaySuccess(PayPlatform.ALIPAY, orderNo)

                ALIPAY_CANCEL -> payResultCallback?.onPayCancel()

                else -> payResultCallback?.onPayFail("支付失败")
            }
            unregisterChina()
        }
    }

    /** Paypal支付结果回调广播  */
    private val mPaypalReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val status = intent.getIntExtra(KEY_STATUS, 1)
            LogUtil.d(TAG, String.format("onPaypalReceive status: %d", status))
            when (status) {
                PAYPAL_OK -> payResultCallback?.onPaySuccess(PayPlatform.PAYPAL, orderNo)

                else -> payResultCallback?.onPayFail("支付失败")

            }
            unregisterOversea()
        }
    }
    private var isRegisterChina = false
    private var isRegisterOversea = false
    private var payResultCallback: PayResultCallback? = null

    private fun requireContext(): Context? {
        return when (lifecycleOwner) {
            is ComponentActivity -> {
                lifecycleOwner
            }
            is Fragment -> {
                lifecycleOwner.requireContext()
            }
            else -> {
                null
            }
        }
    }

    private fun registerChina() {
        if (isRegisterChina) return
        val context = requireContext() ?: return
        isRegisterChina = true
        context.registerReceiver(mAlipayReceiver, IntentFilter("ACTION_ALIPAY"))
        LocalBroadcastManager.getInstance(context)
            .registerReceiver(mWxpayReceiver, IntentFilter("ACTION_WEIXIN_PAY"))
    }

    private fun unregisterChina() {
        if (!isRegisterChina) return
        val context = requireContext() ?: return
        isRegisterChina = false
        context.unregisterReceiver(mAlipayReceiver)
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mWxpayReceiver)
    }

    private fun registerOversea() {
        if (isRegisterOversea) return
        val context = requireContext() ?: return
        isRegisterOversea = true
        context.registerReceiver(mPaypalReceiver, IntentFilter("action.PAYPAL"))
    }

    private fun unregisterOversea() {
        if (!isRegisterOversea) return
        val context = requireContext() ?: return
        isRegisterOversea = false
        context.unregisterReceiver(mPaypalReceiver)
    }

    fun startPayChina(mammonUrl: String) {
        orderNo = null
        val context = requireContext() ?: return
        registerChina()
        val intent = Intent("com.mobvoi.companion.action.BBS")
        intent.putExtra("url", mammonUrl)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage(context.packageName)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    // 因 海外 不允许三方支付 paypal 服务端开发中止
    fun startPayOversea(mammonUrl: String) {
        orderNo = null
        val context = requireContext() ?: return
        registerOversea()
        val intent = Intent("com.mobvoi.companion.action.BBS")
        intent.putExtra("url", mammonUrl)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage(context.packageName)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    fun setPayResultCallback(payResultCallback: PayResultCallback?) {
        this.payResultCallback = payResultCallback
    }

    interface PayResultCallback {
        fun onPaySuccess(@PayPlatform platform: Int, orderNo: String?)
        fun onPayCancel()
        fun onPayFail(errorMsg: String?)
    }
}