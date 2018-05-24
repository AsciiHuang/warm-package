package com.ascii.warmpackage.api

import com.google.gson.annotations.SerializedName

class InvoiceResult {
    @SerializedName("receipts")
    var receipts: List<InvoiceEntity>? = null

    class InvoiceEntity {
        @SerializedName("item")
        var item: String? = null

        @SerializedName("value")
        var value: String? = null
    }

    override fun toString(): String {
        var stringBuilder = StringBuilder()
        receipts?.let {
            for (receipt in it) {
                stringBuilder.appendln("${receipt.item}: ${receipt.value}")
            }
        }
        return stringBuilder.toString()
    }
}