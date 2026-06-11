package com.memory.platform_specificfeatures.app

import android.app.Application
import com.memory.platform_specificfeatures.di.appModule
import com.memory.platform_specificfeatures.di.commonModule
import com.memory.platform_specificfeatures.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(
                commonModule,
                appModule,
                platformModule
            )
        }
    }
}