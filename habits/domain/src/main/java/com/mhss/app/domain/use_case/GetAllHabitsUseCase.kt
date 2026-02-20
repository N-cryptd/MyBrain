package com.mhss.app.domain.use_case

import com.mhss.app.domain.model.Habit
import com.mhss.app.domain.repository.HabitRepository
import com.mhss.app.preferences.domain.model.Order
import com.mhss.app.preferences.domain.model.OrderType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class GetAllHabitsUseCase(
    private val habitsRepository: HabitRepository,
    @Named("defaultDispatcher") private val defaultDispatcher: CoroutineDispatcher
) {
    operator fun invoke(order: Order): Flow<List<Habit>> {
        return habitsRepository.getAllHabits().map { habits ->
            when (order.orderType) {
                is OrderType.ASC -> {
                    when (order) {
                        is Order.Alphabetical -> habits.sortedBy { it.title }
                        is Order.DateCreated -> habits.sortedBy { it.createdDate }
                        is Order.DateModified -> habits.sortedBy { it.updatedDate }
                        is Order.Priority -> habits.sortedBy { habit: Habit -> habit.priority }
                        else -> habits.sortedBy { it.createdDate }
                    }
                }
                is OrderType.DESC -> {
                    when (order) {
                        is Order.Alphabetical -> habits.sortedByDescending { it.title }
                        is Order.DateCreated -> habits.sortedByDescending { it.createdDate }
                        is Order.DateModified -> habits.sortedByDescending { it.updatedDate }
                        is Order.Priority -> habits.sortedByDescending { habit: Habit -> habit.priority }
                        else -> habits.sortedByDescending { it.createdDate }
                    }
                }
            }
        }.flowOn(defaultDispatcher)
    }
}
