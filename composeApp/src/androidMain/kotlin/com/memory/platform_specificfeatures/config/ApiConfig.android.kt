package com.memory.platform_specificfeatures.config

import com.memory.platform_specificfeatures.BuildConfig

actual object ApiConfig {
    actual val geminiApiKey: String = BuildConfig.GEMINI_API_KEY
    actual val groqApiKey: String = BuildConfig.GROQ_API_KEY
}