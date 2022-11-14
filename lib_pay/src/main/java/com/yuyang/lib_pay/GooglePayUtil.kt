package com.yuyang.lib_pay

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.android.billingclient.api.*
import com.yuyang.lib_base.BaseApp
import com.yuyang.lib_base.MainHandler
import com.yuyang.lib_base.utils.LogUtil

class GooglePayUtil(private val lifecycleOwner: LifecycleOwner) {

    companion object {
        private const val TAG = "GooglePayUtil"
    }

    private val billingClient: BillingClient
    private lateinit var productId: String

    @BillingClient.ProductType
    private lateinit var productType: String
    private var payResultCallback: PayResultCallback? = null

    // 监听购买交易的更新
    private val mPurchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult: BillingResult, purchases: List<Purchase>? ->
            LogUtil.i(TAG, "onPurchasesUpdated")
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    if (!purchases.isNullOrEmpty())
                        handlePurchaseList(purchases, true)
                }
                BillingClient.BillingResponseCode.USER_CANCELED -> {
                    LogUtil.d(TAG, "onPurchasesUpdated() - 用户取消购买当前商品")
                    payResultCallback?.onPayCancel()
                }
                else -> {
                    LogUtil.d(
                        TAG,
                        "onPurchasesUpdated() got unknown resultCode: " + billingResult.responseCode
                    )
                    payResultCallback?.onPayFail(billingResult.debugMessage)
                }
            }
        }

    init {
        billingClient = BillingClient.newBuilder(BaseApp.getInstance())
            .setListener(mPurchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        lifecycleOwner
            .lifecycle
            .addObserver(
                object : DefaultLifecycleObserver {
                    override fun onResume(owner: LifecycleOwner) {
                        super.onResume(owner)
                        if (billingClient.isReady) {
                            refreshPurchasesAsync(BillingClient.ProductType.INAPP)
                            refreshPurchasesAsync(BillingClient.ProductType.SUBS)
                        }
                    }
                    override fun onDestroy(owner: LifecycleOwner) {
                        super.onDestroy(owner)
                        destroy()
                        lifecycleOwner.lifecycle.removeObserver(this)
                    }
                })
    }

    private fun executeServiceRequest(runnable: Runnable) {
        if (billingClient.isReady) {
            runnable.run()
        } else {
            startServiceConnection(runnable)
        }
    }

    private fun startServiceConnection(executeOnSuccess: Runnable?) {
        billingClient.startConnection(
            object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    val responseCode = billingResult.responseCode
                    val debugMessage = billingResult.debugMessage
                    LogUtil.d(TAG, "onBillingSetupFinished: $responseCode $debugMessage")
                    if (responseCode == BillingClient.BillingResponseCode.OK) {
                        executeOnSuccess?.run()
                    } else {
                        if (executeOnSuccess != null) {
                            payResultCallback?.onPayFail(debugMessage)
                        }
                    }
                }

                override fun onBillingServiceDisconnected() {}
            })
    }

    fun startPay(productId: String, @BillingClient.ProductType productType: String) {
        this.productId = productId
        this.productType = productType
        LogUtil.i(TAG, String.format("startPay productId:%s productType:%s", productId, productType))
        executeServiceRequest {
            queryProduct(productId, productType)
//            refreshPurchasesAsync(BillingClient.ProductType.INAPP)
//            refreshPurchasesAsync(BillingClient.ProductType.SUBS)
        }
    }

    private fun queryProduct(productId: String, @BillingClient.ProductType productType: String) {
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    listOf(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(productId)
                            .setProductType(productType)
                            .build()
                    )
                )
                .build()
        billingClient.queryProductDetailsAsync(
            queryProductDetailsParams
        ) { billingResult, productDetailsList ->
            val responseCode = billingResult.responseCode
            val debugMessage = billingResult.debugMessage
            if (responseCode == BillingClient.BillingResponseCode.OK) {
                for (productDetails in productDetailsList) {
                    if (productId == productDetails.productId) {
                        launchBillingFlow(productDetails)
                    }
                }
            } else {
                LogUtil.d(TAG, "onProductDetailsResponse: $responseCode $debugMessage")
                payResultCallback?.onPayFail(debugMessage)
            }
        }
    }

    /** 应用可能会跟踪不到或不知道购买交易 需要同步交易 仅返回有效订阅和非消耗型一次性购买交易  */
    private fun refreshPurchasesAsync(@BillingClient.ProductType productType: String) {
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(productType).build()
        ) { billingResult: BillingResult, list: List<Purchase> ->
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    LogUtil.i(TAG, "queryPurchasesAsync OK")
                    handlePurchaseList(list, false)
                }
                BillingClient.BillingResponseCode.USER_CANCELED -> {
                    LogUtil.i(TAG, "queryPurchasesAsync cancel")
                }
                else -> {
                    LogUtil.e(TAG, "queryPurchasesAsync error: " + billingResult.debugMessage)
                }
            }
        }
        LogUtil.d(TAG, "Refreshing purchases started.")
    }

    /** 启动购买流程  */
    private fun launchBillingFlow(productDetails: ProductDetails) {
        val productDetailsParamsBuilder = BillingFlowParams.ProductDetailsParams.newBuilder()
            // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
            .setProductDetails(productDetails)

        if (!productDetails.subscriptionOfferDetails.isNullOrEmpty()) {
            // to get an offer token, call ProductDetails.subscriptionOfferDetails()
            // for a list of offers that are available to the user
            productDetailsParamsBuilder.setOfferToken(productDetails.subscriptionOfferDetails!![0].offerToken)
        }

        val flowParams =
            BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(productDetailsParamsBuilder.build()))
                // .setSubscriptionUpdateParams()
                // .setObfuscatedAccountId() // 混淆的账户id 存mUserID
                // .setObfuscatedProfileId() // 混淆的个人资料id 存mOrderID
                // .setIsOfferPersonalized()  // 是否为个性化价格 比如针对欧盟可能会有不同价格
                .build()
        val billingResult = billingClient.launchBillingFlow(BaseApp.getInstance().currentActivity.get()!!, flowParams)
        LogUtil.i(TAG, "购买商品：$productDetails")
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            LogUtil.e(TAG, "购买商品失败   code = ${billingResult.responseCode}    msg = ${billingResult.debugMessage}")
        }
    }

    /**
     * @param newTrade true 本次交易 false 同步历史
     */
    private fun handlePurchaseList(purchaseList: List<Purchase>, newTrade: Boolean) {
        LogUtil.i(TAG, "handlePurchaseList " + purchaseList.size)
        for (purchase in purchaseList) {
            val purchaseState = purchase.purchaseState
            if (purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (productType == BillingClient.ProductType.INAPP) {
                    handleConsumePurchase(purchase, newTrade)
                } else if (productType == BillingClient.ProductType.SUBS) {
                    // 处理订阅商品
                    handleAcknowledgeProduct(purchase, newTrade)
                }
            } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                LogUtil.i(TAG, "交易还处于待办状态 不能授予用户使用权限")
            }
        }
    }

    private fun handleConsumePurchase(purchase: Purchase, newTrade: Boolean) {
        if (billingClient.isReady) {
            val consumeParams =
                ConsumeParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken) // 购买令牌
                    .build()
            billingClient.consumeAsync(consumeParams, object : ConsumeResponseListener {
                override fun onConsumeResponse(
                    billingResult: BillingResult,
                    purchaseToken: String
                ) {
                    MainHandler.getInstance().post {
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            LogUtil.d(TAG, "onConsumeResponse successful")
                            if (newTrade) {
                                payResultCallback?.onPaySuccess()
                            }
                        } else {
                            LogUtil.e(TAG, "onConsumeResponse fail: " + billingResult.debugMessage)
                            if (newTrade) {
                                payResultCallback?.onPayFail(billingResult.debugMessage)
                            }
                        }
                    }
                }
            })
        } else {
            LogUtil.i(TAG, "consumePurchase 连接已断开")
        }
    }

    private fun handleAcknowledgeProduct(purchase: Purchase, newTrade: Boolean) {
        if (billingClient.isReady) {
            if (!purchase.isAcknowledged) {
                billingClient.acknowledgePurchase(
                    AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                ) { billingResult: BillingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        LogUtil.d(TAG, "onAcknowledgePurchaseResponse successful")
                        if (newTrade) {
                            payResultCallback?.onPaySuccess()
                        }
                    } else {
                        LogUtil.e(
                            TAG, "onAcknowledgePurchaseResponse fail: " + billingResult.debugMessage
                        )
                        if (newTrade) {
                            payResultCallback?.onPayFail(billingResult.debugMessage)
                        }
                    }
                }
            } else {
                LogUtil.i(TAG, String.format("已有订阅：%s", purchase))
                if (newTrade) {
                    payResultCallback?.onPayFail("已有订阅 请勿重复订阅")
                }
            }
        } else {
            LogUtil.i(TAG, "handleAcknowledgeProduct 连接已断开")
        }
    }

    /** 提取交易记录  */
    private fun queryHistory(@BillingClient.ProductType productType: String) {
        val params = QueryPurchaseHistoryParams.newBuilder().setProductType(productType).build()
        billingClient.queryPurchaseHistoryAsync(
            params
        ) { billingResult, list ->
            // check billingResult
            // process returned purchase history list, e.g. display purchase history
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !list.isNullOrEmpty()) {
                for (purchaseHistoryRecord in list) {
                    // 服务器验证过 就走消耗流程 否则走自己服务验证流程
                }
            } else {
                //                    ToastUtil.showToast(billingResult.getDebugMessage());
            }
        }
    }

    fun destroy() {
        LogUtil.d(TAG, "Destroying the manager.")
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }

    fun setPayResultCallback(payResultCallback: PayResultCallback?) {
        this.payResultCallback = payResultCallback
    }

    interface PayResultCallback {
        fun onPaySuccess()
        fun onPayCancel()
        fun onPayFail(errorMsg: String?)
    }
}