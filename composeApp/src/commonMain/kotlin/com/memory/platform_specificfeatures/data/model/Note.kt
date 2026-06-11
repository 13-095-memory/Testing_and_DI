package com.memory.platform_specificfeatures.data.model

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class TodoItem(
    val id: Long = 0,
    val text: String,
    val isDone: Boolean = false
)

enum class NoteType { NOTE, TODO }

data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "Personal",
    val color: NoteColor = NoteColor.WHITE,
    val fontStyle: String = "Normal",
    val fontSize: String = "Medium",
    val noteType: NoteType = NoteType.NOTE,
    val todoItems: List<TodoItem> = emptyList(),
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds()
)