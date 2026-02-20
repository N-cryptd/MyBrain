package com.mhss.app.domain.use_case

import com.mhss.app.domain.model.Habit
import com.mhss.app.domain.model.Priority
import com.mhss.app.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class GetHabitsByPriorityUseCase(
    private val habitsRepository: HabitRepository
) {
    operator fun invoke(priority: Priority): Flow<List<Habit>> {
        return habitsRepository.getHabitsByPriority(priority.value)
    }
}
