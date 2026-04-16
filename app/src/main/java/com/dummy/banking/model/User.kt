package com.dummy.banking.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    val id: String,
    val name: String,
    val username: String,
    val password: String,
    val balance: Long
)
