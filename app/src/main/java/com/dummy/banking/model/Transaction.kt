package com.dummy.banking.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Transaction(
    val id: String,
    val date: String,
    val amount: Long,
    val status: String,
    val recipient: String
)
