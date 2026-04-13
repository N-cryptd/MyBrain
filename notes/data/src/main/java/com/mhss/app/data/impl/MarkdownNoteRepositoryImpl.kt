package com.mhss.app.data.impl

import android.net.Uri
import androidx.core.net.toUri
import com.mhss.app.data.storage.MarkdownFileManager
import com.mhss.app.domain.model.Note
import com.mhss.app.domain.model.NoteFolder
import com.mhss.app.domain.repository.NoteRepository
import com.mhss.app.util.linking.NoteLinkingEngine
import com.mhss.app.util.linking.NoteRef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MarkdownNoteRepositoryImpl(
    private val markdownFileManager: MarkdownFileManager,
    private val rootUri: Uri,
    private val linkingEngine: NoteLinkingEngine = NoteLinkingEngine()
) : NoteRepository {

    override fun getAllFolderlessNotes(): Flow<List<Note>> {
        return markdownFileManager.getFolderNotesFlow(rootUri)
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return markdownFileManager.getAllNotesFlow(rootUri)
    }

    override suspend fun getNote(id: String): Note? {
        return try {
            markdownFileManager.getNote(id.toUri())
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun searchNotes(query: String): List<Note> {
        return markdownFileManager.searchNotes(query, rootUri)
    }

    override fun getNotesByFolder(folderId: String): Flow<List<Note>> {
        return markdownFileManager.getFolderNotesFlow(folderId.toUri())
    }

    override suspend fun upsertNote(note: Note, currentFolderId: String?): String {
        return markdownFileManager.upsertNote(note, currentFolderId, rootUri)
    }

    override suspend fun upsertNotes(notes: List<Note>): List<String> {
        return notes.map {
            upsertNote(it, null)
        }
    }

    override suspend fun deleteNote(note: Note) {
        markdownFileManager.deleteNote(note, rootUri)
    }

    override suspend fun insertNoteFolder(folderName: String): String {
        return markdownFileManager.createFolder(folderName, rootUri)
    }

    override suspend fun updateNoteFolder(folder: NoteFolder) {
        markdownFileManager.updateFolder(folder.id.toUri(), folder.name.trim(), rootUri)
    }

    override suspend fun deleteNoteFolder(folder: NoteFolder) {
        markdownFileManager.deleteFolder(folder.id.toUri(), rootUri)
    }

    override fun getAllNoteFolders(): Flow<List<NoteFolder>> {
        return markdownFileManager.getFolderFoldersFlow(rootUri)
    }

    override suspend fun getNoteFolder(folderId: String): NoteFolder? {
        if (folderId == rootUri.toString()) return null
        return markdownFileManager.getFolder(folderId.toUri())
    }

    override suspend fun searchFoldersByName(name: String): List<NoteFolder> {
        return markdownFileManager.searchFolderByName(name, rootUri)
    }

    override suspend fun getLinkedNotes(noteId: String): List<Note> {
        return try {
            val note = getNote(noteId) ?: return emptyList()
            val linkTitles = parseLinkTitles(note.content)
            val allNotes = getAllNotesFlow(rootUri).first()
            val noteRefs = allNotes.map { NoteRef(it.id, it.title) }

            linkTitles.mapNotNull { link ->
                linkingEngine.resolveLink(link, noteRefs)?.id
            }.mapNotNull { linkedNoteId ->
                allNotes.find { it.id == linkedNoteId }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getBacklinks(noteId: String): List<Note> {
        return try {
            val allNotes = getAllNotesFlow(rootUri).first()

            allNotes.filter { otherNote ->
                if (otherNote.id == noteId) return@filter false

                val linkTitles = parseLinkTitles(otherNote.content)
                val noteRefs = allNotes.map { NoteRef(it.id, it.title) }

                linkTitles.any { link ->
                    val resolvedNote = linkingEngine.resolveLink(link, noteRefs)
                    resolvedNote?.id == noteId
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun createLink(fromNoteId: String, toNoteId: String) {
        try {
            val fromNote = getNote(fromNoteId) ?: return
            val toNote = getNote(toNoteId) ?: return

            if (!parseLinkTitles(fromNote.content).any {
                it.equals(toNote.title, ignoreCase = true)
            }) {
                val linkText = "[[${toNote.title}]]"
                val updatedContent = fromNote.content.trim() + "\n\n" + linkText
                upsertNote(fromNote.copy(content = updatedContent), null)
            }
        } catch (e: Exception) {
        }
    }

    override suspend fun removeLink(fromNoteId: String, toNoteId: String) {
        try {
            val fromNote = getNote(fromNoteId) ?: return
            val toNote = getNote(toNoteId) ?: return

            val linkText = "[[${toNote.title}]]"
            val updatedContent = fromNote.content.replace(linkText, "")
            upsertNote(fromNote.copy(content = updatedContent.trim()), null)
        } catch (e: Exception) {
        }
    }

    override suspend fun updateNoteLinks(noteId: String, content: String, allNotes: List<Note>) {
        try {
            val linkTitles = parseLinkTitles(content)
            val noteRefs = allNotes.map { NoteRef(it.id, it.title) }

            val linkedNoteIds = linkTitles.mapNotNull { link ->
                linkingEngine.resolveLink(link, noteRefs)?.id
            }

            val note = getNote(noteId)
            if (note != null && linkedNoteIds.isNotEmpty()) {
                val updatedNote = note.copy(linkedNoteIds = formatLinkedNoteIdsJson(linkedNoteIds))
                upsertNote(updatedNote, null)
            }
        } catch (e: Exception) {
        }
    }

    private fun parseLinkTitles(content: String): List<String> {
        val linkPattern = Regex("""\[\[([^\]]+)\]\]""")
        return linkPattern.findAll(content)
            .map { match ->
                val linkContent = match.groupValues[1].trim()
                if (linkContent.contains("|")) {
                    linkContent.substringBefore("|").trim()
                } else {
                    linkContent
                }
            }
            .distinctBy { it.lowercase() }
            .toList()
    }

    private fun formatLinkedNoteIdsJson(noteIds: List<String>): String {
        return noteIds.joinToString(prefix = "[", separator = ",", postfix = "]") { "\"$it\"" }
    }

    private fun getAllNotesFlow(rootUri: Uri): Flow<List<Note>> {
        return markdownFileManager.getAllNotesFlow(rootUri)
    }
}