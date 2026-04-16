package com.dummy.banking.repository

import android.content.Context
import com.dummy.banking.model.Transaction
import com.dummy.banking.R
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moshi: Moshi
) {
    suspend fun getTransactions(): List<Transaction> {
        delay(1000) // Simulate delay
        val jsonString = context.resources.openRawResource(R.raw.transactions)
            .bufferedReader().use { it.readText() }
        val listType = Types.newParameterizedType(List::class.java, Transaction::class.java)
        val adapter = moshi.adapter<List<Transaction>>(listType)
        return adapter.fromJson(jsonString) ?: emptyList()
    }

    suspend fun getTransactionsPaginated(page: Int, pageSize: Int): List<Transaction> {
        delay(1000) // Simulate network delay
        val allTransactions = getTransactions()
        val fromIndex = page * pageSize
        if (fromIndex >= allTransactions.size) return emptyList()
        val toIndex = minOf(fromIndex + pageSize, allTransactions.size)
        return allTransactions.subList(fromIndex, toIndex)
    }

    suspend fun transfer(recipient: String, amount: Long): Result<Boolean> {
        delay(2000)
        val success = (1..10).random() > 3
        return if (success) {
            Result.success(true)
        } else {
            Result.failure(Exception("Transfer failed. Please try again later."))
        }
    }
}
