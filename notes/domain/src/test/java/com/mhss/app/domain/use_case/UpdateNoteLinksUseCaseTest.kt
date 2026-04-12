package com.mhss.app.domain.use_case

import com.mhss.app.domain.model.Note
import com.mhss.app.domain.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class UpdateNoteLinksUseCaseTest {

    private lateinit var useCase: UpdateNoteLinksUseCase
    private val mockRepository: NoteRepository = mock()

    @Before
    fun setup() {
        useCase = UpdateNoteLinksUseCase(mockRepository)
    }

    @Test
    fun `should call repository updateNoteLinks with correct parameters`() = runTest(UnconfinedTestDispatcher()) {
        val allNotes = listOf(
            Note(id = "1", title = "Note 1", content = "Content 1", createdDate = 0, updatedDate = 0),
            Note(id = "2", title = "Note 2", content = "Content 2", createdDate = 0, updatedDate = 0)
        )
        whenever(mockRepository.getAllNotes()).thenReturn(flowOf(allNotes))

        useCase("note-id", "Content with [[Note 1]] link")

        runCurrent()

        verify(mockRepository).updateNoteLinks("note-id", "Content with [[Note 1]] link", allNotes)
    }

    @Test
    fun `should handle empty content gracefully`() = runTest(UnconfinedTestDispatcher()) {
        val allNotes = emptyList<Note>()
        whenever(mockRepository.getAllNotes()).thenReturn(flowOf(allNotes))

        useCase("note-id", "")

        runCurrent()

        verify(mockRepository).updateNoteLinks("note-id", "", allNotes)
    }

    @Test
    fun `should handle multiple links in content`() = runTest(UnconfinedTestDispatcher()) {
        val allNotes = listOf(
            Note(id = "1", title = "Note 1", content = "", createdDate = 0, updatedDate = 0),
            Note(id = "2", title = "Note 2", content = "", createdDate = 0, updatedDate = 0),
            Note(id = "3", title = "Note 3", content = "", createdDate = 0, updatedDate = 0)
        )
        whenever(mockRepository.getAllNotes()).thenReturn(flowOf(allNotes))

        useCase("note-id", "Links to [[Note 1]] and [[Note 2]] and [[Note 3]]")

        runCurrent()

        verify(mockRepository).updateNoteLinks("note-id", "Links to [[Note 1]] and [[Note 2]] and [[Note 3]]", allNotes)
    }
}

    @Test
    fun `should call repository updateNoteLinks with correct parameters`() = runTest {
        val mockRepository = mockk<NoteRepository>()
        val allNotes = listOf(
            Note(id = "1", title = "Note 1", content = "Content 1", createdDate = 0, updatedDate = 0),
            Note(id = "2", title = "Note 2", content = "Content 2", createdDate = 0, updatedDate = 0)
        )
        every { mockRepository.getAllNotes() } returns flowOf(allNotes)

        val useCase = UpdateNoteLinksUseCase(mockRepository)

        useCase("note-id", "Content with [[Note 1]] link")

        coVerify(exactly = 1) {
            mockRepository.updateNoteLinks("note-id", "Content with [[Note 1]] link", allNotes)
        }
    }

    @Test
    fun `should handle empty content gracefully`() = runTest {
        val mockRepository = mockk<NoteRepository>()
        val allNotes = emptyList<Note>()
        every { mockRepository.getAllNotes() } returns flowOf(allNotes)

        val useCase = UpdateNoteLinksUseCase(mockRepository)

        useCase("note-id", "")

        coVerify(exactly = 1) {
            mockRepository.updateNoteLinks("note-id", "", allNotes)
        }
    }

    @Test
    fun `should handle multiple links in content`() = runTest {
        val mockRepository = mockk<NoteRepository>()
        val allNotes = listOf(
            Note(id = "1", title = "Note 1", content = "", createdDate = 0, updatedDate = 0),
            Note(id = "2", title = "Note 2", content = "", createdDate = 0, updatedDate = 0),
            Note(id = "3", title = "Note 3", content = "", createdDate = 0, updatedDate = 0)
        )
        every { mockRepository.getAllNotes() } returns flowOf(allNotes)

        val useCase = UpdateNoteLinksUseCase(mockRepository)

        useCase("note-id", "Links to [[Note 1]] and [[Note 2]] and [[Note 3]]")

        coVerify(exactly = 1) {
            mockRepository.updateNoteLinks("note-id", "Links to [[Note 1]] and [[Note 2]] and [[Note 3]]", allNotes)
        }
    }
}
