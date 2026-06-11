package com.memory.platform_specificfeatures.platform

expect class DeviceInfo() {
    fun getDeviceName(): String
    fun getManufacturer(): String
    fun getOsVersion(): String
    fun getSdkVersion(): String
    fun isTablet(): Boolean
}