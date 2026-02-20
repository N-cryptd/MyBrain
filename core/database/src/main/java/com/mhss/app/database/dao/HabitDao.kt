package com.mhss.app.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.mhss.app.database.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits")
    suspend fun getAllFullHabits(): List<HabitEntity>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabit(id: String): HabitEntity?

    @Query("SELECT * FROM habits WHERE alarmId = :alarmId")
    suspend fun getHabitByAlarm(alarmId: Int): HabitEntity?

    @Query("SELECT * FROM habits WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchHabits(query: String): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE priority = :priority")
    fun getHabitsByPriority(priority: Int): Flow<List<HabitEntity>>

    @Upsert
    suspend fun upsertHabit(habit: HabitEntity)

    @Upsert
    suspend fun upsertHabits(habits: List<HabitEntity>)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("UPDATE habits SET streak_count = :count WHERE id = :id")
    suspend fun updateStreakCount(id: String, count: Int)

    @Query("UPDATE habits SET best_streak = :count WHERE id = :id")
    suspend fun updateBestStreak(id: String, count: Int)

    @Query("UPDATE habits SET completed_dates = completed_dates || :timestamp WHERE id = :id")
    suspend fun addCompletedDate(id: String, timestamp: Long)

    @Query("UPDATE habits SET completed_dates = (
        SELECT CASE 
            WHEN json_each.value = :timestamp THEN NULL 
            ELSE json_each.value 
        END 
        FROM json_each(completed_dates) 
        WHERE json_each.value != :timestamp
    ) WHERE id = :id")
    suspend fun removeCompletedDate(id: String, timestamp: Long)
}
