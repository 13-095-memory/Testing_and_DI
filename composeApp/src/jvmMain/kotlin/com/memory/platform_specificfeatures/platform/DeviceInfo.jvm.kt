package com.memory.platform_specificfeatures.platform

actual class DeviceInfo actual constructor() {
    actual fun getDeviceName(): String = System.getProperty("os.name") ?: "Desktop"
    actual fun getManufacturer(): String = System.getProperty("os.arch") ?: "Unknown"
    actual fun getOsVersion(): String = System.getProperty("os.version") ?: "Unknown"
    actual fun getSdkVersion(): String = System.getProperty("java.version") ?: "Unknown"
    actual fun isTablet(): Boolean = false
}