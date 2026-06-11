package com.memory.platform_specificfeatures.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memory.platform_specificfeatures.presentation.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen() {
    val viewModel: SettingsViewModel = koinViewModel()

    val deviceName by viewModel.deviceName.collectAsState()
    val manufacturer by viewModel.manufacturer.collectAsState()
    val osVersion by viewModel.osVersion.collectAsState()
    val sdkVersion by viewModel.sdkVersion.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val batteryLevel by viewModel.batteryLevel.collectAsState()
    val isCharging by viewModel.isCharging.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val showDate by viewModel.showDate.collectAsState()
    val autoSave by viewModel.autoSave.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .statusBarsPadding() // ← fix posisi terlalu tinggi
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        Text(
            "Settings",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onBackground
        )
        Text(
            "Preferences & device info",
            fontSize = 13.sp,
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f)
        )

        Spacer(Modifier.height(24.dp))

        // APPEARANCE
        SettingsSectionTitle("APPEARANCE")
        SettingsCard {
            // Dark Theme Toggle - beneran berfungsi!
            SettingsRowSwitch(
                label = "Dark Theme",
                checked = isDarkTheme,
                onToggle = { viewModel.toggleTheme() }
            )
            Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f))
            SettingsRowText("Default font", "Sans-serif")
            Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f))
            SettingsRowText("Font size", "Medium")
        }

        Spacer(Modifier.height(16.dp))

        // NOTES
        SettingsSectionTitle("NOTES")
        SettingsCard {
            SettingsRowText("Sort by", "Date modified")
            Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f))
            // Show Date Toggle - sekarang berfungsi tampilkan date di NoteCard!
            SettingsRowSwitch(
                label = "Show date",
                checked = showDate,
                onToggle = { viewModel.toggleShowDate(it) }
            )
            Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f))
            SettingsRowSwitch(
                label = "Auto-save",
                checked = autoSave,
                onToggle = { viewModel.toggleAutoSave(it) }
            )
        }

        Spacer(Modifier.height(16.dp))

        // NETWORK
        SettingsSectionTitle("NETWORK")
        SettingsCard {
            SettingsRowBadge(
                label = "Status",
                badge = if (isConnected) "Connected" else "Offline",
                badgeColor = if (isConnected) Color(0xFF4CAF50) else Color(0xFFF44336)
            )
            Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f))
            SettingsRowText(
                "Battery",
                "$batteryLevel% ${if (isCharging) "⚡ Charging" else ""}"
            )
        }

        Spacer(Modifier.height(16.dp))

        // DEVICE INFO
        SettingsSectionTitle("DEVICE INFO")
        SettingsCard {
            SettingsRowText("Manufacturer", manufacturer)
            Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f))
            SettingsRowText("Model", deviceName)
            Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f))
            SettingsRowText("OS Version", osVersion)
            Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f))
            SettingsRowText("SDK", sdkVersion)
        }

        Spacer(Modifier.height(16.dp))

        // APP INFO
        SettingsSectionTitle("APP INFO")
        SettingsCard {
            SettingsRowBadge("Version", "1.0.0", MaterialTheme.colors.secondary)
            Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f))
            SettingsRowBadge("DI Framework", "Koin", MaterialTheme.colors.primary)
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colors.secondary,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            content()
        }
    }
}

@Composable
fun SettingsRowText(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontSize = 14.sp,
            color = MaterialTheme.colors.onSurface
        )
        Text(
            value,
            fontSize = 14.sp,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun SettingsRowSwitch(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontSize = 14.sp,
            color = MaterialTheme.colors.onSurface
        )
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colors.primary,
                checkedTrackColor = MaterialTheme.colors.primary.copy(alpha = 0.5f),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
fun SettingsRowBadge(label: String, badge: String, badgeColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontSize = 14.sp,
            color = MaterialTheme.colors.onSurface
        )
        Box(
            modifier = Modifier
                .background(badgeColor, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                badge,
                fontSize = 12.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}