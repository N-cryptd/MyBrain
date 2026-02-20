package com.mhss.app.domain.use_case

import com.mhss.app.domain.repository.HabitRepository
import org.koin.core.annotation.Single
import kotlin.time.Clock.System.now

@Single
class UncompleteHabitUseCase(
    private val habitsRepository: HabitRepository
) {
    suspend operator fun invoke(habitId: String, timestamp: Long = now().toEpochMilliseconds()) {
        habitsRepository.uncompleteHabit(habitId, timestamp)
    }
}
