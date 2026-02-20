package com.mhss.app.presentation.model

import com.mhss.app.domain.model.Habit

data class HabitUiModel(
    val habit: Habit,
    val isCompletedToday: Boolean = false
)

fun Habit.toHabitUiModel(isCompletedToday: Boolean = false) = HabitUiModel(
    habit = this,
    isCompletedToday = isCompletedToday
)
