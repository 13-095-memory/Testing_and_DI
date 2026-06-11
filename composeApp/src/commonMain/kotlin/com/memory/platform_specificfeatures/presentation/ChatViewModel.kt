package com.memory.platform_specificfeatures.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memory.platform_specificfeatures.data.remote.AIError
import com.memory.platform_specificfeatures.data.remote.GeminiContent
import com.memory.platform_specificfeatures.data.repository.AIRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChatViewModel(
    private val aiRepository: AIRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val conversationHistory = mutableListOf<GeminiContent>()

    fun sendMessage(userInput: String) {
        if (userInput.isBlank() || _uiState.value.isLoading) return

        _uiState.update { state ->
            state.copy(
                messages = state.messages + ChatMessage(userInput, isUser = true),
                isLoading = true,
                error = null
            )
        }

        conversationHistory.add(aiRepository.buildUserContent(userInput))

        viewModelScope.launch {
            aiRepository.sendMessage(conversationHistory.toList())
                .onSuccess { responseText ->
                    conversationHistory.add(aiRepository.buildModelContent(responseText))
                    _uiState.update { state ->
                        state.copy(
                            messages = state.messages + ChatMessage(responseText, isUser = false),
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    if (conversationHistory.isNotEmpty()) {
                        conversationHistory.removeAt(conversationHistory.lastIndex)
                    }
                    val errorMessage = when (error) {
                        is AIError.RateLimited   -> "Terlalu banyak request. Tunggu sebentar ya! 🙏"
                        is AIError.Unauthorized  -> "API key tidak valid. Cek pengaturan API key kamu."
                        is AIError.ServerError   -> "Server Gemini sedang bermasalah. Coba lagi nanti."
                        is AIError.NetworkError  -> "Tidak ada koneksi internet. Cek jaringanmu!"
                        is AIError.EmptyResponse -> "AI tidak memberikan respons. Coba tanya yang lain."
                        else -> "Terjadi kesalahan: ${error.message}"
                    }
                    _uiState.update { state ->
                        state.copy(isLoading = false, error = errorMessage)
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearChat() {
        conversationHistory.clear()
        _uiState.update { ChatUiState() }
    }
}