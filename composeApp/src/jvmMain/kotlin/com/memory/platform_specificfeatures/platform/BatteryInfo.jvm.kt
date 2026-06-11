package com.memory.platform_specificfeatures.platform

actual class BatteryInfo actual constructor() {
    actual fun getBatteryLevel(): Int = 100
    actual fun isCharging(): Boolean = true
}