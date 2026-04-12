package com.mhss.app.presentation

import com.mhss.app.domain.model.Note
import com.mhss.app.domain.use_case.DeleteNoteUseCase
import com.mhss.app.domain.use_case.GetAllNoteFoldersUseCase
import com.mhss.app.domain.use_case.GetAllNotesUseCase
import com.mhss.app.domain.use_case.GetBacklinksUseCase
import com.mhss.app.domain.use_case.GetLinkedNotesUseCase
import com.mhss.app.domain.use_case.GetNoteFolderUseCase
import com.mhss.app.domain.use_case.GetNoteUseCase
import com.mhss.app.domain.use_case.RemoveNoteLinkUseCase
import com.mhss.app.domain.use_case.SendAiPromptUseCase
import com.mhss.app.domain.use_case.UpsertNoteUseCase
import com.mhss.app.domain.use_case.UpdateNoteLinksUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.koin.core.annotation.Named
import org.mockito.Mockito.*
import org.mockito.kotlin.*
import java.util.Optional

@OptIn(ExperimentalCoroutinesApi::class)
class NoteDetailsViewModelTest {

    private lateinit var viewModel: NoteDetailsViewModel
    private lateinit var applicationScope: CoroutineScope

    private val getNote = mock<GetNoteUseCase>()
    private val upsertNote = mock<UpsertNoteUseCase>()
    private val deleteNote = mock<DeleteNoteUseCase>()
    private val getAllNotes = mock<GetAllNotesUseCase>()
    private val getBacklinks = mock<GetBacklinksUseCase>()
    private val getLinkedNotes = mock<GetLinkedNotesUseCase>()
    private val removeLink = mock<RemoveNoteLinkUseCase>()
    private val updateNoteLinks = mock<UpdateNoteLinksUseCase>()
    private val getAllFolders = mock<GetAllNoteFoldersUseCase>()
    private val getNoteFolder = mock<GetNoteFolderUseCase>()
    private val getPreference = mock<com.mhss.app.preferences.domain.use_case.GetPreferenceUseCase>()
    private val sendAiPrompt = mock<SendAiPromptUseCase>()

    @Before
    fun setup() {
        applicationScope = CoroutineScope(Dispatchers.Unconfined)

        whenever(getAllNotes(any(), any())).thenReturn(flowOf(emptyList()))
        whenever(getBacklinks(any())).thenReturn(emptyList())
        whenever(getLinkedNotes(any())).thenReturn(emptyList())
        whenever(getAllFolders()).thenReturn(flowOf(emptyList()))
        whenever(getNoteFolder(any())).thenReturn(Optional.empty())
        whenever(getPreference(any(), any())).thenReturn(flowOf(0))
        whenever(upsertNote(any(), any())).thenReturn("test-id")
        whenever(getNote(any())).thenReturn(Optional.empty())

        viewModel = NoteDetailsViewModel(
            getNote = getNote,
            upsertNote = upsertNote,
            deleteNote = deleteNote,
            getPreference = getPreference,
            getAllFolders = getAllFolders,
            getNoteFolder = getNoteFolder,
            getAllNotes = getAllNotes,
            getLinkedNotesUseCase = getLinkedNotes,
            getBacklinksUseCase = getBacklinks,
            removeLink = removeLink,
            updateNoteLinks = updateNoteLinks,
            sendAiPrompt = sendAiPrompt,
            @Named("applicationScope") applicationScope = applicationScope,
            id = "",
            folderId = ""
        )
    }

    @Test
    fun `should update note links on content save`() = runTest(UnconfinedTestDispatcher()) {
        val noteId = "test-note-id"
        val newNote = Note(
            id = noteId,
            title = "Test Note",
            content = "Content with [[Linked Note]]",
            createdDate = 0,
            updatedDate = 0
        )

        whenever(getNote(noteId)).thenReturn(Optional.of(newNote))
        whenever(getAllNotes(any(), any())).thenReturn(flowOf(listOf(newNote)))

        viewModel.onEvent(NoteDetailsEvent.UpdateContent(newNote.content))

        val state = viewModel.noteUiState.value
        assertEquals("Test Note", state.title)
        assertEquals(newNote.content, state.content)

        runCurrent()

        verify(updateNoteLinks)(noteId, newNote.content)
    }

    @Test
    fun `should remove link when RemoveLink event is triggered`() = runTest(UnconfinedTestDispatcher()) {
        val noteId = "test-note-id"
        val linkedNoteId = "linked-note-id"
        val linkedNote = Note(
            id = linkedNoteId,
            title = "Linked Note",
            content = "",
            createdDate = 0,
            updatedDate = 0
        )
        val currentNote = Note(
            id = noteId,
            title = "Current Note",
            content = "",
            createdDate = 0,
            updatedDate = 0
        )

        whenever(getNote(noteId)).thenReturn(Optional.of(currentNote))
        whenever(getNote(linkedNoteId)).thenReturn(Optional.of(linkedNote))
        whenever(getAllNotes(any(), any())).thenReturn(flowOf(listOf(currentNote, linkedNote)))
        whenever(getLinkedNotes(noteId)).thenReturn(emptyList())

        viewModel.onEvent(NoteDetailsEvent.RemoveLink(noteId, linkedNoteId))

        runCurrent()

        verify(removeLink)(noteId, linkedNoteId)
        verify(getLinkedNotes)(noteId)
    }
}
