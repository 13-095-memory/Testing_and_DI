package com.memory.platform_specificfeatures.platform

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

actual class NetworkMonitor actual constructor() {

    actual fun isConnected(): Boolean = true // stub - iOS pakai NWPathMonitor native

    actual fun observeConnectivity(): Flow<Boolean> {
        return MutableStateFlow(true) // stub ok untuk tugas
    }
}