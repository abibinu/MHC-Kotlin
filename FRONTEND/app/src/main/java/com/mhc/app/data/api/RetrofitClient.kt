package com.mhc.app.data.api

import com.mhc.app.data.session.UserSessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private var baseUrl: String = UserSessionManager.DEFAULT_SERVER_URL

    fun setBaseUrl(url: String) {
        var formatted = url.trim()
        if (!formatted.endsWith("/")) {
            formatted += "/"
        }
        if (!formatted.startsWith("http://") && !formatted.startsWith("https://")) {
            formatted = "http://$formatted"
        }
        baseUrl = formatted
        apiServiceInstance = createApiService()
    }

    fun getBaseUrl(): String = baseUrl

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private fun createApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private var apiServiceInstance: ApiService = createApiService()

    val apiService: ApiService
        get() = apiServiceInstance
}
