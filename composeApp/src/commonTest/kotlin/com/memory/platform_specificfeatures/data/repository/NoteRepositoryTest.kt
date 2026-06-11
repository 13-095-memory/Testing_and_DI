package com.memory.platform_specificfeatures.data.repository

import app.cash.turbine.test
import com.memory.platform_specificfeatures.data.model.Note
import com.memory.platform_specificfeatures.data.model.NoteColor
import com.memory.platform_specificfeatures.data.model.NoteType
import com.memory.platform_specificfeatures.data.model.TodoItem
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NoteRepositoryTest {

    @Test
    fun getNoteById_existingId_returnsCorrectNote() {
        // Arrange: Initialize repository
        val repository = NoteRepository()
        val targetId = 1L

        // Act: Retrieve note by ID
        val note = repository.getNoteById(targetId)

        // Assert: Verify the note details
        assertNotNull(note)
        assertEquals(targetId, note.id)
        assertEquals("Meeting notes", note.title)
    }

    @Test
    fun getNoteById_nonExistingId_returnsNull() {
        // Arrange: Initialize repository
        val repository = NoteRepository()
        val targetId = 999L

        // Act: Retrieve note by invalid ID
        val note = repository.getNoteById(targetId)

        // Assert: Verify that result is null
        assertNull(note)
    }

    @Test
    fun insertNote_validNote_addsToBeginningOfListWithNewId() = runTest {
        // Arrange: Initialize repository and prepare a new note
        val repository = NoteRepository()
        val newNote = Note(
            title = "New test note",
            content = "This is a new test note content",
            category = "Work",
            color = NoteColor.BLUE
        )

        // Act: Insert the new note
        repository.insertNote(newNote)

        // Assert: Verify the note was inserted at index 0 and has correct ID (max existing ID + 1 = 8)
        val allNotes = repository.getNoteById(8L) // max ID initially is 7, so next is 8
        assertNotNull(allNotes)
        assertEquals("New test note", allNotes.title)
        assertEquals("This is a new test note content", allNotes.content)
    }

    @Test
    fun deleteNote_existingNote_removesFromList() = runTest {
        // Arrange: Initialize repository and identify a note to delete
        val repository = NoteRepository()
        val targetNote = repository.getNoteById(1L)!!

        // Act: Delete the note
        repository.deleteNote(targetNote)

        // Assert: Verify the note no longer exists in the repository
        val deletedNote = repository.getNoteById(1L)
        assertNull(deletedNote)
    }

    @Test
    fun deleteNoteById_existingId_removesFromList() = runTest {
        // Arrange: Initialize repository
        val repository = NoteRepository()
        val targetId = 2L

        // Act: Delete the note by ID
        repository.deleteNoteById(targetId)

        // Assert: Verify the note no longer exists in the repository
        val deletedNote = repository.getNoteById(targetId)
        assertNull(deletedNote)
    }

    @Test
    fun updateNote_existingNote_modifiesDetails() = runTest {
        // Arrange: Initialize repository and fetch note to update
        val repository = NoteRepository()
        val originalNote = repository.getNoteById(3L)!!
        val updatedNote = originalNote.copy(title = "Updated Grocery List", content = "Updated items list")

        // Act: Update the note
        repository.updateNote(updatedNote)

        // Assert: Verify updated details are stored
        val result = repository.getNoteById(3L)!!
        assertEquals("Updated Grocery List", result.title)
        assertEquals("Updated items list", result.content)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FLOW TESTING WITH TURBINE
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun getAllNotes_flowEmitsUpdatedList_whenNewNoteInserted() = runTest {
        // Arrange: Initialize repository and define target flow
        val repository = NoteRepository()
        val notesFlow = repository.getAllNotes()

        // Act & Assert using Turbine
        notesFlow.test {
            // First emission is the initial state containing 7 notes
            val initialList = awaitItem()
            assertEquals(7, initialList.size)

            // Insert new note
            val newNote = Note(
                title = "Flow test note",
                content = "Testing flows",
                category = "Study",
                color = NoteColor.YELLOW
            )
            repository.insertNote(newNote)

            // Second emission should occur, containing 8 notes
            val updatedList = awaitItem()
            assertEquals(8, updatedList.size)
            assertEquals("Flow test note", updatedList.first().title)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getAllNotes_flowEmitsUpdatedChecklistState_whenTodoItemToggled() = runTest {
        // Arrange: Initialize repository and define target flow
        val repository = NoteRepository()
        val notesFlow = repository.getAllNotes()

        // Act & Assert using Turbine
        notesFlow.test {
            // First emission is the initial list
            val initialList = awaitItem()
            val initialTodoNote = initialList.find { it.id == 7L }!!
            // Check that item 2 in note 7 is initially not done
            val initialItem = initialTodoNote.todoItems.find { it.id == 2L }!!
            assertFalse(initialItem.isDone)

            // Toggle item 2 of note 7
            repository.toggleTodoItem(noteId = 7L, itemId = 2L)

            // Second emission should reflect the toggled item state
            val updatedList = awaitItem()
            val updatedTodoNote = updatedList.find { it.id == 7L }!!
            val updatedItem = updatedTodoNote.todoItems.find { it.id == 2L }!!
            assertTrue(updatedItem.isDone)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
