package com.mhss.app.presentation

import com.mhss.app.domain.model.Habit
import com.mhss.app.preferences.domain.model.Order

sealed class HabitsScreenEvent {
    data class Navigate(val habitId: String? = null) : HabitsScreenEvent()
    data class ShowDetails(val habitId: String) : HabitsScreenEvent()
    data class DeleteHabit(val habit: Habit) : HabitsScreenEvent()
    data class UpdateStreak(val habitId: String, count: Int) : HabitsScreenEvent()
    data class CompleteHabit(val habitId: String) : HabitsScreenEvent()
    data class UpdateOrder(val order: Order) : HabitsScreenEvent()
    data class SearchHabits(val query: String) : HabitsScreenEvent()
}
