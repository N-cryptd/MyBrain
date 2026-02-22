package com.mhss.app.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mhss.app.database.converters.IdSerializer
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
    val priority: Int = 0,
    @SerialName("createdDate")
    @ColumnInfo(name = "created_date")
    val createdDate: Long = 0L,
    @SerialName("updatedDate")
    @ColumnInfo(name = "updated_date")
    val updatedDate: Long = 0L,
    @SerialName("frequency")
    val frequency: Int = 0,
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
