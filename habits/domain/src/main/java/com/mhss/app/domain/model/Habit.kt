package com.mhss.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Habit(
    val title: String,
    val description: String = "",
    val priority: Priority = Priority.LOW,
    val createdDate: Long = 0L,
    val updatedDate: Long = 0L,
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    val frequencyDays: List<Int> = emptyList(),
    val completedDates: List<Long> = emptyList(),
    val streakCount: Int = 0,
    val bestStreak: Int = 0,
    val reminderEnabled: Boolean = false,
    val reminderTime: Long = 0L,
    val alarmId: Int? = null,
    val id: String = ""
)

enum class HabitFrequency(val value: Int) {
    DAILY(0),
    WEEKLY(1),
    CUSTOM_DAYS(2),
    WEEKDAYS(3),
    WEEKENDS(4),
    MONTHLY(5)
}
