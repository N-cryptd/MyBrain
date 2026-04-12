package com.mhss.app.domain.use_case

import com.mhss.app.domain.repository.NoteRepository
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory

@Factory
class UpdateNoteLinksUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(noteId: String, content: String) {
        val allNotes = repository.getAllNotes().first()
        repository.updateNoteLinks(noteId, content, allNotes)
    }
}
