package com.mhss.app.domain.use_case

import com.mhss.app.domain.model.Note
import com.mhss.app.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetLinkedNotesUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(noteId: String): List<Note> {
        return repository.getLinkedNotes(noteId)
    }
}