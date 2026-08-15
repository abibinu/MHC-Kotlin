package com.mhc.app.data.api

import com.mhc.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse>

    @GET("api/users")
    suspend fun getUserProfile(
        @Query("user_id") userId: Int
    ): Response<UserProfile>

    @POST("api/chatbot")
    suspend fun sendChatMessage(
        @Body request: ChatRequest
    ): Response<ChatResponse>

    @GET("api/chatbot/history")
    suspend fun getChatHistory(
        @Query("user_id") userId: Int
    ): Response<ChatHistoryResponse>
}
