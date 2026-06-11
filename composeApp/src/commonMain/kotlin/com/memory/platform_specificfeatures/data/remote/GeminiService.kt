package com.memory.platform_specificfeatures.data.remote

import com.memory.platform_specificfeatures.config.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// ── Error types ───────────────────────────────────────────────
sealed class AIError : Exception() {
    data class RateLimited(val retryAfter: Int = 60) : AIError()
    data class Unauthorized(override val message: String = "API key tidak valid") : AIError()
    data class ServerError(override val message: String = "Server bermasalah") : AIError()
    data class NetworkError(override val message: String = "Tidak ada koneksi internet") : AIError()
    data class EmptyResponse(override val message: String = "AI tidak memberikan respons") : AIError()
    data class ApiError(override val message: String) : AIError()
}

// ── Groq DTOs ─────────────────────────────────────────────────
@Serializable
data class GroqRequest(
    val model: String,
    val messages: List<GroqMessage>,
    @SerialName("max_tokens") val maxTokens: Int = 1000,
    val temperature: Double = 0.7
)

@Serializable
data class GroqMessage(
    val role: String,
    val content: String
)

@Serializable
data class GroqResponse(
    val choices: List<GroqChoice>? = null,
    val error: GroqError? = null
)

@Serializable
data class GroqChoice(
    val message: GroqMessage,
    @SerialName("finish_reason") val finishReason: String? = null
)

@Serializable
data class GroqError(
    val message: String = "",
    val type: String = "",
    val code: String? = null
)

// ── GeminiContent tetap ada agar AIRepository tidak perlu diubah ──
@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String
)

@Serializable
data class GeminiPart(
    val text: String
)

// ── Service ───────────────────────────────────────────────────
class GeminiService {

    private val baseUrl = "https://api.groq.com/openai/v1"
    private val model = "llama-3.3-70b-versatile"

    private val systemPrompt = """
        Kamu adalah AI assistant untuk aplikasi Notes bernama "Memory".
        Tugasmu adalah membantu user dengan:
        - Memberikan ide dan inspirasi untuk catatan mereka
        - Membantu memperbaiki atau mengembangkan isi catatan
        - Menjawab pertanyaan umum dengan ringkas
        - Memberikan saran cara mengorganisir catatan
        
        Gunakan Bahasa Indonesia yang ramah dan santai.
        Berikan jawaban yang padat dan jelas, hindari jawaban yang terlalu panjang.
        Jika user bertanya di luar konteks notes, tetap bantu dengan sopan.
    """.trimIndent()

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(io.ktor.client.plugins.HttpTimeout) {
            requestTimeoutMillis = 60000  // 60 detik
            connectTimeoutMillis = 15000  // 15 detik
            socketTimeoutMillis = 60000   // 60 detik
        }
    }

    suspend fun chat(conversationHistory: List<GeminiContent>): Result<String> {
        return try {
            // Convert GeminiContent ke GroqMessage
            val messages = mutableListOf<GroqMessage>()

            // Menambahkan system prompt
            messages.add(GroqMessage(role = "system", content = systemPrompt))

            // Tambahkan conversation history
            conversationHistory.forEach { content ->
                val role = if (content.role == "model") "assistant" else content.role
                val text = content.parts.firstOrNull()?.text ?: ""
                messages.add(GroqMessage(role = role, content = text))
            }

            val request = GroqRequest(
                model = model,
                messages = messages,
                maxTokens = 1000,
                temperature = 0.7
            )

            val response: GroqResponse = httpClient.post("$baseUrl/chat/completions") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer ${ApiConfig.groqApiKey}")
                setBody(request)
            }.body()

            if (response.error != null) {
                return Result.failure(AIError.ApiError(response.error.message))
            }

            val text = response.choices?.firstOrNull()?.message?.content

            if (text.isNullOrBlank()) {
                Result.failure(AIError.EmptyResponse())
            } else {
                Result.success(text)
            }

        } catch (e: Exception) {
            Result.failure(AIError.NetworkError(e.message ?: "Unknown network error"))
        }
    }
}