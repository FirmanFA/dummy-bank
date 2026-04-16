package com.dummy.banking.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class TransactionStatus {
    @Json(name = "Success") Success,
    @Json(name = "Failed") Failed
}

@JsonClass(generateAdapter = true)
data class Transaction(
    val id: String,
    val date: String,
    val amount: Long,
    val status: TransactionStatus,
    val recipient: String
)
