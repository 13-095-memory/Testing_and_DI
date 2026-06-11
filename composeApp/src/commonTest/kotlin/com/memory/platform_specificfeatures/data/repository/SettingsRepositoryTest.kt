package com.memory.platform_specificfeatures.data.repository

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsRepositoryTest {

    @Test
    fun testDefaultSettings() {
        // Arrange
        val repository = SettingsRepository()

        // Act & Assert (AAA Pattern)
        assertTrue(repository.showDate.value)
        assertFalse(repository.isDarkTheme.value)
        assertTrue(repository.autoSave.value)
        assertEquals("Medium", repository.fontSize.value)
    }

    @Test
    fun testUpdateSettings() {
        // Arrange
        val repository = SettingsRepository()

        // Act
        repository.setDarkTheme(true)
        repository.setShowDate(false)
        repository.updateFontSize("Large")
        repository.updateAutoSave(false)

        // Assert (AAA Pattern)
        assertTrue(repository.isDarkTheme.value)
        assertFalse(repository.showDate.value)
        assertFalse(repository.autoSave.value)
        assertEquals("Large", repository.fontSize.value)
    }
}
