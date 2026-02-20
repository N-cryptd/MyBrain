package com.mhss.app.data.tools

import com.mhss.app.domain.model.Note
import com.mhss.app.domain.model.NoteFolder
import com.mhss.app.domain.use_case.CreateNoteFolderUseCase
import com.mhss.app.domain.use_case.GetNoteFolderUseCase
import com.mhss.app.domain.use_case.GetNoteUseCase
import com.mhss.app.domain.use_case.SearchNoteFoldersByNameUseCase
import com.mhss.app.domain.use_case.SearchNotesUseCase
import com.mhss.app.domain.use_case.UpsertNoteUseCase
import com.mhss.app.domain.use_case.UpsertNotesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class NoteToolSetTest {

    private lateinit var noteToolSet: NoteToolSet
    
    private val upsertNote: UpsertNoteUseCase = mockk()
    private val upsertNotes: UpsertNotesUseCase = mockk()
    private val searchNotesByName: SearchNotesUseCase = mockk()
    private val getNote: GetNoteUseCase = mockk()
    private val createFolderUseCase: CreateNoteFolderUseCase = mockk()
    private val searchNoteFoldersByName: SearchNoteFoldersByNameUseCase = mockk()
    private val getNoteFolder: GetNoteFolderUseCase = mockk()

    @Before
    fun setup() {
        noteToolSet = NoteToolSet(
            upsertNote = upsertNote,
            upsertNotes = upsertNotes,
            searchNotesByName = searchNotesByName,
            getNote = getNote,
            createFolderUseCase = createFolderUseCase,
            searchNoteFoldersByName = searchNoteFoldersByName,
            getNoteFolder = getNoteFolder
        )
    }

    @Test
    fun testSearchNotes() = runTest {
        val expectedNotes = listOf(
            Note(title = "Test Note 1", content = "Content 1", id = "1"),
            Note(title = "Test Note 2", content = "Content 2", id = "2")
        )
        coEvery { searchNotesByName("test") } returns expectedNotes
        
        val result = noteToolSet.searchNotes("test")
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return 2 notes", 2, result.notes.size)
        assertEquals("First note should match", "Test Note 1", result.notes[0].title)
        coVerify { searchNotesByName("test") }
    }

    @Test
    fun testCreateNote() = runTest {
        val noteId = "test-note-id"
        coEvery { upsertNote(any()) } returns noteId
        
        val result = noteToolSet.createNote(
            title = "Test Note",
            content = "Test Content",
            folderId = null,
            pinned = false
        )
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return the created note ID", noteId, result.createdNoteId)
        coVerify { upsertNote(any()) }
    }

    @Test
    fun testCreateNoteWithFolder() = runTest {
        val folderId = "folder-123"
        val noteId = "test-note-id"
        val folder = NoteFolder(name = "Test Folder", id = folderId)
        
        coEvery { getNoteFolder(folderId) } returns folder
        coEvery { upsertNote(any()) } returns noteId
        
        val result = noteToolSet.createNote(
            title = "Test Note",
            content = "Test Content",
            folderId = folderId,
            pinned = false
        )
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return the created note ID", noteId, result.createdNoteId)
        coVerify { getNoteFolder(folderId) }
        coVerify { upsertNote(any()) }
    }

    @Test
    fun testCreateMultipleNotes() = runTest {
        val noteIds = listOf("note-1", "note-2", "note-3")
        val noteInputs = listOf(
            NoteInput(title = "Note 1", content = "Content 1"),
            NoteInput(title = "Note 2", content = "Content 2"),
            NoteInput(title = "Note 3", content = "Content 3")
        )
        
        coEvery { upsertNotes(any()) } returns noteIds
        
        val result = noteToolSet.createMultipleNotes(noteInputs)
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return 3 note IDs", 3, result.createdNoteIds.size)
        assertEquals("First ID should match", "note-1", result.createdNoteIds[0])
        coVerify { upsertNotes(any()) }
    }

    @Test
    fun testGetNoteById() = runTest {
        val note = Note(title = "Test Note", content = "Test Content", id = "note-123")
        coEvery { getNote("note-123") } returns note
        
        val result = noteToolSet.getNoteById("note-123")
        
        assertNotNull("Result should not be null", result)
        assertEquals("Note title should match", "Test Note", result.note.title)
        assertEquals("Note ID should match", "note-123", result.note.id)
        coVerify { getNote("note-123") }
    }

    @Test
    fun testSearchFolders() = runTest {
        val expectedFolders = listOf(
            NoteFolder(name = "Work", id = "folder-1"),
            NoteFolder(name = "Personal", id = "folder-2")
        )
        coEvery { searchNoteFoldersByName("work") } returns expectedFolders
        
        val result = noteToolSet.searchFolders("work")
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return 2 folders", 2, result.folders.size)
        assertEquals("First folder should match", "Work", result.folders[0].name)
        coVerify { searchNoteFoldersByName("work") }
    }

    @Test
    fun testCreateFolder() = runTest {
        val folderId = "new-folder-id"
        coEvery { createFolderUseCase("New Folder") } returns folderId
        
        val result = noteToolSet.createFolder("New Folder")
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return the created folder ID", folderId, result.folderId)
        coVerify { createFolderUseCase("New Folder") }
    }
}
