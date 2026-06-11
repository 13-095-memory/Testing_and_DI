package com.memory.platform_specificfeatures.data.repository

import com.memory.platform_specificfeatures.data.remote.GeminiService
import com.memory.platform_specificfeatures.data.remote.GeminiContent
import com.memory.platform_specificfeatures.data.remote.GeminiPart

class AIRepository(private val geminiService: GeminiService) {

    suspend fun sendMessage(conversationHistory: List<GeminiContent>): Result<String> {
        return geminiService.chat(conversationHistory)
    }

    fun buildUserContent(text: String) = GeminiContent(
        role = "user",
        parts = listOf(GeminiPart(text = text))
    )

    fun buildModelContent(text: String) = GeminiContent(
        role = "model",
        parts = listOf(GeminiPart(text = text))
    )
}