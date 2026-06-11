package com.memory.platform_specificfeatures.data.repository

import com.memory.platform_specificfeatures.data.model.Note
import com.memory.platform_specificfeatures.data.model.NoteColor
import com.memory.platform_specificfeatures.data.model.NoteType
import com.memory.platform_specificfeatures.data.model.TodoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NoteRepository {

    private val _notes = MutableStateFlow<List<Note>>(
        listOf(
            Note(id = 1, title = "Meeting notes",
                content = "Sprint goals and DI migration plan.",
                category = "Work", color = NoteColor.BLUE),
            Note(id = 2, title = "Ideas list",
                content = "Feature ideas for next release. Dark mode, widgets.",
                category = "Personal", color = NoteColor.PINK),
            Note(id = 3, title = "Grocery list",
                content = "Milk, eggs, coffee beans, bread, butter.",
                category = "Personal", color = NoteColor.GREEN),
            Note(id = 4, title = "Koin DI notes",
                content = "single{}, factory{}, viewModelOf()",
                category = "Study", color = NoteColor.YELLOW),
            Note(id = 5, title = "Trip June",
                content = "Destinations, budget ideas, packing list.",
                category = "Personal", color = NoteColor.PURPLE),
            Note(id = 6, title = "Books to read",
                content = "Atomic Habits, Clean Architecture.",
                category = "Study", color = NoteColor.WHITE),
            // contoh todo note
            Note(
                id = 7,
                title = "Weekly Tasks",
                content = "",
                category = "Personal",
                color = NoteColor.PINK,
                noteType = NoteType.TODO,
                todoItems = listOf(
                    TodoItem(id = 1, text = "Selesaikan tugas KMP", isDone = true),
                    TodoItem(id = 2, text = "Push ke GitHub branch week-8"),
                    TodoItem(id = 3, text = "Buat video demo 45 detik"),
                )
            )
        )
    )

    fun getAllNotes(): Flow<List<Note>> = _notes.asStateFlow()

    fun getNoteById(id: Long): Note? = _notes.value.find { it.id == id }

    suspend fun insertNote(note: Note) {
        val list   = _notes.value.toMutableList()
        val newNote = note.copy(id = (list.maxOfOrNull { it.id } ?: 0) + 1)
        list.add(0, newNote)
        _notes.value = list
    }

    suspend fun deleteNote(note: Note) {
        _notes.value = _notes.value.filter { it.id != note.id }
    }

    suspend fun deleteNoteById(id: Long) {
        _notes.value = _notes.value.filter { it.id != id }
    }

    suspend fun updateNote(note: Note) {
        _notes.value = _notes.value.map { if (it.id == note.id) note else it }
    }

    // toggle satu todo item
    suspend fun toggleTodoItem(noteId: Long, itemId: Long) {
        _notes.value = _notes.value.map { note ->
            if (note.id != noteId) note
            else note.copy(
                todoItems = note.todoItems.map { item ->
                    if (item.id == itemId) item.copy(isDone = !item.isDone) else item
                }
            )
        }
    }
}