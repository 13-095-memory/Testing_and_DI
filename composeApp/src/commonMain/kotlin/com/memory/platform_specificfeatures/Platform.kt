package com.memory.platform_specificfeatures

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform