package com.mhss.app.domain.use_case

import com.mhss.app.domain.model.Note
import com.mhss.app.domain.repository.NoteRepository
import org.koin.core.annotation.Factory

@Factory
class GetBacklinksUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(noteId: String): List<Note> {
        return repository.getBacklinks(noteId)
    }
}