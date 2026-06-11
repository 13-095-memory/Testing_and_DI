package com.memory.platform_specificfeatures.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memory.platform_specificfeatures.data.repository.SettingsRepository
import com.memory.platform_specificfeatures.platform.BatteryInfo
import com.memory.platform_specificfeatures.platform.DeviceInfo
import com.memory.platform_specificfeatures.platform.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val deviceInfo: DeviceInfo,
    private val networkMonitor: NetworkMonitor,
    private val batteryInfo: BatteryInfo
) : ViewModel() {

    private val _deviceName   = MutableStateFlow("")
    val deviceName: StateFlow<String> = _deviceName.asStateFlow()

    private val _manufacturer = MutableStateFlow("")
    val manufacturer: StateFlow<String> = _manufacturer.asStateFlow()

    private val _osVersion    = MutableStateFlow("")
    val osVersion: StateFlow<String> = _osVersion.asStateFlow()

    private val _sdkVersion   = MutableStateFlow("")
    val sdkVersion: StateFlow<String> = _sdkVersion.asStateFlow()

    private val _isConnected  = MutableStateFlow(true)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _batteryLevel = MutableStateFlow(0)
    val batteryLevel: StateFlow<Int> = _batteryLevel.asStateFlow()

    private val _isCharging   = MutableStateFlow(false)
    val isCharging: StateFlow<Boolean> = _isCharging.asStateFlow()

    private val _isDarkTheme  = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _showDate     = MutableStateFlow(true)
    val showDate: StateFlow<Boolean> = _showDate.asStateFlow()

    private val _autoSave     = MutableStateFlow(true)
    val autoSave: StateFlow<Boolean> = _autoSave.asStateFlow()

    init {
        loadDeviceInfo()
        loadNetworkStatus()
        loadBatteryInfo()
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.isDarkTheme.collect { _isDarkTheme.value = it }
        }
        viewModelScope.launch {
            settingsRepository.showDate.collect { _showDate.value = it }
        }
        viewModelScope.launch {
            settingsRepository.autoSave.collect { _autoSave.value = it }
        }
    }

    private fun loadDeviceInfo() {
        _deviceName.value   = deviceInfo.getDeviceName()
        _manufacturer.value = deviceInfo.getManufacturer()
        _osVersion.value    = deviceInfo.getOsVersion()
        _sdkVersion.value   = deviceInfo.getSdkVersion()
    }

    private fun loadNetworkStatus() {
        _isConnected.value = networkMonitor.isConnected()
    }

    private fun loadBatteryInfo() {
        _batteryLevel.value = batteryInfo.getBatteryLevel()
        _isCharging.value   = batteryInfo.isCharging()
    }

    fun toggleTheme() {
        val new = !_isDarkTheme.value
        _isDarkTheme.value = new
        viewModelScope.launch { settingsRepository.setDarkTheme(new) }
    }

    fun toggleShowDate(value: Boolean) {
        _showDate.value = value
        viewModelScope.launch { settingsRepository.setShowDate(value) }
    }

    fun toggleAutoSave(value: Boolean) {
        _autoSave.value = value
        viewModelScope.launch { settingsRepository.updateAutoSave(value) }
    }
}