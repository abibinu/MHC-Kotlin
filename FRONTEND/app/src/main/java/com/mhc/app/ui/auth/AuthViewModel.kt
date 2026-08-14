package com.mhc.app.ui.auth

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mhc.app.data.api.RetrofitClient
import com.mhc.app.data.model.LoginRequest
import com.mhc.app.data.model.RegisterRequest
import com.mhc.app.data.session.UserSessionManager
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = UserSessionManager(application)

    var isLoginTab by mutableStateOf(true)
        private set

    var nameInput by mutableStateOf("")
    var emailInput by mutableStateOf("")
    var passwordInput by mutableStateOf("")
    var confirmPasswordInput by mutableStateOf("")

    var serverUrlInput by mutableStateOf(sessionManager.getServerBaseUrl())
    var isConfigDialogOpen by mutableStateOf(false)

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)
    var showOfflineOption by mutableStateOf(false)
        private set

    init {
        RetrofitClient.setBaseUrl(sessionManager.getServerBaseUrl())
    }

    fun toggleTab(isLogin: Boolean) {
        isLoginTab = isLogin
        errorMessage = null
        successMessage = null
    }

    fun updateServerUrl(newUrl: String) {
        serverUrlInput = newUrl
        sessionManager.saveServerBaseUrl(newUrl)
        RetrofitClient.setBaseUrl(newUrl)
        isConfigDialogOpen = false
        successMessage = "Server URL updated to $newUrl"
        showOfflineOption = false
    }

    fun authenticate(onSuccess: () -> Unit) {
        errorMessage = null
        successMessage = null
        showOfflineOption = false

        val email = emailInput.trim()
        val password = passwordInput.trim()

        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Please fill in all required fields."
            return
        }

        if (isLoginTab) {
            performLogin(email, password, onSuccess)
        } else {
            val name = nameInput.trim()
            if (name.isBlank()) {
                errorMessage = "Please enter your name."
                return
            }
            if (password != confirmPasswordInput.trim()) {
                errorMessage = "Passwords do not match."
                return
            }
            performRegister(name, email, password)
        }
    }

    fun continueOffline(onSuccess: () -> Unit) {
        val userName = if (nameInput.isNotBlank()) nameInput.trim() else if (emailInput.isNotBlank()) emailInput.substringBefore("@") else "Companion User"
        val userEmail = if (emailInput.isNotBlank()) emailInput.trim() else "demo@mhc.local"
        sessionManager.saveUserSession(userId = 999, name = userName, email = userEmail)
        onSuccess()
    }

    private fun performLogin(email: String, password: String, onSuccess: () -> Unit) {
        isLoading = true
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(email, password))
                isLoading = false
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val userId = body.userId ?: -1
                    val userName = body.name ?: "User"
                    sessionManager.saveUserSession(userId, userName, email)
                    onSuccess()
                } else {
                    errorMessage = response.errorBody()?.string()?.let { parseMessage(it) }
                        ?: "Login failed. Check your credentials."
                }
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "Backend server unreachable. Make sure Express server is running, or continue in Offline Demo mode below."
                showOfflineOption = true
            }
        }
    }

    private fun performRegister(name: String, email: String, password: String) {
        isLoading = true
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.register(RegisterRequest(name, email, password))
                isLoading = false
                if (response.isSuccessful) {
                    successMessage = "Registration successful! Please log in with your credentials."
                    isLoginTab = true
                    passwordInput = ""
                    confirmPasswordInput = ""
                } else {
                    errorMessage = response.errorBody()?.string()?.let { parseMessage(it) }
                        ?: "Registration failed. Email might already exist."
                }
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "Backend server unreachable. Make sure Express server is running, or continue in Offline Demo mode below."
                showOfflineOption = true
            }
        }
    }

    private fun parseMessage(jsonString: String): String {
        return try {
            val regex = """"message"\s*:\s*"([^"]+)"""".toRegex()
            regex.find(jsonString)?.groupValues?.get(1) ?: "An error occurred."
        } catch (e: Exception) {
            "An error occurred."
        }
    }
}
