package com.ascii.warmpackage.api

import com.android.volley.*
import com.google.gson.Gson

class InvoiceAPI(queue: RequestQueue) : APIBase<InvoiceAPI, InvoiceResult>(queue) {

    // 如果需要寫第二種建構式就是醬寫
    // 預設建構式以外的建構式都一定要傳入預設建構式所需的參數
    // 以此為例就是 queue:RequestQueue
    // 如果拿掉 queue:RequestQueue 就會無法編譯
    constructor(queue:RequestQueue,
                successListener: API.APISuccessListener<InvoiceResult>,
                failListener: API.APIFailListener): this(queue) {
        this.successListener = successListener
        this.failListener = failListener
    }

    // 省略 { return "..." } function body 的寫法
    override fun getUrl(): String = "https://asciihuang.github.io/invoice.json"

    override fun parseResult(data: String): InvoiceResult {
        var receiptResult = Gson().fromJson(data, InvoiceResult::class.java)
        return receiptResult
    }
}