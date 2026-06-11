package com.memory.platform_specificfeatures.config

actual object ApiConfig {
    actual val geminiApiKey: String = System.getenv("GEMINI_API_KEY") ?: ""
    actual val groqApiKey: String = System.getenv("GROQ_API_KEY") ?: ""
}