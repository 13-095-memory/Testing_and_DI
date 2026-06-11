package com.memory.platform_specificfeatures.platform

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

actual class NetworkMonitor actual constructor() {
    actual fun isConnected(): Boolean = true
    actual fun observeConnectivity(): Flow<Boolean> = MutableStateFlow(true)
}