package com.mhss.app.presentation

import com.mhss.app.domain.model.Habit

sealed class HabitDetailEvent {
    data class UpdateTitle(val title: String) : HabitDetailEvent()
    data class UpdateDescription(val description: String) : HabitDetailEvent()
    data class UpdatePriority(val priority: com.mhss.app.domain.model.Priority) : HabitDetailEvent()
    data class UpdateFrequency(val frequency: com.mhss.app.domain.model.HabitFrequency) : HabitDetailEvent()
    data class UpdateFrequencyDays(val days: List<Int>) : HabitDetailEvent()
    data class UpdateReminderEnabled(val enabled: Boolean) : HabitDetailEvent()
    data class UpdateReminderTime(val time: Long) : HabitDetailEvent()
    data class SaveHabit(val habit: Habit) : HabitDetailEvent()
    data object DeleteHabit : HabitDetailEvent()
    data object NavigateUp : HabitDetailEvent()
}
