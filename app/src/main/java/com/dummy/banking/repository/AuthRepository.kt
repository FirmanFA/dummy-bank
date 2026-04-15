package com.dummy.banking.repository

import com.dummy.banking.model.User
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {
    suspend fun login(username: String, password: String): Result<User> {
        delay(1500) // Simulate API delay
        return if (username == "user" && password == "pass123") {
            Result.success(User("1", "user", 5000000L))
        } else {
            Result.failure(Exception("Invalid username or password"))
        }
    }
}
