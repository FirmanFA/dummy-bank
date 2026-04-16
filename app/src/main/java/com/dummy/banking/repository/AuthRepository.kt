package com.dummy.banking.repository

import android.content.Context
import com.dummy.banking.R
import com.dummy.banking.model.User
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moshi: Moshi
) {
    private var cachedUsers: List<User>? = null

    suspend fun login(username: String, password: String): Result<User> {
        delay(1500) // Simulate API delay
        
        val users = cachedUsers ?: run {
            val jsonString = context.resources.openRawResource(R.raw.users)
                .bufferedReader().use { it.readText() }
            val listType = Types.newParameterizedType(List::class.java, User::class.java)
            val adapter = moshi.adapter<List<User>>(listType)
            val result = adapter.fromJson(jsonString) ?: emptyList()
            cachedUsers = result
            result
        }

        val user = users.find { it.username == username && it.password == password }
        
        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid username or password"))
        }
    }
}
