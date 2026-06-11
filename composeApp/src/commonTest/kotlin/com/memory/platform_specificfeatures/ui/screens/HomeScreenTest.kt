package com.memory.platform_specificfeatures.ui.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import com.memory.platform_specificfeatures.data.model.Note
import com.memory.platform_specificfeatures.data.repository.NoteRepository
import com.memory.platform_specificfeatures.data.repository.SettingsRepository
import com.memory.platform_specificfeatures.platform.BatteryInfo
import com.memory.platform_specificfeatures.platform.DeviceInfo
import com.memory.platform_specificfeatures.platform.NetworkMonitor
import com.memory.platform_specificfeatures.presentation.NotesViewModel
import com.memory.platform_specificfeatures.presentation.SettingsViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class HomeScreenTest {

    private val noteRepository: NoteRepository = mockk(relaxed = true)
    private val networkMonitor: NetworkMonitor = mockk(relaxed = true)
    private val settingsRepository: SettingsRepository = mockk(relaxed = true)
    private val deviceInfo: DeviceInfo = mockk(relaxed = true)
    private val batteryInfo: BatteryInfo = mockk(relaxed = true)

    private val notesFlow = MutableStateFlow<List<Note>>(emptyList())
    private val networkFlow = MutableStateFlow(true)
    private val showDateFlow = MutableStateFlow(true)
    private val isDarkThemeFlow = MutableStateFlow(false)
    private val autoSaveFlow = MutableStateFlow(true)

    @BeforeTest
    fun setUp() {
        // Mock notes and network monitoring flows
        every { noteRepository.getAllNotes() } returns notesFlow
        every { networkMonitor.observeConnectivity() } returns networkFlow
        every { networkMonitor.isConnected() } returns true

        // Mock settings flows and initial values
        every { settingsRepository.showDate } returns showDateFlow
        every { settingsRepository.isDarkTheme } returns isDarkThemeFlow
        every { settingsRepository.autoSave } returns autoSaveFlow

        // Device info mocks
        every { deviceInfo.getDeviceName() } returns "Test Device"
        every { deviceInfo.getManufacturer() } returns "Google"
        every { deviceInfo.getOsVersion() } returns "Android 13"
        every { deviceInfo.getSdkVersion() } returns "33"

        // Battery info mocks
        every { batteryInfo.getBatteryLevel() } returns 100
        every { batteryInfo.isCharging() } returns false

        startKoin {
            modules(module {
                single { noteRepository }
                single { networkMonitor }
                single { settingsRepository }
                single { deviceInfo }
                single { batteryInfo }
                factory { NotesViewModel(get(), get()) }
                factory { SettingsViewModel(get(), get(), get(), get()) }
            })
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun searchField_isDisplayed_andAllowsInput() = runComposeUiTest {
        // Arrange: Prepare empty notes list
        notesFlow.value = emptyList()

        // Act: Render the HomeScreen under test
        setContent {
            HomeScreen(
                onNavigateToCreate = {},
                onNavigateToSettings = {},
                onNavigateToRead = {},
                onNavigateToChat = {}
            )
        }

        // Assert: Verify search text field works
        onNodeWithTag("search_text_field").assertIsDisplayed()
        onNodeWithTag("search_text_field").performTextInput("Meeting")
    }

    @Test
    fun categoryChips_areDisplayed_andCanBeSelected() = runComposeUiTest {
        // Arrange: Prepare empty notes list
        notesFlow.value = emptyList()

        // Act: Render the HomeScreen under test
        setContent {
            HomeScreen(
                onNavigateToCreate = {},
                onNavigateToSettings = {},
                onNavigateToRead = {},
                onNavigateToChat = {}
            )
        }

        // Assert: Verify category tab filters are interactive
        onNodeWithTag("category_chip_All").assertIsDisplayed()
        onNodeWithTag("category_chip_Work").assertIsDisplayed().performClick()
        onNodeWithTag("category_chip_Personal").assertIsDisplayed().performClick()
    }

    @Test
    fun emptyNotesView_isDisplayed_whenNotesListIsEmpty() = runComposeUiTest {
        // Arrange: Provide an empty list of notes
        notesFlow.value = emptyList()

        // Act: Render the HomeScreen
        setContent {
            HomeScreen(
                onNavigateToCreate = {},
                onNavigateToSettings = {},
                onNavigateToRead = {},
                onNavigateToChat = {}
            )
        }

        // Assert: Verify empty state placeholder is shown
        onNodeWithTag("empty_notes_view").assertIsDisplayed()
    }
}
