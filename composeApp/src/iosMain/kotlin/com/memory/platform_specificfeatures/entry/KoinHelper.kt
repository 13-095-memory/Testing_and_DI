package com.memory.platform_specificfeatures.entry

import com.memory.platform_specificfeatures.di.appModule
import com.memory.platform_specificfeatures.di.commonModule
import com.memory.platform_specificfeatures.di.platformModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            commonModule,
            appModule,
            platformModule
        )
    }
}