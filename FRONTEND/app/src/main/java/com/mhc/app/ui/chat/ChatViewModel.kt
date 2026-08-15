package com.mhc.app.ui.chat

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mhc.app.data.api.RetrofitClient
import com.mhc.app.data.model.ChatMessage
import com.mhc.app.data.model.ChatRequest
import com.mhc.app.data.session.UserSessionManager
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = UserSessionManager(application)
    val userId = sessionManager.getUserId()
    val userName = sessionManager.getUserName()

    var selectedLanguage by mutableStateOf("English")
        private set

    var inputText by mutableStateOf("")

    var isSending by mutableStateOf(false)
        private set

    val messages = mutableStateListOf<ChatMessage>()

    init {
        loadInitialGreeting()
        if (userId > 0) {
            fetchChatHistory()
        }
    }

    fun setLanguage(language: String) {
        if (selectedLanguage != language) {
            selectedLanguage = language
            messages.add(
                ChatMessage(
                    text = if (language == "Malayalam") {
                        "ഭാഷ മലയാളത്തിലേക്ക് മാറ്റി. ഞാൻ നിങ്ങളുടെ വിർച്വൽ തെറാപ്പിസ്റ്റ് ആണ്. നിങ്ങളെ എങ്ങനെ സഹായിക്കാം?"
                    } else {
                        "Language switched to English. I am your virtual therapist Companion. How can I help you today?"
                    },
                    isUser = false
                )
            )
        }
    }

    private fun loadInitialGreeting() {
        if (messages.isEmpty()) {
            messages.add(
                ChatMessage(
                    text = "Hello $userName, I'm Companion, your AI virtual therapist. I am here to listen without judgment. How are you feeling today?",
                    isUser = false
                )
            )
        }
    }

    private fun fetchChatHistory() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getChatHistory(userId)
                if (response.isSuccessful && response.body()?.history != null) {
                    val historyList = response.body()!!.history!!
                    if (historyList.isNotEmpty()) {
                        messages.clear()
                        for (item in historyList) {
                            messages.add(ChatMessage(text = item.message, isUser = true))
                            messages.add(ChatMessage(text = item.response, isUser = false))
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore history load errors gracefully in offline mode
            }
        }
    }

    fun sendMessage(userText: String = inputText) {
        val trimmed = userText.trim()
        if (trimmed.isBlank() || isSending) return

        val userMessage = ChatMessage(text = trimmed, isUser = true)
        messages.add(userMessage)
        inputText = ""
        isSending = true

        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.sendChatMessage(
                    ChatRequest(
                        userId = if (userId > 0) userId else 999,
                        message = trimmed,
                        language = selectedLanguage,
                        userName = userName
                    )
                )

                isSending = false
                if (response.isSuccessful && response.body()?.reply != null) {
                    val reply = response.body()!!.reply!!
                    messages.add(ChatMessage(text = reply, isUser = false))
                } else {
                    messages.add(
                        ChatMessage(
                            text = getFallbackReply(trimmed, selectedLanguage),
                            isUser = false
                        )
                    )
                }
            } catch (e: Exception) {
                isSending = false
                messages.add(
                    ChatMessage(
                        text = getFallbackReply(trimmed, selectedLanguage),
                        isUser = false
                    )
                )
            }
        }
    }

    private fun getFallbackReply(input: String, lang: String): String {
        return if (lang == "Malayalam") {
            when {
                input.contains("സങ്കടം") || input.contains("sad") -> "സങ്കടം തോന്നുന്നത് സ്വാഭാവികമാണ്. മനസ്സ് തുറന്ന് സംസാരിക്കാൻ ഞാൻ ഇവിടെയുണ്ട്. എന്ത് സംഭവിച്ചു എന്ന് പറയൂ."
                input.contains("ദേഷ്യം") || input.contains("angry") -> "ദേഷ്യം ശമിപ്പിക്കാൻ സാവധാനം ദീർഘശ്വാസം എടുക്കൂ. എനിക്ക് താങ്കളെ കേൾക്കാൻ ആഗ്രഹമുണ്ട്."
                input.contains("പേടി") || input.contains("anxious") -> "ആകുലപ്പെടേണ്ടതില്ല, എല്ലാം ശരിയാകും. ഈ നിമിഷത്തിൽ ശ്രദ്ധ കേന്ദ്രീകരിക്കൂ."
                else -> "താങ്കൾ പറഞ്ഞത് ഞാൻ ശ്രദ്ധിച്ചു. മാനസിക പിരിമുറുക്കം കുറയ്ക്കാൻ ദീർഘശ്വാസ വ്യായാമങ്ങൾ സഹായിക്കും. ഞാൻ താങ്കൾക്ക് ഒപ്പമുണ്ട്."
            }
        } else {
            when {
                input.contains("sad", ignoreCase = true) || input.contains("depressed", ignoreCase = true) ->
                    "I hear you. It's completely valid to feel sad sometimes. Would you like to share what triggered these emotions?"
                input.contains("anxious", ignoreCase = true) || input.contains("stress", ignoreCase = true) ->
                    "Take a slow, deep breath in... and exhale gently. You are in a safe place. What is causing you stress right now?"
                input.contains("angry", ignoreCase = true) ->
                    "It's healthy to acknowledge anger. Take a brief moment to ground yourself. I'm here to support you."
                else ->
                    "Thank you for sharing that with me. Remember to take things one step at a time. I am always here to support your mental well-being."
            }
        }
    }
}
