package com.memory.platform_specificfeatures.di

import com.memory.platform_specificfeatures.platform.BatteryInfo
import com.memory.platform_specificfeatures.platform.DeviceInfo
import com.memory.platform_specificfeatures.platform.NetworkMonitor
import org.koin.dsl.module

val appModule = module {
    // Platform services only
    single { DeviceInfo() }
    single { NetworkMonitor() }
    single { BatteryInfo() }
}