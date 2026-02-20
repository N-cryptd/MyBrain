package com.mhss.app.domain.use_case

import com.mhss.app.domain.repository.HabitRepository
import org.koin.core.annotation.Factory

@Factory
class GetHabitByIdUseCase(
    private val habitsRepository: HabitRepository
) {
    suspend operator fun invoke(id: String) = habitsRepository.getHabitById(id)
}
