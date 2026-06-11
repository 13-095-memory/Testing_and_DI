package com.memory.platform_specificfeatures.di

import org.koin.dsl.module

val platformModule = module {
    // Android-specific dependencies sudah di-handle
    // lewat KoinComponent inject di actual classes
}