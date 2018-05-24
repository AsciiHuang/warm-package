package com.ascii.warmpackage.api

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest

// 將子類別的型態、結果類別的型態指定為泛型
// 並指定傳入的第一個類別只能是 APIBase 的子類
abstract class APIBase<APIType: APIBase<APIType, ResultType>, ResultType>(queue: RequestQueue): API<ResultType> {

    // 類別成員宣告時可以直接調用建構式中的參數
    private var requestQueue: RequestQueue = queue
    // 如果有要在建構式做其他事的話就是醬寫
    // 需要注意 init function 位置必須在操作到的成員變數之下，否則無法編譯
    // 可以試著把 init { ... } 整段搬到最上面試試
    // init {
    //     requestQueue = queue
    // }

    protected var successListener: API.APISuccessListener<ResultType>? = null
    protected var failListener: API.APIFailListener? = null

    override fun getUrl(): String {
        return ""
    }

    override fun success(listener: API.APISuccessListener<ResultType>): APIType {
        successListener = listener
        return this as APIType
    }

    override fun fail(listener: API.APIFailListener): APIType {
        failListener = listener
        return this as APIType
    }

    fun start() {
        var url = getUrl()
        if ("".equals(url)) {
            failListener?.onFail()
        } else {
            var stringRequest = StringRequest(Request.Method.GET, getUrl(),
                    object : Response.Listener<String> {
                        override fun onResponse(response: String?) {
                            response?.let {
                                var result: ResultType = parseResult(it)
                                successListener?.onSuccess(result)
                            }
                        }
                    },
                    object : Response.ErrorListener {
                        override fun onErrorResponse(error: VolleyError?) {
                            failListener?.onFail()
                        }
                    })
            requestQueue.add(stringRequest)
        }
    }
}