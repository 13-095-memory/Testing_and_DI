package com.memory.platform_specificfeatures.platform

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.memory.platform_specificfeatures.database.AppDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class DatabaseDriverFactory actual constructor() : KoinComponent {
    private val context: Context by inject()

    actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(AppDatabase.Schema, context, "notes.db")
}