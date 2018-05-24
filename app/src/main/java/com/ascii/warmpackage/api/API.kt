package com.ascii.warmpackage.api

interface API<ResultType> {
    fun getUrl(): String
    fun parseResult(data: String): ResultType
    fun success(listener: APISuccessListener<ResultType>): API<ResultType>
    fun fail(listener: APIFailListener): API<ResultType>

    interface APISuccessListener<ResultType> {
        fun onSuccess(result: ResultType)
    }

    interface APIFailListener {
        fun onFail()
    }
}