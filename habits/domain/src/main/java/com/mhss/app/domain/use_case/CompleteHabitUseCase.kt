package com.mhss.app.domain.use_case

import com.mhss.app.domain.repository.HabitRepository
import org.koin.core.annotation.Single
import kotlin.time.Clock.System.now

@Single
class CompleteHabitUseCase(
    private val habitsRepository: HabitRepository
) {
    suspend operator fun invoke(habitId: String) {
        val timestamp = now().toEpochMilliseconds()
        habitsRepository.completeHabit(habitId, timestamp)
    }
}
