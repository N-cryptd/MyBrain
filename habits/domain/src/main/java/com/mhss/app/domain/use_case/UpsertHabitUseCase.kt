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
