package com.memory.platform_specificfeatures.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository {

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _fontSize = MutableStateFlow("Medium")
    val fontSize: StateFlow<String> = _fontSize.asStateFlow()

    private val _showDate = MutableStateFlow(true)
    val showDate: StateFlow<Boolean> = _showDate.asStateFlow()

    private val _autoSave = MutableStateFlow(true)
    val autoSave: StateFlow<Boolean> = _autoSave.asStateFlow()

    fun setDarkTheme(value: Boolean) { _isDarkTheme.value = value }
    fun updateFontSize(size: String) { _fontSize.value = size }
    fun setShowDate(value: Boolean)  { _showDate.value = value }
    fun updateAutoSave(save: Boolean) { _autoSave.value = save }
}