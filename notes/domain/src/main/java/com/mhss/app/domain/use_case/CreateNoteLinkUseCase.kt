package com.mhss.app.domain.use_case

import org.koin.core.annotation.Factory

@Factory
class CreateNoteLinkUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(fromNoteId: String, toNoteId: String) {
        repository.createLink(fromNoteId, toNoteId)
    }
}