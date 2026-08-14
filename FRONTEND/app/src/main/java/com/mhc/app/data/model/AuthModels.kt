package com.mhc.app.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    @SerializedName("user_id") val userId: Int?,
    val name: String?
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class ApiResponse(
    val message: String
)

data class UserProfile(
    val name: String,
    val email: String
)
