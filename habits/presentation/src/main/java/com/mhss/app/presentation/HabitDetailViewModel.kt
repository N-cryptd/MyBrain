package com.mhss.app.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhss.app.domain.model.Habit
import com.mhss.app.domain.model.HabitFrequency
import com.mhss.app.domain.model.Priority
import com.mhss.app.domain.use_case.DeleteHabitUseCase
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
    private val upsertHabit: UpsertHabitUseCase,
    private val deleteHabit: DeleteHabitUseCase,
    habitId: String? = null
) : ViewModel() {

    init {
        habitId?.let { loadHabit(it) }
    }

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
                    _uiState.value = _uiState.value.copy(saved = true, navigateUp = true)
                }
            }

            HabitDetailEvent.DeleteHabit -> {
                _uiState.value = _uiState.value.copy(deleteRequested = true)
            }

            HabitDetailEvent.NavigateUp -> {
                _uiState.value = _uiState.value.copy(navigateUp = true)
            }
        }
    }

    fun confirmDelete() {
        _uiState.value.habit?.let {
            viewModelScope.launch {
                deleteHabit(it)
            }
        }
        _uiState.value = _uiState.value.copy(navigateUp = true)
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
        val navigateUp: Boolean = false,
        val deleteRequested: Boolean = false,
        val snackbarHostState: SnackbarHostState = SnackbarHostState()
    )
}
