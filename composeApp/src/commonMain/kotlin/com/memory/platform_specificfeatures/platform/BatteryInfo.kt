package com.memory.platform_specificfeatures.platform

expect class BatteryInfo() {
    fun getBatteryLevel(): Int  // 0-100
    fun isCharging(): Boolean
}