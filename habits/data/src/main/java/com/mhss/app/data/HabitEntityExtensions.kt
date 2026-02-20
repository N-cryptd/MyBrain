package com.mhss.app.data

import com.mhss.app.database.entity.HabitEntity
import com.mhss.app.domain.model.Habit

fun HabitEntity.toHabit() = Habit(
    title = title,
    description = description,
    priority = com.mhss.app.domain.model.Priority.entries.firstOrNull { it.value == priority } ?: com.mhss.app.domain.model.Priority.LOW,
    createdDate = createdDate,
    updatedDate = updatedDate,
    frequency = com.mhss.app.domain.model.HabitFrequency.entries.firstOrNull { it.value == frequency } ?: com.mhss.app.domain.model.HabitFrequency.DAILY,
    frequencyDays = frequencyDays,
    completedDates = completedDates,
    streakCount = streakCount,
    bestStreak = bestStreak,
    reminderEnabled = reminderEnabled,
    reminderTime = reminderTime,
    alarmId = alarmId,
    id = id
)

fun Habit.toHabitEntity() = HabitEntity(
    title = title,
    description = description,
    priority = priority.value,
    createdDate = createdDate,
    updatedDate = updatedDate,
    frequency = frequency.value,
    frequencyDays = frequencyDays,
    completedDates = completedDates,
    streakCount = streakCount,
    bestStreak = bestStreak,
    reminderEnabled = reminderEnabled,
    reminderTime = reminderTime,
    alarmId = alarmId,
    id = id
)
