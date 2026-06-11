package com.memory.platform_specificfeatures

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.memory.platform_specificfeatures.presentation.SettingsViewModel
import com.memory.platform_specificfeatures.ui.screens.ChatScreen
import com.memory.platform_specificfeatures.ui.screens.CreateNoteScreen
import com.memory.platform_specificfeatures.ui.screens.HomeScreen
import com.memory.platform_specificfeatures.ui.screens.ReadNoteScreen
import com.memory.platform_specificfeatures.ui.screens.SettingsScreen
import org.koin.compose.viewmodel.koinViewModel

private val PinkPrimary    = Color(0xFFE91E8C)
private val PinkLight      = Color(0xFFF48FB1)
private val PinkDark       = Color(0xFFC2185B)
private val RoseSurface    = Color(0xFFFFF0F5)
private val RoseBackground = Color(0xFFFCE4EC)
private val DeepPlum       = Color(0xFF3D0026)

private val LightColors = lightColors(
    primary        = PinkPrimary,
    primaryVariant = PinkDark,
    secondary      = Color(0xFFFF6EC7),
    background     = RoseBackground,
    surface        = RoseSurface,
    onPrimary      = Color.White,
    onSecondary    = Color.White,
    onBackground   = DeepPlum,
    onSurface      = DeepPlum,
)

private val DarkColors = darkColors(
    primary        = PinkLight,
    primaryVariant = PinkPrimary,
    secondary      = Color(0xFFFF80AB),
    background     = Color(0xFF1C0010),
    surface        = Color(0xFF2E0019),
    onPrimary      = Color(0xFF1C0010),
    onSecondary    = Color(0xFF1C0010),
    onBackground   = Color(0xFFFFD6E7),
    onSurface      = Color(0xFFFFD6E7),
)

private val AppTypography = androidx.compose.material.Typography(
    h5 = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, letterSpacing = (-0.5).sp),
    h6 = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp, letterSpacing = 0.sp),
    subtitle1 = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp),
    body1 = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    body2 = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 17.sp),
    caption = TextStyle(fontWeight = FontWeight.Normal, fontSize = 11.sp, letterSpacing = 0.2.sp),
    button = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 13.sp, letterSpacing = 0.3.sp)
)

@Composable
fun App() {
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

    MaterialTheme(
        colors     = if (isDarkTheme) DarkColors else LightColors,
        typography = AppTypography
    ) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen(
                    onNavigateToCreate   = { navController.navigate("create") },
                    onNavigateToSettings = { navController.navigate("settings") },
                    onNavigateToRead     = { noteId -> navController.navigate("read/$noteId") },
                    onNavigateToChat     = { navController.navigate("chat") }
                )
            }
            composable("create") {
                CreateNoteScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable("settings") {
                SettingsScreen()
            }
            composable("read/{noteId}") { back ->
                val noteId = back.arguments?.getString("noteId")?.toLongOrNull() ?: 0L
                ReadNoteScreen(noteId = noteId, onNavigateBack = { navController.popBackStack() })
            }
            composable("chat") {
                ChatScreen(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}