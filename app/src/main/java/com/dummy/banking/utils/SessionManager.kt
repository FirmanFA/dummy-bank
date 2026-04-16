package com.dummy.banking.utils

import android.content.Context
import com.dummy.banking.model.User
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moshi: Moshi
) {
    private val prefs = context.getSharedPreferences("banking_prefs", Context.MODE_PRIVATE)
    private val adapter = moshi.adapter(User::class.java)

    fun saveUser(user: User) {
        prefs.edit().putString("user_data", adapter.toJson(user)).apply()
    }

    fun getUser(): User? {
        val json = prefs.getString("user_data", null) ?: return null
        return try {
            adapter.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    fun clearSession() {
        prefs.edit().remove("user_data").apply()
    }

    fun isLoggedIn(): Boolean {
        return getUser() != null
    }
}
