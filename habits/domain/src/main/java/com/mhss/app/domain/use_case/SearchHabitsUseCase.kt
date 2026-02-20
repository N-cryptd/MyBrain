package com.mhss.app.domain.use_case

import com.mhss.app.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class SearchHabitsUseCase(
    private val habitsRepository: HabitRepository
) {
    operator fun invoke(query: String): Flow<List<com.mhss.app.domain.model.Habit>> {
        return habitsRepository.searchHabits(query)
    }
}
