package com.xjcmpt.mylibrary

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.xjcmpt.mylibrary.utils.MyLogUtils

class MyPaySDK {
    companion object {

        var billingClient: BillingClient? = null

        interface OnProductDetailsFaileListeners {
            fun onBuyProductFaile(code: Int?, msg: String?)
        }

        interface OnQueryProductDetails {
            fun onQueryProductDetailsFaile(code: Int, msg: String)
        }

        interface SdkConfiguration {
            fun configurationState()
        }

        var onProductDetailsFaileListeners: OnProductDetailsFaileListeners? = null

        public fun initializeSdk(
            context: Context,
            queryProductDetailsListeners: OnProductDetailsFaileListeners
        ) {
            billingClient = BillingClient.newBuilder(context)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()  //支持待处理的交易。
                .build()

            billingClient?.startConnection(billingClientStateListener)
            onProductDetailsFaileListeners = queryProductDetailsListeners
        }

        var isLinkGoogle = false


        val billingClientStateListener = object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
//强烈建议您实现自己的连接重试逻辑并替换 onBillingServiceDisconnected() 方法。请确保在执行任何方法时都与 BillingClient 保持连接。
                MyLogUtils.e("连接到GooglePay失败，请重试");
                billingClient?.startConnection(this);
                isLinkGoogle = false

            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {

                val code = billingResult.responseCode
                if (code != BillingClient.BillingResponseCode.OK) {
                    val msg = billingResult.getDebugMessage();
                    MyLogUtils.e("连接到GooglePay失败    code = " + code + "    msg = " + msg);
                    isLinkGoogle = false

                    return;
                }
                isLinkGoogle = true

                MyLogUtils.e("连接到GooglePay成功");
                checkGoodsForSale()
            }
        }

        fun checkGoodsForSale() {
            querySUBS()
            queryINAPP()
        }

        /**购买后的回调
         *Google Play 会调用 onPurchasesUpdated()，以将购买操作的结果传送给监听器。
         *在初始化客户端时使用 setListener() 方法指定监听器。
         *收到购买交易的通知
         */
        val purchasesUpdatedListener = object : PurchasesUpdatedListener {
            override fun onPurchasesUpdated(
                billingResult: BillingResult,
                purchases: MutableList<Purchase>?
            ) {
                val code = billingResult.responseCode
                val debugMessage = billingResult.debugMessage
                MyLogUtils.e(
                    "支付 ：code = " + code + "    msg = " + debugMessage + ",strProductType=" + strProductType
                );

                if (code == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        MyLogUtils.e("支付成功 Token=" + purchase.purchaseToken);

                        //通过服务器验证,然后消耗商品 or 直接消耗商品

                        if (strProductType.equals(BillingClient.ProductType.SUBS)) {
                            handlerPurchaseSUBS(purchase)  //非消耗型商品,订阅的商品
                        } else {
                            handlePurchaseINAPP(purchase)  //消耗型商品
                        }
                    }
                } else {
                    when (code) {
                        BillingClient.BillingResponseCode.SERVICE_TIMEOUT ->
                            MyLogUtils.e("服务连接超时")

                        BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED ->
                            MyLogUtils.e("FEATURE_NOT_SUPPORTED")

                        BillingClient.BillingResponseCode.SERVICE_DISCONNECTED ->
                            MyLogUtils.e("服务未连接")

                        BillingClient.BillingResponseCode.USER_CANCELED -> {
                            MyLogUtils.e("支付取消")
                            onFail(3);
                        }
                        BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE ->
                            MyLogUtils.e("服务不可用")

                        BillingClient.BillingResponseCode.BILLING_UNAVAILABLE ->
                            MyLogUtils.e("购买不可用")

                        BillingClient.BillingResponseCode.ITEM_UNAVAILABLE ->
                            MyLogUtils.e("商品不存在")

                        BillingClient.BillingResponseCode.DEVELOPER_ERROR ->
                            MyLogUtils.e("提供给 API 的无效参数")

                        BillingClient.BillingResponseCode.ERROR ->
                            MyLogUtils.e("错误")

                        BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED ->
                            MyLogUtils.e("未消耗掉")

                        BillingClient.BillingResponseCode.ITEM_NOT_OWNED ->
                            MyLogUtils.e("不可购买")
                        else ->
                            MyLogUtils.e("未知")

                    }

                }

            }
        }


        var strProductType = BillingClient.ProductType.INAPP

        //提取购买交易 确保所有购买交易都得到成功处理
        // queryPurchasesAsync() 仅返回有效订阅和非消耗型一次性购买交易。
        public fun querySUBS() {
            MyLogUtils.d("querySUBS_isLinkGoogle=" + isLinkGoogle)

            if (!isLinkGoogle) {
                return
            }
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
            val listener = object : PurchasesResponseListener {
                override fun onQueryPurchasesResponse(
                    p0: BillingResult,
                    p1: MutableList<Purchase>
                ) {
                    for (i in p1.indices) {
                        val purchase = p1.get(i)
                        if (purchase.isAcknowledged) {
                            handlePurchaseINAPP(purchase)
                        }
                    }
                }
            }
            val purchasesResult = billingClient?.queryPurchasesAsync(params.build(), listener)
        }

        public fun queryINAPP() {
            MyLogUtils.d("queryINAPP_isLinkGoogle=" + isLinkGoogle)
            if (!isLinkGoogle) {
                return
            }
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
            val listener = object : PurchasesResponseListener {
                override fun onQueryPurchasesResponse(
                    p0: BillingResult,
                    p1: MutableList<Purchase>
                ) {
                    for (i in p1.indices) {
                        val purchase = p1.get(i)
                        MyLogUtils.d("queryINAPP=" + purchase.purchaseState)
                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                            handlePurchaseINAPP(purchase)
                        }
                    }
                }
            }
            val purchasesResult = billingClient?.queryPurchasesAsync(params.build(), listener)
        }


        //5.0 查询商品
        /*ProductType.INAPP 一次性商品
         *ProductType.SUBS 针对订阅
        */
        public fun queryProductDetails(
            id: String,
            type: String,
            context: Activity,
            listener: OnQueryProductDetails
        ) {
            val build = QueryProductDetailsParams.Product.newBuilder()
                .setProductId(id)  //default id
                .setProductType(type)
                .build()

            var mutableList = mutableListOf<QueryProductDetailsParams.Product>()
            mutableList.add(build)

            val queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                    .setProductList(mutableList)
                    .build()

            billingClient?.queryProductDetailsAsync(queryProductDetailsParams,
                object : ProductDetailsResponseListener {
                    override fun onProductDetailsResponse(
                        p0: BillingResult,
                        mutableList: MutableList<ProductDetails>
                    ) {
                        var code = p0.getResponseCode();
                        if (code != BillingClient.BillingResponseCode.OK || mutableList == null || mutableList.isEmpty()) {
                            val msg = p0.getDebugMessage();
                            MyLogUtils.e("查询商品失败    code = " + code + "    msg = " + msg);
                            onFail(1);
                            listener?.onQueryProductDetailsFaile(code, msg)
                            return;
                        }
                        MyLogUtils.e("查询商品成功");

                        strProductType = mutableList.get(0).productType

                        buyIt(mutableList.get(0), context);
                    }
                })

        }

        //购买
//launchBillingFlow() 方法会返回 BillingClient.BillingResponseCode 中列出的几个响应代码之一
        /**
         * 如果您的应用可能会面向欧盟用户分发，请使用 setIsOfferPersonalized() 方法向用户披露您的商品价格已通过自动化决策进行了个性化设置。
         * */
        private fun buyIt(productDetails: ProductDetails, context: Activity) {

            val build = BillingFlowParams.ProductDetailsParams.newBuilder()

                .setProductDetails(productDetails)
                //  .setOfferToken(selectedOfferToken)
                .build()

            val productDetailsParamsList = listOf(build)

            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

// Launch the billing flow
            val billingResult = billingClient?.launchBillingFlow(context, billingFlowParams)
            val responseCode = billingResult?.responseCode
            val debugMessage = billingResult?.debugMessage
            if (responseCode == BillingClient.BillingResponseCode.OK) {
                //购买商品成功 回调 PurchasesUpdatedListener
                MyLogUtils.e(
                    "购买商品成功  productId= " + productDetails.productId + ",productType=" + productDetails.productType
                            + "\nname=" + productDetails.name + ",description=" + productDetails.description
                )

            } else {
                //购买商品失败
                MyLogUtils.e(
                    "购买商品失败    code = " + responseCode + "    msg = " + debugMessage
                )
                onFail(2)
                onProductDetailsFaileListeners?.onBuyProductFaile(responseCode, debugMessage)
            }
        }

        /*处理链接Google play失败逻辑
    *处理查询商品失败逻辑
    */
        public fun onFail(type: Int) {
            MyLogUtils.e("onFail    type = " + type)
        }

        /** 检查购买交易的状态是否为 PURCHASED,购买交易处在 PENDING 状态时，请勿确认该交易
         * 授予权利并确认购买交易的流程取决于购买的是非消耗型商品、消耗型商品，还是订阅。
         *
         * 需要重复购买一个商品 请调用consumeAsync()方法
         * */
        fun handlePurchaseINAPP(purchase: Purchase) {
            val purchaseState = purchase.purchaseState
            if (purchaseState == Purchase.PurchaseState.PURCHASED) {
                val consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(purchase.getPurchaseToken())
                    .build()

                billingClient?.consumeAsync(consumeParams, object : ConsumeResponseListener {
                    override fun onConsumeResponse(p0: BillingResult, p1: String) {
                        if (p0.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            //支付成功,消耗商品
                            MyLogUtils.d("支付成功 消耗商品 INAPP")

                        }
                    }
                })
            } else if (purchaseState == Purchase.PurchaseState.PENDING) {

            }
        }

        /**非消耗型商品的购买交易，请使用 Google Play 结算库中的 BillingClient.acknowledgePurchase()
         * 订阅的处理方式与非消耗型商品类似
         *
         * 购买交易的状态为 PENDING，则您应按照处理待处理的交易中的说明处理购买交易
         *   BillingClient.acknowledgePurchase() 或 Google Play Developer API 中的 Purchases.Subscriptions.Acknowledge 确认订阅
         * */
        fun handlerPurchaseSUBS(purchase: Purchase) {
            val purchaseState = purchase.purchaseState
            if (purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                    billingClient?.acknowledgePurchase(acknowledgePurchaseParams.build(),
                        object : AcknowledgePurchaseResponseListener {
                            override fun onAcknowledgePurchaseResponse(p0: BillingResult) {
                                if (p0.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                    //支付成功,消耗商品
                                    MyLogUtils.d("支付成功 消耗商品 SUBS")
                                }
                            }
                        })
                }
            } else if (purchaseState == Purchase.PurchaseState.PENDING) {

            }

        }

        //提取交易记录
        public fun queryHistory() {
            val params = QueryPurchaseHistoryParams.newBuilder()
                .setProductType(strProductType)
            val listener = object : PurchaseHistoryResponseListener {
                override fun onPurchaseHistoryResponse(
                    p0: BillingResult,
                    mutableList: MutableList<PurchaseHistoryRecord>?
                ) {

                }
            }

            val purchaseHistoryResult =
                billingClient?.queryPurchaseHistoryAsync(params.build(), listener)

        }

    }

}