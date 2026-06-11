package com.memory.platform_specificfeatures.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memory.platform_specificfeatures.data.model.Note
import com.memory.platform_specificfeatures.data.model.NoteColor
import com.memory.platform_specificfeatures.data.model.NoteType
import com.memory.platform_specificfeatures.data.model.TodoItem
import com.memory.platform_specificfeatures.data.repository.NoteRepository
import com.memory.platform_specificfeatures.platform.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(
    private val noteRepository: NoteRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _isConnected = MutableStateFlow(true)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    val filteredNotes: StateFlow<List<Note>> = combine(
        noteRepository.getAllNotes(),
        _searchQuery,
        _selectedCategory
    ) { notes, query, category ->
        notes.filter { note ->
            val matchesQuery = query.isEmpty() ||
                    note.title.lowercase().contains(query.lowercase()) ||
                    note.content.lowercase().contains(query.lowercase())
            val matchesCategory = category == "All" || note.category == category
            matchesQuery && matchesCategory
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        observeNetwork()
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.observeConnectivity().collect { connected ->
                _isConnected.value = connected
            }
        }
    }

    // ── Single note sebagai StateFlow → auto recompose ────────
    fun getNoteByIdFlow(id: Long): StateFlow<Note?> {
        return noteRepository.getAllNotes()
            .map { notes -> notes.find { it.id == id } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = noteRepository.getNoteById(id)
            )
    }

    // tetap ada untuk backward compat
    fun getNoteById(id: Long): Note? = noteRepository.getNoteById(id)

    // ── Note biasa ────────────────────────────────────────────
    fun addNote(
        title: String,
        content: String,
        category: String,
        color: NoteColor = NoteColor.WHITE,
        fontStyle: String = "Normal",
        fontSize: String = "Medium"
    ) {
        viewModelScope.launch {
            noteRepository.insertNote(
                Note(
                    title     = title,
                    content   = content,
                    category  = category,
                    color     = color,
                    fontStyle = fontStyle,
                    fontSize  = fontSize,
                    noteType  = NoteType.NOTE
                )
            )
        }
    }

    // ── Todo note ─────────────────────────────────────────────
    fun addTodoNote(
        title: String,
        category: String,
        color: NoteColor = NoteColor.PINK,
        todoItems: List<TodoItem>
    ) {
        viewModelScope.launch {
            noteRepository.insertNote(
                Note(
                    title     = title,
                    content   = "",
                    category  = category,
                    color     = color,
                    noteType  = NoteType.TODO,
                    todoItems = todoItems
                )
            )
        }
    }

    // toggle satu item di todo note
    fun toggleTodoItem(noteId: Long, itemId: Long) {
        viewModelScope.launch {
            noteRepository.toggleTodoItem(noteId, itemId)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch { noteRepository.deleteNote(note) }
    }

    fun deleteNoteById(id: Long) {
        viewModelScope.launch { noteRepository.deleteNoteById(id) }
    }

    fun updateSearchQuery(query: String) { _searchQuery.value = query }
    fun selectCategory(category: String) { _selectedCategory.value = category }
}