package com.memory.platform_specificfeatures.presentation

import com.memory.platform_specificfeatures.data.model.Note
import com.memory.platform_specificfeatures.data.model.NoteColor
import com.memory.platform_specificfeatures.data.repository.NoteRepository
import com.memory.platform_specificfeatures.platform.NetworkMonitor
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val noteRepository: NoteRepository = mockk(relaxed = true)
    private val networkMonitor: NetworkMonitor = mockk(relaxed = true)

    private lateinit var viewModel: NotesViewModel

    private val initialNotes = listOf(
        Note(
            id = 1,
            title = "Personal task",
            content = "Buy milk and bread",
            category = "Personal",
            color = NoteColor.PINK
        ),
        Note(
            id = 2,
            title = "Work task",
            content = "Setup Koin dependency injection",
            category = "Work",
            color = NoteColor.BLUE
        )
    )

    private val notesFlow = MutableStateFlow(initialNotes)
    private val networkFlow = MutableStateFlow(true)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { noteRepository.getAllNotes() } returns notesFlow
        every { networkMonitor.observeConnectivity() } returns networkFlow

        viewModel = NotesViewModel(noteRepository, networkMonitor)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_observesNetworkAndLoadsNotes() = runTest {
        backgroundScope.launch { viewModel.filteredNotes.collect {} }

        testDispatcher.scheduler.advanceUntilIdle()

        verify { networkMonitor.observeConnectivity() }
        verify { noteRepository.getAllNotes() }
        assertTrue(viewModel.isConnected.value)
        assertEquals(2, viewModel.filteredNotes.value.size)
    }

    @Test
    fun deleteNote_callsRepositoryDelete() = runTest {
        backgroundScope.launch { viewModel.filteredNotes.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        val noteToDelete = initialNotes.first()
        viewModel.deleteNote(noteToDelete)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { noteRepository.deleteNote(noteToDelete) }
    }

    @Test
    fun updateSearchQuery_emptyQuery_returnsAllNotes() = runTest {
        backgroundScope.launch { viewModel.filteredNotes.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateSearchQuery("Work")
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, viewModel.filteredNotes.value.size)

        viewModel.updateSearchQuery("")
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, viewModel.filteredNotes.value.size)
    }

    @Test
    fun selectCategory_allCategory_returnsAllNotes() = runTest {
        backgroundScope.launch { viewModel.filteredNotes.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectCategory("Personal")
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, viewModel.filteredNotes.value.size)

        viewModel.selectCategory("All")
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, viewModel.filteredNotes.value.size)
    }

    @Test
    fun searchAndCategory_combined_filtersCorrectly() = runTest {
        backgroundScope.launch { viewModel.filteredNotes.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectCategory("Work")
        viewModel.updateSearchQuery("Setup")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.filteredNotes.value.size)
        assertEquals("Work task", viewModel.filteredNotes.value.first().title)
    }

    @Test
    fun addNote_callsRepositoryInsert() = runTest {
        backgroundScope.launch { viewModel.filteredNotes.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.addNote(
            title = "Study Multiplatform",
            content = "Learn KMP coding",
            category = "Study",
            color = NoteColor.GREEN
        )
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            noteRepository.insertNote(
                match { note ->
                    note.title == "Study Multiplatform" &&
                            note.content == "Learn KMP coding" &&
                            note.category == "Study" &&
                            note.color == NoteColor.GREEN
                }
            )
        }
    }

    @Test
    fun networkDisconnected_updatesIsConnectedState() = runTest {
        backgroundScope.launch { viewModel.filteredNotes.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.isConnected.value)

        networkFlow.value = false
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(false, viewModel.isConnected.value)
    }
}
