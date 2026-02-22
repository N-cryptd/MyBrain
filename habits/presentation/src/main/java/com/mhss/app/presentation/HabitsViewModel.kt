package com.mhss.app.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhss.app.domain.model.Habit
import com.mhss.app.domain.use_case.CompleteHabitUseCase
import com.mhss.app.domain.use_case.DeleteHabitUseCase
import com.mhss.app.domain.use_case.GetAllHabitsUseCase
import com.mhss.app.domain.use_case.SearchHabitsUseCase
import com.mhss.app.domain.use_case.UncompleteHabitUseCase
import com.mhss.app.domain.use_case.UpsertHabitUseCase
import com.mhss.app.preferences.PrefsConstants
import com.mhss.app.preferences.domain.model.Order
import com.mhss.app.preferences.domain.model.OrderType
import com.mhss.app.preferences.domain.model.intPreferencesKey
import com.mhss.app.preferences.domain.model.toInt
import com.mhss.app.preferences.domain.model.toOrder
import com.mhss.app.preferences.domain.use_case.GetPreferenceUseCase
import com.mhss.app.preferences.domain.use_case.SavePreferenceUseCase
import com.mhss.app.ui.R
import com.mhss.app.ui.snackbar.showSnackbar
import kotlinx.coroutines.Job
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
    private val uncompleteHabit: UncompleteHabitUseCase,
    private val searchHabitsUseCase: SearchHabitsUseCase,
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

    fun onEvent(event: HabitsScreenEvent) {
        when (event) {
            is HabitsScreenEvent.Navigate -> {
                uiState = uiState.copy(navigateToDetail = event.habitId)
            }

            is HabitsScreenEvent.DeleteHabit -> {
                viewModelScope.launch {
                    deleteHabit(event.habit)
                }
            }

            is HabitsScreenEvent.CompleteHabit -> {
                viewModelScope.launch {
                    completeHabit(event.habitId)
                }
            }

            is HabitsScreenEvent.UpdateStreak -> {
                viewModelScope.launch {
                    val habit = uiState.habits.find { it.id == event.habitId }
                    habit?.copy(streakCount = event.count)?.let { upsertHabit(it) }
                }
            }

            is HabitsScreenEvent.UpdateOrder -> viewModelScope.launch {
                savePreference(
                    intPreferencesKey(PrefsConstants.HABITS_ORDER_KEY),
                    event.order.toInt()
                )
            }

            is HabitsScreenEvent.SearchHabits -> {
                viewModelScope.launch {
                    searchHabits(event.query)
                }
            }

            is HabitsScreenEvent.ShowDetails -> {
                uiState = uiState.copy(navigateToDetail = event.habitId)
            }
        }
    }

    fun completeHabitToday(habit: Habit, completedToday: Boolean) {
        viewModelScope.launch {
            if (completedToday) {
                uncompleteHabit(habit.id, System.currentTimeMillis())
            } else {
                completeHabit(habit.id)
            }
        }
    }

    data class UiState(
        val habits: List<Habit> = emptyList(),
        val habitOrder: Order = Order.DateCreated(OrderType.DESC),
        val searchHabits: List<Habit> = emptyList(),
        val snackbarHostState: SnackbarHostState = SnackbarHostState(),
        val navigateToDetail: String? = null
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
        searchHabitsJob = searchHabitsUseCase(query).onEach { habits ->
            uiState = uiState.copy(
                searchHabits = habits
            )
        }.launchIn(viewModelScope)
    }
}
