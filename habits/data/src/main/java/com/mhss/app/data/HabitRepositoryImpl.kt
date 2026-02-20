package com.mhss.app.data

import com.mhss.app.database.dao.HabitDao
import com.mhss.app.database.entity.HabitEntity
import com.mhss.app.domain.model.Habit
import com.mhss.app.domain.model.HabitFrequency
import com.mhss.app.domain.repository.HabitRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class HabitRepositoryImpl(
    private val habitDao: HabitDao,
    @Named("ioDispatcher") private val ioDispatcher: CoroutineDispatcher
) : HabitRepository {

    override fun getAllHabits(): Flow<List<Habit>> {
        return habitDao.getAllHabits()
            .flowOn(ioDispatcher)
            .map { habits ->
                habits.map { it.toHabit() }
            }
    }

    override suspend fun getHabitById(id: String): Habit? {
        return withContext(ioDispatcher) {
            habitDao.getHabit(id)?.toHabit()
        }
    }

    override suspend fun getHabitByAlarm(alarmId: Int): Habit? {
        return withContext(ioDispatcher) {
            habitDao.getHabitByAlarm(alarmId)?.toHabit()
        }
    }

    override fun searchHabits(query: String): Flow<List<Habit>> {
        return habitDao.searchHabits(query)
            .flowOn(ioDispatcher)
            .map { habits ->
                habits.map { it.toHabit() }
            }
    }

    override fun getHabitsByPriority(priority: Int): Flow<List<Habit>> {
        return habitDao.getHabitsByPriority(priority)
            .flowOn(ioDispatcher)
            .map { habits ->
                habits.map { it.toHabit() }
            }
    }

    override suspend fun upsertHabit(habit: Habit): String {
        return withContext(ioDispatcher) {
            val id = habit.id.ifBlank { generateId() }
            habitDao.upsertHabit(habit.copy(id = id).toHabitEntity())
            id
        }
    }

    override suspend fun upsertHabits(habits: List<Habit>): List<String> {
        return withContext(ioDispatcher) {
            val habitsWithIds = habits.map {
                it.copy(id = it.id.ifBlank { generateId() })
            }
            habitDao.upsertHabits(habitsWithIds.map { it.toHabitEntity() })
            habitsWithIds.map { it.id }
        }
    }

    override suspend fun updateHabit(habit: Habit) {
        withContext(ioDispatcher) {
            habitDao.updateHabit(habit.toHabitEntity())
        }
    }

    override suspend fun deleteHabit(habit: Habit) {
        withContext(ioDispatcher) {
            habitDao.deleteHabit(habit.toHabitEntity())
        }
    }

    override suspend fun updateStreakCount(habitId: String, count: Int) {
        withContext(ioDispatcher) {
            habitDao.updateStreakCount(habitId, count)
        }
    }

    override suspend fun completeHabit(habitId: String, timestamp: Long) {
        withContext(ioDispatcher) {
            habitDao.addCompletedDate(habitId, timestamp)
            val habit = habitDao.getHabit(habitId)?.toHabit() ?: return@withContext
            val newStreak = calculateStreak(habit.copy(completedDates = habit.completedDates + timestamp))
            habitDao.updateStreakCount(habitId, newStreak)
            if (newStreak > habit.bestStreak) {
                habitDao.updateBestStreak(habitId, newStreak)
            }
        }
    }

    override suspend fun uncompleteHabit(habitId: String, timestamp: Long) {
        withContext(ioDispatcher) {
            habitDao.removeCompletedDate(habitId, timestamp)
            val habit = habitDao.getHabit(habitId)?.toHabit() ?: return@withContext
            val newStreak = calculateStreak(habit.copy(completedDates = habit.completedDates - timestamp))
            habitDao.updateStreakCount(habitId, newStreak)
        }
    }

    override suspend fun getTodayHabits(): List<Habit> {
        return withContext(ioDispatcher) {
            habitDao.getAllFullHabits()
                .map { it.toHabit() }
                .filter { isHabitDueToday(it) }
        }
    }

    private fun generateId(): String {
        return java.util.UUID.randomUUID().toString()
    }

    private fun calculateStreak(habit: Habit): Int {
        if (habit.completedDates.isEmpty()) return 0

        val sortedDates = habit.completedDates.sortedDescending()
        val calendar = java.util.Calendar.getInstance()
        val today = calendar.apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis

        var streak = 0
        var currentDate = today

        for (date in sortedDates) {
            val dateCalendar = java.util.Calendar.getInstance().apply { timeInMillis = date }
            dateCalendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            dateCalendar.set(java.util.Calendar.MINUTE, 0)
            dateCalendar.set(java.util.Calendar.SECOND, 0)
            dateCalendar.set(java.util.Calendar.MILLISECOND, 0)
            val normalizedDate = dateCalendar.timeInMillis

            if (normalizedDate == currentDate) {
                streak++
                currentDate -= getMillisForFrequency(habit.frequency)
            } else {
                break
            }
        }

        return streak
    }

    private fun getMillisForFrequency(frequency: HabitFrequency): Long {
        return when (frequency) {
            HabitFrequency.DAILY -> 24 * 60 * 60 * 1000L
            HabitFrequency.WEEKLY -> 7 * 24 * 60 * 60 * 1000L
            else -> 24 * 60 * 60 * 1000L
        }
    }

    private fun isHabitDueToday(habit: Habit): Boolean {
        val calendar = java.util.Calendar.getInstance()
        val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        val dayOfWeekIndex = when (dayOfWeek) {
            java.util.Calendar.MONDAY -> 1
            java.util.Calendar.TUESDAY -> 2
            java.util.Calendar.WEDNESDAY -> 3
            java.util.Calendar.THURSDAY -> 4
            java.util.Calendar.FRIDAY -> 5
            java.util.Calendar.SATURDAY -> 6
            java.util.Calendar.SUNDAY -> 7
            else -> 1
        }

        return when (habit.frequency) {
            HabitFrequency.DAILY -> true
            HabitFrequency.WEEKDAYS -> dayOfWeekIndex in 1..5
            HabitFrequency.WEEKENDS -> dayOfWeekIndex in 6..7
            HabitFrequency.CUSTOM_DAYS -> habit.frequencyDays.contains(dayOfWeekIndex)
            HabitFrequency.WEEKLY -> true
            HabitFrequency.MONTHLY -> true
        }
    }
}
