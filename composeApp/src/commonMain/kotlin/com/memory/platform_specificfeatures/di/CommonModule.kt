package com.memory.platform_specificfeatures.di

import com.memory.platform_specificfeatures.data.remote.GeminiService
import com.memory.platform_specificfeatures.data.repository.AIRepository
import com.memory.platform_specificfeatures.data.repository.NoteRepository
import com.memory.platform_specificfeatures.data.repository.SettingsRepository
import com.memory.platform_specificfeatures.presentation.ChatViewModel
import com.memory.platform_specificfeatures.presentation.NotesViewModel
import com.memory.platform_specificfeatures.presentation.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dataModule = module {
    // Repositories
    single { NoteRepository() }
    single { SettingsRepository() }

    // AI layer
    single { GeminiService() }
    single { AIRepository(get()) }
}

val viewModelModule = module {
    // ViewModels
    viewModelOf(::NotesViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::ChatViewModel)
}

val commonModule = module {
    includes(dataModule, viewModelModule)
}