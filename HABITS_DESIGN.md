# Habits Module - Design and Specification Document

## 1. Overview and Goals

### 1.1 Purpose
This document outlines the comprehensive design and specification for implementing the Habits module in the MyBrain application. The Habits module will enable users to create, track, and maintain daily, weekly, or custom frequency habits with streak tracking and completion statistics.

### 1.2 Goals
- Provide a habit tracking system with flexible frequency options
- Enable streak calculation and visualization
- Support reminders for habit completion
- Integrate seamlessly with existing app architecture (Clean Architecture, MVI)
- Follow established patterns from Tasks and Notes modules
- Support data backup/restore via serialization

### 1.3 Key Features
- Create habits with customizable title, description, and priority
- Set frequency (daily, weekly, monthly, custom days)
- Track completion history with timestamps
- Calculate and display streak counts
- Set reminders with optional time-based notifications
- Search and filter habits by priority
- Display habit statistics (completion rate, best streak, total completions)

## 2. Entity Design

### 2.1 Habit Model (Domain Layer)

**File:** `/root/MyBrain/habits/domain/src/main/java/com/mhss/app/domain/model/Habit.kt`

```kotlin
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
    val frequencyDays: List<Int> = emptyList(), // For custom days (1-7 for weekly)
    val completedDates: List<Long> = emptyList(), // Timestamps of completions
    val streakCount: Int = 0,
    val bestStreak: Int = 0,
    val reminderEnabled: Boolean = false,
    val reminderTime: Long = 0L, // Time of day in millis
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
```

### 2.2 HabitEntity (Database Layer)

**File:** `/root/MyBrain/core/database/src/main/java/com/mhss/app/database/entity/HabitEntity.kt`

```kotlin
package com.mhss.app.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mhss.app.database.converters.IdSerializer
import com.mhss.app.domain.model.Habit
import com.mhss.app.domain.model.HabitFrequency
import com.mhss.app.domain.model.Priority
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "habits")
@Serializable
data class HabitEntity(
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String = "",
    @SerialName("priority")
    val priority: Int = Priority.LOW.value,
    @SerialName("createdDate")
    @ColumnInfo(name = "created_date")
    val createdDate: Long = 0L,
    @SerialName("updatedDate")
    @ColumnInfo(name = "updated_date")
    val updatedDate: Long = 0L,
    @SerialName("frequency")
    val frequency: Int = HabitFrequency.DAILY.value,
    @SerialName("frequencyDays")
    @ColumnInfo(name = "frequency_days")
    val frequencyDays: List<Int> = emptyList(),
    @SerialName("completedDates")
    @ColumnInfo(name = "completed_dates")
    val completedDates: List<Long> = emptyList(),
    @SerialName("streakCount")
    @ColumnInfo(name = "streak_count")
    val streakCount: Int = 0,
    @SerialName("bestStreak")
    @ColumnInfo(name = "best_streak")
    val bestStreak: Int = 0,
    @SerialName("reminderEnabled")
    @ColumnInfo(name = "reminder_enabled")
    val reminderEnabled: Boolean = false,
    @SerialName("reminderTime")
    @ColumnInfo(name = "reminder_time")
    val reminderTime: Long = 0L,
    @SerialName("alarmId")
    val alarmId: Int? = null,
    @SerialName("id")
    @PrimaryKey
    @Serializable(IdSerializer::class)
    val id: String
)

fun HabitEntity.toHabit() = Habit(
    title = title,
    description = description,
    priority = Priority.entries.firstOrNull { it.value == priority } ?: Priority.LOW,
    createdDate = createdDate,
    updatedDate = updatedDate,
    frequency = HabitFrequency.entries.firstOrNull { it.value == frequency } ?: HabitFrequency.DAILY,
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
```

### 2.3 Field Definitions and Types

| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| id | String | Unique identifier (UUID) | Auto-generated, required |
| title | String | Habit name | Non-blank, max 100 chars |
| description | String | Habit details | Max 500 chars |
| priority | Int | Priority level (0=Low, 1=Medium, 2=High) | Default: 0 |
| createdDate | Long | Creation timestamp (epoch millis) | Default: current time |
| updatedDate | Long | Last update timestamp | Default: current time |
| frequency | Int | Frequency type enum value | Required |
| frequencyDays | List<Int> | Days of week (1=Mon, 7=Sun) for custom frequency | Optional |
| completedDates | List<Long> | Completion timestamps | Optional |
| streakCount | Int | Current streak count | Default: 0, min: 0 |
| bestStreak | Int | Best streak achieved | Default: 0, min: 0 |
| reminderEnabled | Boolean | Is reminder active | Default: false |
| reminderTime | Long | Reminder time in millis | Default: 0 |
| alarmId | Int? | Alarm manager ID | Optional |

### 2.4 Validation Rules
- `title` must not be blank and must not exceed 100 characters
- `description` must not exceed 500 characters
- `streakCount` and `bestStreak` must be non-negative
- `frequencyDays` values must be in range 1-7 (for weekly-based frequencies)
- `completedDates` must be valid timestamps
- `reminderTime` must be in range 0-86399999 (0-23:59:59:999 in millis)

## 3. Database Schema

### 3.1 Table Structure

```sql
CREATE TABLE habits (
    id TEXT PRIMARY KEY NOT NULL,
    title TEXT NOT NULL,
    description TEXT NOT NULL DEFAULT '',
    priority INTEGER NOT NULL DEFAULT 0,
    created_date INTEGER NOT NULL DEFAULT 0,
    updated_date INTEGER NOT NULL DEFAULT 0,
    frequency INTEGER NOT NULL DEFAULT 0,
    frequency_days TEXT NOT NULL DEFAULT '',
    completed_dates TEXT NOT NULL DEFAULT '',
    streak_count INTEGER NOT NULL DEFAULT 0,
    best_streak INTEGER NOT NULL DEFAULT 0,
    reminder_enabled INTEGER NOT NULL DEFAULT 0,
    reminder_time INTEGER NOT NULL DEFAULT 0,
    alarmId INTEGER
);
```

### 3.2 Indexes

```sql
CREATE INDEX idx_habits_created_date ON habits(created_date);
CREATE INDEX idx_habits_priority ON habits(priority);
CREATE INDEX idx_habits_updated_date ON habits(updated_date);
```

### 3.3 Relationships
- Habits table is independent (no foreign keys)
- Links to `alarms` table via `alarmId` (optional, similar to tasks)

## 4. Migration Plan (v5 → v6)

### 4.1 Migration Object

**File:** `/root/MyBrain/core/database/src/main/java/com/mhss/app/database/migrations/RoomMigrations.kt`

Add the following migration:

```kotlin
val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Create habits table
        db.execSQL("""
            CREATE TABLE habits (
                id TEXT PRIMARY KEY NOT NULL,
                title TEXT NOT NULL,
                description TEXT NOT NULL DEFAULT '',
                priority INTEGER NOT NULL DEFAULT 0,
                created_date INTEGER NOT NULL DEFAULT 0,
                updated_date INTEGER NOT NULL DEFAULT 0,
                frequency INTEGER NOT NULL DEFAULT 0,
                frequency_days TEXT NOT NULL DEFAULT '',
                completed_dates TEXT NOT NULL DEFAULT '',
                streak_count INTEGER NOT NULL DEFAULT 0,
                best_streak INTEGER NOT NULL DEFAULT 0,
                reminder_enabled INTEGER NOT NULL DEFAULT 0,
                reminder_time INTEGER NOT NULL DEFAULT 0,
                alarmId INTEGER
            )
        """.trimIndent())

        // Create indexes
        db.execSQL("CREATE INDEX idx_habits_created_date ON habits(created_date)")
        db.execSQL("CREATE INDEX idx_habits_priority ON habits(priority)")
        db.execSQL("CREATE INDEX idx_habits_updated_date ON habits(updated_date)")
    }
}
```

### 4.2 Update Database Class

**File:** `/root/MyBrain/core/database/src/main/java/com/mhss/app/database/MyBrainDatabase.kt`

```kotlin
@Database(
    entities = [
        NoteEntity::class, 
        TaskEntity::class, 
        DiaryEntryEntity::class, 
        BookmarkEntity::class, 
        AlarmEntity::class, 
        NoteFolderEntity::class,
        HabitEntity::class  // Add this
    ],
    version = 6  // Update from 5 to 6
)
@TypeConverters(DBConverters::class)
abstract class MyBrainDatabase: RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun taskDao(): TaskDao
    abstract fun diaryDao(): DiaryDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun alarmDao(): AlarmDao
    abstract fun habitDao(): HabitDao  // Add this

    companion object {
        const val DATABASE_NAME = "by_brain_db"
    }
}
```

### 4.3 Update Database Module

**File:** `/root/MyBrain/core/database/src/main/java/com/mhss/app/database/di/DatabaseModule.kt`

Update migration array to include `MIGRATION_5_6`:
```kotlin
val migrationArray = arrayOf(
    MIGRATION_1_2,
    MIGRATION_2_3,
    MIGRATION_3_4,
    MIGRATION_4_5,
    MIGRATION_5_6  // Add this
)
```

## 5. Domain Layer Design

### 5.1 Repository Interface

**File:** `/root/MyBrain/habits/domain/src/main/java/com/mhss/app/domain/repository/HabitRepository.kt`

```kotlin
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
```

### 5.2 Use Cases

**File:** `/root/MyBrain/habits/domain/src/main/java/com/mhss/app/domain/use_case/GetAllHabitsUseCase.kt`

```kotlin
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
                        is Order.Priority -> habits.sortedBy { it.priority }
                        else -> habits.sortedBy { it.createdDate }
                    }
                }
                is OrderType.DESC -> {
                    when (order) {
                        is Order.Alphabetical -> habits.sortedByDescending { it.title }
                        is Order.DateCreated -> habits.sortedByDescending { it.createdDate }
                        is Order.DateModified -> habits.sortedByDescending { it.updatedDate }
                        is Order.Priority -> habits.sortedByDescending { it.priority }
                        else -> habits.sortedByDescending { it.createdDate }
                    }
                }
            }
        }.flowOn(defaultDispatcher)
    }
}
```

**File:** `/root/MyBrain/habits/domain/src/main/java/com/mhss/app/domain/use_case/UpsertHabitUseCase.kt`

```kotlin
package com.mhss.app.domain.use_case

import com.mhss.app.alarm.use_case.DeleteAlarmUseCase
import com.mhss.app.alarm.use_case.UpsertAlarmUseCase
import com.mhss.app.domain.model.Habit
import com.mhss.app.domain.repository.HabitRepository
import com.mhss.app.widget.WidgetUpdater
import org.koin.core.annotation.Single
import kotlin.time.Clock.System.now

@Single
class UpsertHabitUseCase(
    private val habitsRepository: HabitRepository,
    private val upsertAlarm: UpsertAlarmUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase,
    private val widgetUpdater: WidgetUpdater
) {
    suspend operator fun invoke(
        habit: Habit,
        previousHabit: Habit? = null,
        updateWidget: Boolean = true
    ): Boolean {
        val nowMillis = now().toEpochMilliseconds()
        val finalHabit = when {
            habit.reminderEnabled && habit.reminderTime != 0L -> {
                val alarmTime = getAlarmTimeForToday(habit.reminderTime)
                if (alarmTime > nowMillis) {
                    val alarmId = upsertAlarm(habit.alarmId ?: 0, alarmTime)
                    habit.copy(alarmId = alarmId)
                } else {
                    deleteAlarmUseCase(habit.alarmId ?: 0)
                    habit.copy(alarmId = null)
                }
            }
            !habit.reminderEnabled && previousHabit?.alarmId != null -> {
                deleteAlarmUseCase(previousHabit.alarmId)
                habit.copy(alarmId = null)
            }
            else -> habit
        }

        habitsRepository.upsertHabit(finalHabit)
        if (updateWidget) widgetUpdater.updateAll(WidgetUpdater.WidgetType.Habits)

        return finalHabit.alarmId != null || !finalHabit.reminderEnabled
    }

    private fun getAlarmTimeForToday(timeInMillis: Long): Long {
        val now = now().toEpochMilliseconds()
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = now
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis + timeInMillis
    }
}
```

**File:** `/root/MyBrain/habits/domain/src/main/java/com/mhss/app/domain/use_case/GetHabitsByPriorityUseCase.kt`

```kotlin
package com.mhss.app.domain.use_case

import com.mhss.app.domain.model.Habit
import com.mhss.app.domain.model.Priority
import com.mhss.app.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class GetHabitsByPriorityUseCase(
    private val habitsRepository: HabitRepository
) {
    operator fun invoke(priority: Priority): Flow<List<Habit>> {
        return habitsRepository.getHabitsByPriority(priority.value)
    }
}
```

**File:** `/root/MyBrain/habits/domain/src/main/java/com/mhss/app/domain/use_case/DeleteHabitUseCase.kt`

```kotlin
package com.mhss.app.domain.use_case

import com.mhss.app.alarm.use_case.DeleteAlarmUseCase
import com.mhss.app.domain.model.Habit
import com.mhss.app.domain.repository.HabitRepository
import com.mhss.app.widget.WidgetUpdater
import org.koin.core.annotation.Single

@Single
class DeleteHabitUseCase(
    private val habitsRepository: HabitRepository,
    private val deleteAlarmUseCase: DeleteAlarmUseCase,
    private val widgetUpdater: WidgetUpdater
) {
    suspend operator fun invoke(habit: Habit) {
        habit.alarmId?.let { deleteAlarmUseCase(it) }
        habitsRepository.deleteHabit(habit)
        widgetUpdater.updateAll(WidgetUpdater.WidgetType.Habits)
    }
}
```

**File:** `/root/MyBrain/habits/domain/src/main/java/com/mhss/app/domain/use_case/CompleteHabitUseCase.kt`

```kotlin
package com.mhss.app.domain.use_case

import com.mhss.app.domain.repository.HabitRepository
import org.koin.core.annotation.Single
import kotlin.time.Clock.System.now

@Single
class CompleteHabitUseCase(
    private val habitsRepository: HabitRepository
) {
    suspend operator fun invoke(habitId: String) {
        val timestamp = now().toEpochMilliseconds()
        habitsRepository.completeHabit(habitId, timestamp)
    }
}
```

**File:** `/root/MyBrain/habits/domain/src/main/java/com/mhss/app/domain/use_case/SearchHabitsUseCase.kt`

```kotlin
package com.mhss.app.domain.use_case

import com.mhss.app.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class SearchHabitsUseCase(
    private val habitsRepository: HabitRepository
) {
    operator fun invoke(query: String): Flow<List<com.mhss.app.domain.model.Habit>> {
        return habitsRepository.searchHabits(query)
    }
}
```

**File:** `/root/MyBrain/habits/domain/src/main/java/com/mhss/app/domain/use_case/GetHabitByIdUseCase.kt`

```kotlin
package com.mhss.app.domain.use_case

import com.mhss.app.domain.repository.HabitRepository
import org.koin.core.annotation.Single

@Single
class GetHabitByIdUseCase(
    private val habitsRepository: HabitRepository
) {
    suspend operator fun invoke(id: String): com.mhss.app.domain.model.Habit? {
        return habitsRepository.getHabitById(id)
    }
}
```

### 5.3 DI Module

**File:** `/root/MyBrain/habits/domain/src/main/java/com/mhss/app/domain/di/HabitsDomainModule.kt`

```kotlin
package com.mhss.app.domain.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan("com.mhss.app.domain")
class HabitsDomainModule
```

## 6. Data Layer Design

### 6.1 DAO Interface

**File:** `/root/MyBrain/core/database/src/main/java/com/mhss/app/database/dao/HabitDao.kt`

```kotlin
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
```

### 6.2 Repository Implementation

**File:** `/root/MyBrain/habits/data/src/main/java/com/mhss/app/data/HabitRepositoryImpl.kt`

```kotlin
package com.mhss.app.data

import com.mhss.app.database.dao.HabitDao
import com.mhss.app.database.entity.toHabit
import com.mhss.app.database.entity.toHabitEntity
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
            else -> 24 * 60 * 60 * 1000L // Default to daily
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
```

### 6.3 DI Module

**File:** `/root/MyBrain/habits/data/src/main/java/com/mhss/app/data/HabitsDataModule.kt`

```kotlin
package com.mhss.app.data

import com.mhss.app.domain.di.HabitsDomainModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.dsl.module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.mhss.app.data")
internal class HabitsDataModule

val habitsDataModule = module {
    includes(HabitsDataModule().module, HabitsDomainModule().module)
}
```

## 7. Presentation Layer Design

### 7.1 ViewModels

**File:** `/root/MyBrain/habits/presentation/src/main/java/com/mhss/app/presentation/HabitsViewModel.kt`

```kotlin
package com.mhss.app.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhss.app.domain.model.Habit
import com.mhss.app.domain.use_case.CompleteHabitUseCase
import com.mhss.app.domain.use_case.DeleteHabitUseCase
import com.mhss.app.domain.use_case.GetAllHabitsUseCase
import com.mhss.app.domain.use_case.SearchHabitsUseCase
import com.mhss.app.domain.use_case.UpsertHabitUseCase
import com.mhss.app.preferences.PrefsConstants
import com.mhss.app.preferences.domain.model.Order
import com.mhss.app.preferences.domain.model.intPreferencesKey
import com.mhss.app.preferences.domain.model.toOrder
import com.mhss.app.preferences.domain.use_case.GetPreferenceUseCase
import com.mhss.app.preferences.domain.use_case.SavePreferenceUseCase
import com.mhss.app.ui.R
import com.mhss.app.ui.snackbar.showSnackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HabitsViewModel(
    private val getAllHabits: GetAllHabitsUseCase,
    private val upsertHabit: UpsertHabitUseCase,
    private val deleteHabit: DeleteHabitUseCase,
    private val completeHabit: CompleteHabitUseCase,
    private val searchHabits: SearchHabitsUseCase,
    getPreference: GetPreferenceUseCase,
    private val savePreference: SavePreferenceUseCase
) : ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set

    private var getHabitsJob: Job? = null
    private var searchHabitsJob: Job? = null

    init {
        viewModelScope.launch {
            getPreference(
                intPreferencesKey(PrefsConstants.HABITS_ORDER_KEY),
                Order.DateCreated(OrderType.DESC).toInt()
            ).collect { order ->
                getHabits(order.toOrder())
            }
        }
    }

    fun onEvent(event: HabitEvent) {
        when (event) {
            is HabitEvent.AddHabit -> {
                viewModelScope.launch {
                    if (event.habit.title.isNotBlank()) {
                        upsertHabit(event.habit)
                    } else {
                        uiState.snackbarHostState.showSnackbar(R.string.error_empty_title)
                    }
                }
            }

            is HabitEvent.UpdateHabit -> {
                viewModelScope.launch {
                    upsertHabit(event.habit, event.previousHabit)
                }
            }

            is HabitEvent.DeleteHabit -> {
                viewModelScope.launch {
                    deleteHabit(event.habit)
                }
            }

            is HabitEvent.CompleteHabit -> {
                viewModelScope.launch {
                    completeHabit(event.habitId)
                }
            }

            is HabitEvent.UpdateOrder -> viewModelScope.launch {
                savePreference(
                    intPreferencesKey(PrefsConstants.HABITS_ORDER_KEY),
                    event.order.toInt()
                )
            }

            is HabitEvent.SearchHabits -> {
                viewModelScope.launch {
                    searchHabits(event.query)
                }
            }
        }
    }

    data class UiState(
        val habits: List<Habit> = emptyList(),
        val habitOrder: Order = Order.DateCreated(OrderType.DESC),
        val searchHabits: List<Habit> = emptyList(),
        val snackbarHostState: SnackbarHostState = SnackbarHostState()
    )

    private fun getHabits(order: Order) {
        getHabitsJob?.cancel()
        getHabitsJob = getAllHabits(order).onEach { habits ->
            uiState = uiState.copy(
                habits = habits,
                habitOrder = order
            )
        }.launchIn(viewModelScope)
    }

    private fun searchHabits(query: String) {
        searchHabitsJob?.cancel()
        searchHabitsJob = searchHabits(query).onEach { habits ->
            uiState = uiState.copy(
                searchHabits = habits
            )
        }.launchIn(viewModelScope)
    }
}
```

**File:** `/root/MyBrain/habits/presentation/src/main/java/com/mhss/app/presentation/HabitDetailViewModel.kt`

```kotlin
package com.mhss.app.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhss.app.domain.model.Habit
import com.mhss.app.domain.model.HabitFrequency
import com.mhss.app.domain.model.Priority
import com.mhss.app.domain.use_case.GetHabitByIdUseCase
import com.mhss.app.domain.use_case.UpsertHabitUseCase
import com.mhss.app.ui.R
import com.mhss.app.ui.snackbar.showSnackbar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HabitDetailViewModel(
    private val getHabitById: GetHabitByIdUseCase,
    private val upsertHabit: UpsertHabitUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun loadHabit(habitId: String) {
        viewModelScope.launch {
            val habit = getHabitById(habitId)
            _uiState.value = _uiState.value.copy(
                habit = habit,
                title = habit?.title ?: "",
                description = habit?.description ?: "",
                priority = habit?.priority ?: Priority.LOW,
                frequency = habit?.frequency ?: HabitFrequency.DAILY,
                frequencyDays = habit?.frequencyDays ?: emptyList(),
                reminderEnabled = habit?.reminderEnabled ?: false,
                reminderTime = habit?.reminderTime ?: 0L
            )
        }
    }

    fun onEvent(event: HabitDetailEvent) {
        when (event) {
            is HabitDetailEvent.UpdateTitle -> {
                _uiState.value = _uiState.value.copy(title = event.title)
            }

            is HabitDetailEvent.UpdateDescription -> {
                _uiState.value = _uiState.value.copy(description = event.description)
            }

            is HabitDetailEvent.UpdatePriority -> {
                _uiState.value = _uiState.value.copy(priority = event.priority)
            }

            is HabitDetailEvent.UpdateFrequency -> {
                _uiState.value = _uiState.value.copy(
                    frequency = event.frequency,
                    frequencyDays = if (event.frequency != HabitFrequency.CUSTOM_DAYS) emptyList() else _uiState.value.frequencyDays
                )
            }

            is HabitDetailEvent.UpdateFrequencyDays -> {
                _uiState.value = _uiState.value.copy(frequencyDays = event.days)
            }

            is HabitDetailEvent.UpdateReminderEnabled -> {
                _uiState.value = _uiState.value.copy(reminderEnabled = event.enabled)
            }

            is HabitDetailEvent.UpdateReminderTime -> {
                _uiState.value = _uiState.value.copy(reminderTime = event.time)
            }

            is HabitDetailEvent.SaveHabit -> {
                viewModelScope.launch {
                    if (_uiState.value.title.isBlank()) {
                        _uiState.value.snackbarHostState.showSnackbar(R.string.error_empty_title)
                        return@launch
                    }

                    val habit = Habit(
                        title = _uiState.value.title,
                        description = _uiState.value.description,
                        priority = _uiState.value.priority,
                        createdDate = _uiState.value.habit?.createdDate ?: System.currentTimeMillis(),
                        updatedDate = System.currentTimeMillis(),
                        frequency = _uiState.value.frequency,
                        frequencyDays = _uiState.value.frequencyDays,
                        completedDates = _uiState.value.habit?.completedDates ?: emptyList(),
                        streakCount = _uiState.value.habit?.streakCount ?: 0,
                        bestStreak = _uiState.value.habit?.bestStreak ?: 0,
                        reminderEnabled = _uiState.value.reminderEnabled,
                        reminderTime = _uiState.value.reminderTime,
                        alarmId = _uiState.value.habit?.alarmId,
                        id = _uiState.value.habit?.id ?: ""
                    )

                    upsertHabit(habit, _uiState.value.habit)
                    _uiState.value = _uiState.value.copy(saved = true)
                }
            }
        }
    }

    data class UiState(
        val habit: Habit? = null,
        val title: String = "",
        val description: String = "",
        val priority: Priority = Priority.LOW,
        val frequency: HabitFrequency = HabitFrequency.DAILY,
        val frequencyDays: List<Int> = emptyList(),
        val reminderEnabled: Boolean = false,
        val reminderTime: Long = 0L,
        val saved: Boolean = false,
        val snackbarHostState: SnackbarHostState = SnackbarHostState()
    )
}
```

### 7.2 UI Events

**File:** `/root/MyBrain/habits/presentation/src/main/java/com/mhss/app/presentation/HabitEvent.kt`

```kotlin
package com.mhss.app.presentation

import com.mhss.app.domain.model.Habit
import com.mhss.app.preferences.domain.model.Order

sealed interface HabitEvent {
    data class AddHabit(val habit: Habit) : HabitEvent
    data class UpdateHabit(val habit: Habit, val previousHabit: Habit? = null) : HabitEvent
    data class DeleteHabit(val habit: Habit) : HabitEvent
    data class CompleteHabit(val habitId: String) : HabitEvent
    data class UpdateOrder(val order: Order) : HabitEvent
    data class SearchHabits(val query: String) : HabitEvent
}

sealed interface HabitDetailEvent {
    data class UpdateTitle(val title: String) : HabitDetailEvent
    data class UpdateDescription(val description: String) : HabitDetailEvent
    data class UpdatePriority(val priority: com.mhss.app.domain.model.Priority) : HabitDetailEvent
    data class UpdateFrequency(val frequency: com.mhss.app.domain.model.HabitFrequency) : HabitDetailEvent
    data class UpdateFrequencyDays(val days: List<Int>) : HabitDetailEvent
    data class UpdateReminderEnabled(val enabled: Boolean) : HabitDetailEvent
    data class UpdateReminderTime(val time: Long) : HabitDetailEvent
    object SaveHabit : HabitDetailEvent
}
```

### 7.3 DI Module

**File:** `/root/MyBrain/habits/presentation/src/main/java/com/mhss/app/presentation/di/HabitsPresentationModule.kt`

```kotlin
package com.mhss.app.presentation.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan("com.mhss.app.presentation")
class HabitsPresentationModule
```

## 8. UI/UX Specifications

### 8.1 Screens

#### HabitsScreen
- Displays list of habits with search functionality
- Shows habit cards with title, description, streak count, priority indicator
- Filter by priority option
- FAB to add new habit
- Swipe to complete habit
- Tap habit to view/edit details

**File:** `/root/MyBrain/habits/presentation/src/main/java/com/mhss/app/presentation/HabitsScreen.kt`

#### HabitDetailScreen
- Form to create/edit habit
- Title input (required)
- Description input (optional)
- Priority selector
- Frequency selector
- Custom days selector (for custom frequency)
- Reminder toggle and time picker
- Save/Cancel buttons

**File:** `/root/MyBrain/habits/presentation/src/main/java/com/mhss/app/presentation/HabitDetailScreen.kt`

#### HabitStatisticsScreen (Optional)
- Shows completion statistics
- Calendar view of completions
- Streak visualization
- Best streak highlight

**File:** `/root/MyBrain/habits/presentation/src/main/java/com/mhss/app/presentation/HabitStatisticsScreen.kt`

### 8.2 UI Components

**File:** `/root/MyBrain/core/ui/src/main/java/com/mhss/app/ui/components/habits/HabitCard.kt`

```kotlin
package com.mhss.app.ui.components.habits

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mhss.app.domain.model.Habit
import com.mhss.app.domain.model.Priority
import com.mhss.app.ui.R
import com.mhss.app.util.date.formatDateDependingOnDay

@Composable
fun HabitCard(
    modifier: Modifier = Modifier,
    habit: Habit,
    onClick: (Habit) -> Unit,
    onComplete: (String) -> Unit = {},
    isCompletedToday: Boolean = false
) {
    val context = LocalContext.current
    val formattedDate by remember(habit.updatedDate) {
        derivedStateOf { habit.updatedDate.formatDateDependingOnDay(context) }
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        onClick = { onClick(habit) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isCompletedToday,
                onCheckedChange = { onComplete(habit.id) }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PriorityIndicator(priority = habit.priority)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = habit.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (habit.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = habit.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_fire),
                        contentDescription = null,
                        tint = Color(0xFFFF6B35),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${habit.streakCount} day streak",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun PriorityIndicator(priority: Priority) {
    val color = when (priority) {
        Priority.HIGH -> Color.Red
        Priority.MEDIUM -> Color(0xFFFFA500)
        Priority.LOW -> Color.Green
    }

    Icon(
        painter = painterResource(R.drawable.ic_priority),
        contentDescription = null,
        tint = color,
        modifier = Modifier.size(12.dp)
    )
}
```

**File:** `/root/MyBrain/core/ui/src/main/java/com/mhss/app/ui/components/habits/HabitFrequencySelector.kt`

```kotlin
package com.mhss.app.ui.components.habits

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mhss.app.domain.model.HabitFrequency
import com.mhss.app.ui.R

@Composable
fun HabitFrequencySelector(
    selectedFrequency: HabitFrequency,
    onFrequencyChange: (HabitFrequency) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.frequency),
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            HabitFrequency.values().forEach { frequency ->
                FilterChip(
                    selected = selectedFrequency == frequency,
                    onClick = { onFrequencyChange(frequency) },
                    label = {
                        Text(
                            text = when (frequency) {
                                HabitFrequency.DAILY -> stringResource(R.string.daily)
                                HabitFrequency.WEEKLY -> stringResource(R.string.weekly)
                                HabitFrequency.CUSTOM_DAYS -> stringResource(R.string.custom)
                                HabitFrequency.WEEKDAYS -> stringResource(R.string.weekdays)
                                HabitFrequency.WEEKENDS -> stringResource(R.string.weekends)
                                HabitFrequency.MONTHLY -> stringResource(R.string.monthly)
                            }
                        )
                    },
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        }
    }
}
```

**File:** `/root/MyBrain/core/ui/src/main/java/com/mhss/app/ui/components/habits/HabitStatsCard.kt`

```kotlin
package com.mhss.app.ui.components.habits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mhss.app.domain.model.Habit
import com.mhss.app.ui.R

@Composable
fun HabitStatsCard(
    modifier: Modifier = Modifier,
    habit: Habit
) {
    val completionRate = if (habit.completedDates.isNotEmpty()) {
        val totalDays = ((System.currentTimeMillis() - habit.createdDate) / (24 * 60 * 60 * 1000)).toInt() + 1
        (habit.completedDates.size * 100 / totalDays)
    } else 0

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.statistics),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatRow(
                icon = R.drawable.ic_fire,
                label = stringResource(R.string.current_streak),
                value = "${habit.streakCount} ${stringResource(R.string.days)}"
            )

            StatRow(
                icon = R.drawable.ic_trophy,
                label = stringResource(R.string.best_streak),
                value = "${habit.bestStreak} ${stringResource(R.string.days)}"
            )

            StatRow(
                icon = R.drawable.ic_check_circle,
                label = stringResource(R.string.total_completions),
                value = "${habit.completedDates.size}"
            )

            StatRow(
                icon = R.drawable.ic_percent,
                label = stringResource(R.string.completion_rate),
                value = "$completionRate%"
            )
        }
    }
}

@Composable
private fun StatRow(
    icon: Int,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Color(0xFFFF6B35),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}
```

## 9. Integration Plan

### 9.1 Navigation Routes

**File:** `/root/MyBrain/core/ui/src/main/java/com/mhss/app/ui/navigation/Screen.kt`

Add to existing Screen sealed class:

```kotlin
sealed class Screen(val route: String) {
    // ... existing screens ...
    object Habits : Screen("habits")
    object HabitDetail : Screen("habit_detail/{habitId}") {
        fun createRoute(habitId: String = "") = "habit_detail/$habitId"
    }
    object HabitsSearch : Screen("habits_search")
}
```

### 9.2 Bottom Bar Tab

**File:** Add to `MainActivity` or navigation graph for Habits tab

```kotlin
// In the navigation graph or bottom bar setup
BottomNavigationItem(
    icon = { Icon(painterResource(R.drawable.ic_habits), contentDescription = null) },
    label = { Text(stringResource(R.string.habits)) },
    selected = selectedRoute == Screen.Habits.route,
    onClick = { navController.navigate(Screen.Habits.route) }
)
```

### 9.3 DI Integration

**File:** Update `/root/MyBrain/app/src/main/java/com/mhss/app/di/AppModule.kt`

```kotlin
val appModule = module {
    includes(
        tasksDataModule,
        habitsDataModule,  // Add this
        notesDataModule,
        // ... other modules
    )
}
```

### 9.4 Build Configuration

**File:** `/root/MyBrain/settings.gradle`

```gradle
include ':habits:domain'
include ':habits:data'
include ':habits:presentation'
```

**File:** `/root/MyBrain/app/build.gradle.kts`

```kotlin
dependencies {
    implementation(project(":tasks:domain"))
    implementation(project(":tasks:presentation"))
    implementation(project(":habits:domain"))  // Add
    implementation(project(":habits:presentation"))  // Add
    implementation(project(":notes:domain"))
    implementation(project(":notes:presentation"))
    // ... other dependencies
}
```

## 10. Implementation Phases

### Phase 1: Foundation (Estimated: 4-6 hours)
- Create domain model (`Habit.kt`, enums)
- Create database entity (`HabitEntity.kt`)
- Set up database migration (MIGRATION_5_6)
- Update database class to include habits
- Create DAO interface (`HabitDao.kt`)
- Create repository interface (`HabitRepository.kt`)

**Deliverables:**
- Entity and model classes
- Database schema
- Migration code
- DAO and repository interfaces

### Phase 2: Data Layer (Estimated: 4-5 hours)
- Implement `HabitRepositoryImpl`
- Create use cases:
  - GetAllHabitsUseCase
  - UpsertHabitUseCase
  - DeleteHabitUseCase
  - CompleteHabitUseCase
  - SearchHabitsUseCase
  - GetHabitsByPriorityUseCase
  - GetHabitByIdUseCase
- Set up DI modules (HabitsDataModule, HabitsDomainModule)

**Deliverables:**
- Repository implementation
- All use cases
- DI configuration

### Phase 3: Presentation Layer - ViewModel (Estimated: 3-4 hours)
- Create `HabitsViewModel`
- Create `HabitDetailViewModel`
- Define UI events (`HabitEvent`, `HabitDetailEvent`)
- Set up presentation DI module

**Deliverables:**
- ViewModels
- Event classes
- DI configuration

### Phase 4: UI Components (Estimated: 5-6 hours)
- Create `HabitCard` component
- Create `HabitFrequencySelector` component
- Create `HabitStatsCard` component
- Create `HabitPrioritySelector` component
- Create reminder/time picker components
- Add necessary string resources

**Deliverables:**
- All UI components
- String resources
- Icons/drawables

### Phase 5: Screens (Estimated: 6-8 hours)
- Create `HabitsScreen` with list view
- Create `HabitDetailScreen` with form
- Create `HabitStatisticsScreen` (optional)
- Implement search functionality
- Add animations and transitions

**Deliverables:**
- All screen implementations
- Navigation integration

### Phase 6: Integration (Estimated: 3-4 hours)
- Add navigation routes
- Add bottom bar tab
- Update main navigation graph
- Connect to alarm system
- Update widget support
- Update preferences constants

**Deliverables:**
- Navigation working
- Bottom bar integration
- Alarm notifications

### Phase 7: Testing & Polish (Estimated: 4-5 hours)
- Unit tests for use cases
- Unit tests for repository
- UI testing
- Edge case handling
- Performance optimization
- Bug fixes

**Deliverables:**
- Test coverage
- Bug-free implementation

### Phase 8: Documentation (Estimated: 2-3 hours)
- Update user documentation
- Add comments to code
- Create usage examples

**Deliverables:**
- Updated documentation
- Code comments

**Total Estimated Effort: 31-41 hours (4-5 days)**

## 11. Testing Strategy

### 11.1 Unit Tests

**Domain Layer Tests:**
- Test use case logic
- Test streak calculation
- Test frequency handling
- Test habit completion/uncompletion

**Files to test:**
- `GetAllHabitsUseCaseTest.kt`
- `UpsertHabitUseCaseTest.kt`
- `CompleteHabitUseCaseTest.kt`
- `HabitRepositoryImplTest.kt`

### 11.2 Integration Tests

**Data Layer Tests:**
- Test DAO operations
- Test repository with mock DAO
- Test migration

**Files to test:**
- `HabitDaoTest.kt`
- `HabitRepositoryIntegrationTest.kt`
- `MigrationTest.kt`

### 11.3 UI Tests

**Presentation Layer Tests:**
- Test ViewModel state management
- Test UI events
- Test screen navigation

**Files to test:**
- `HabitsViewModelTest.kt`
- `HabitDetailViewModelTest.kt`
- `HabitsScreenTest.kt`

### 11.4 Manual Testing Checklist

- [ ] Create habit with all frequencies
- [ ] Test streak calculation accuracy
- [ ] Test reminder notifications
- [ ] Test search functionality
- [ ] Test priority filtering
- [ ] Test habit completion/uncompletion
- [ ] Test database migration
- [ ] Test backup/restore
- [ ] Test widget updates
- [ ] Test edge cases (empty title, invalid dates)

## 12. Additional Considerations

### 12.1 Performance
- Use Flow for reactive data streams
- Implement proper indexing in database
- Lazy load habits list for large datasets
- Cache calculation results

### 12.2 Security
- Validate all user inputs
- Sanitize database queries (Room handles this)
- Secure alarm IDs

### 12.3 Accessibility
- Add content descriptions to all icons
- Support screen readers
- Proper contrast ratios
- Keyboard navigation support

### 12.4 Internationalization
- Extract all strings to resources
- Support RTL layouts
- Locale-aware date formatting

### 12.5 Future Enhancements
- Habit categories/tags
- Habit templates
- Social features (share progress)
- Analytics dashboards
- Export/import habits
- Calendar integration
- Cross-device sync

## 13. File Structure Summary

```
/root/MyBrain/
├── habits/
│   ├── domain/
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/mhss/app/domain/
│   │       ├── model/
│   │       │   └── Habit.kt
│   │       ├── repository/
│   │       │   └── HabitRepository.kt
│   │       ├── use_case/
│   │       │   ├── GetAllHabitsUseCase.kt
│   │       │   ├── UpsertHabitUseCase.kt
│   │       │   ├── DeleteHabitUseCase.kt
│   │       │   ├── CompleteHabitUseCase.kt
│   │       │   ├── SearchHabitsUseCase.kt
│   │       │   ├── GetHabitsByPriorityUseCase.kt
│   │       │   └── GetHabitByIdUseCase.kt
│   │       └── di/
│   │           └── HabitsDomainModule.kt
│   ├── data/
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/mhss/app/data/
│   │       ├── HabitRepositoryImpl.kt
│   │       └── HabitsDataModule.kt
│   └── presentation/
│       ├── build.gradle.kts
│       └── src/main/java/com/mhss/app/presentation/
│           ├── HabitsScreen.kt
│           ├── HabitDetailScreen.kt
│           ├── HabitStatisticsScreen.kt
│           ├── HabitsViewModel.kt
│           ├── HabitDetailViewModel.kt
│           ├── HabitEvent.kt
│           └── di/
│               └── HabitsPresentationModule.kt
├── core/
│   ├── database/
│   │   ├── src/main/java/com/mhss/app/database/
│   │   │   ├── entity/
│   │   │   │   └── HabitEntity.kt
│   │   │   ├── dao/
│   │   │   │   └── HabitDao.kt
│   │   │   ├── migrations/
│   │   │   │   └── RoomMigrations.kt (update)
│   │   │   └── MyBrainDatabase.kt (update)
│   └── ui/
│       └── src/main/java/com/mhss/app/ui/components/habits/
│           ├── HabitCard.kt
│           ├── HabitFrequencySelector.kt
│           ├── HabitPrioritySelector.kt
│           └── HabitStatsCard.kt
└── settings.gradle (update)
```

## 14. Dependencies

### habits/domain/build.gradle.kts
```kotlin
plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(project(":core:preferences"))
    implementation(project(":core:alarm"))
    implementation(project(":core:widget"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.uuid)
    implementation(libs.kotlinx.serialization.json)

    implementation(platform(libs.koin.bom))
    implementation(libs.bundles.koin)
    ksp(libs.koin.ksp.compiler)
}
```

### habits/data/build.gradle.kts
```kotlin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.mhss.app.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
    }
}

dependencies {
    implementation(project(":core:database"))
    implementation(project(":habits:domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(platform(libs.koin.bom))
    implementation(libs.bundles.koin)
    implementation(libs.koin.android)
    ksp(libs.koin.ksp.compiler)

    implementation(libs.kotlinx.serialization.json)
}
```

### habits/presentation/build.gradle.kts
```kotlin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose.compiler)
}

android {
    namespace = "com.mhss.app.presentation"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":habits:domain"))
    implementation(project(":core:util"))
    implementation(project(":core:ui"))
    implementation(project(":core:preferences"))

    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(platform(libs.koin.bom))
    implementation(libs.bundles.koin)
    implementation(libs.koin.android)
    ksp(libs.koin.ksp.compiler)
}
```

---

**Document Version:** 1.0  
**Last Updated:** 2026-02-19  
**Author:** System Design Team  
**Status:** Ready for Implementation
