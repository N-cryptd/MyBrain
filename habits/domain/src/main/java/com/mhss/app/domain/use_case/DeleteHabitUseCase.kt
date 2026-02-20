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
