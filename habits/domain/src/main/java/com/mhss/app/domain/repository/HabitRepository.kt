package com.mhss.app.domain.repository

import com.mhss.app.domain.model.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRepository {

    fun getAllHabits(): Flow<List<Habit>>

    suspend fun getHabitById(id: String): Habit?

    suspend fun getHabitByAlarm(alarmId: Int): Habit?

    fun searchHabits(query: String): Flow<List<Habit>>

    fun getHabitsByPriority(priority: Int): Flow<List<Habit>>

    suspend fun upsertHabit(habit: Habit): String

    suspend fun upsertHabits(habits: List<Habit>): List<String>

    suspend fun updateHabit(habit: Habit)

    suspend fun deleteHabit(habit: Habit)

    suspend fun updateStreakCount(habitId: String, count: Int)

    suspend fun completeHabit(habitId: String, timestamp: Long)

    suspend fun uncompleteHabit(habitId: String, timestamp: Long)

    suspend fun getTodayHabits(): List<Habit>
}
