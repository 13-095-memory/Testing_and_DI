package com.memory.platform_specificfeatures

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "PlatformSpecificFeatures",
    ) {
        App()
    }
}