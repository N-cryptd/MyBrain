package com.mhss.app.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.navigation.NavHostController
import com.mhss.app.domain.model.HabitFrequency
import com.mhss.app.domain.model.Priority
import com.mhss.app.ui.R
import com.mhss.app.presentation.util.color
import com.mhss.app.ui.components.common.DateTimeDialog
import com.mhss.app.ui.components.common.MyBrainAppBar
import com.mhss.app.ui.components.common.TimePickerDialog
import com.mhss.app.ui.snackbar.LocalisedSnackbarHost
import com.mhss.app.util.permissions.Permission
import com.mhss.app.util.permissions.rememberPermissionState
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Suppress("AssignedValueIsNeverRead")
@Composable
fun HabitDetailScreen(
    navController: NavHostController,
    habitId: String?,
    viewModel: HabitDetailViewModel = koinViewModel { parametersOf(habitId) },
) {
    val alarmPermissionState = rememberPermissionState(Permission.SCHEDULE_ALARMS)
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = uiState.snackbarHostState
    var openDialog by rememberSaveable { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.LOW) }
    var frequency by remember { mutableStateOf(HabitFrequency.DAILY) }
    var frequencyDays by remember { mutableStateListOf<Int>() }
    var reminderEnabled by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableLongStateOf(0L) }

    val priorities = listOf(Priority.LOW, Priority.MEDIUM, Priority.HIGH)
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    LaunchedEffect(uiState.habit) {
        val habit = uiState.habit
        if (habit != null) {
            title = habit.title
            description = habit.description
            priority = habit.priority
            frequency = habit.frequency
            frequencyDays.clear()
            frequencyDays.addAll(habit.frequencyDays)
            reminderEnabled = habit.reminderEnabled
            reminderTime = habit.reminderTime
        }
    }

    LaunchedEffect(uiState.navigateUp, uiState.alarmError) {
        if (uiState.navigateUp) {
            openDialog = false
            navController.navigateUp()
        }
    }

    LifecycleStartEffect(Unit) {
        onStopOrDispose {
            if (!viewModel.uiState.value.navigateUp && title.isNotBlank()) {
                val habit = uiState.habit?.copy(
                    title = title,
                    description = description,
                    priority = priority,
                    frequency = frequency,
                    frequencyDays = frequencyDays,
                    reminderEnabled = reminderEnabled,
                    reminderTime = reminderTime,
                    updatedDate = System.currentTimeMillis()
                ) ?: com.mhss.app.domain.model.Habit(
                    title = title,
                    description = description,
                    priority = priority,
                    frequency = frequency,
                    frequencyDays = frequencyDays,
                    reminderEnabled = reminderEnabled,
                    reminderTime = reminderTime
                )
                viewModel.onEvent(HabitDetailEvent.SaveHabit(habit))
            }
        }
    }

    Scaffold(
        snackbarHost = { LocalisedSnackbarHost(snackbarHostState) },
        topBar = {
            MyBrainAppBar(
                title = "",
                actions = {
                    IconButton(onClick = { openDialog = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = stringResource(R.string.delete_habit)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        HabitDetailsContent(
            modifier = Modifier.padding(paddingValues),
            title = title,
            description = description,
            priority = priority,
            frequency = frequency,
            frequencyDays = frequencyDays,
            reminderEnabled = reminderEnabled,
            reminderTime = reminderTime,
            priorities = priorities,
            daysOfWeek = daysOfWeek,
            onTitleChange = { title = it },
            onDescriptionChange = { description = it },
            onPriorityChange = { priority = it },
            onFrequencyChange = { frequency = it },
            onFrequencyDayToggle = { day ->
                if (frequencyDays.contains(day)) {
                    frequencyDays.remove(day)
                } else {
                    frequencyDays.add(day)
                }
            },
            onReminderEnabledChange = { reminderEnabled = it },
            onReminderTimeChange = { reminderTime = it }
        )
    }

    if (openDialog) {
        AlertDialog(
            shape = RoundedCornerShape(25.dp),
            onDismissRequest = { openDialog = false },
            title = { Text(stringResource(R.string.delete_habit_confirmation_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.delete_habit_confirmation_message,
                        uiState.habit?.title ?: "Untitled"
                    )
                )
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(25.dp),
                    onClick = {
                        viewModel.onEvent(HabitDetailEvent.DeleteHabit)
                        viewModel.confirmDelete()
                        openDialog = false
                    },
                ) {
                    Text(stringResource(R.string.delete_habit), color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    shape = RoundedCornerShape(25.dp),
                    onClick = {
                        openDialog = false
                    }) {
                    Text(stringResource(R.string.cancel), color = Color.White)
                }
            }
        )
    }
}

@Composable
fun HabitDetailsContent(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    priority: Priority,
    frequency: HabitFrequency,
    frequencyDays: List<Int>,
    reminderEnabled: Boolean,
    reminderTime: Long,
    priorities: List<Priority>,
    daysOfWeek: List<String>,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriorityChange: (Priority) -> Unit,
    onFrequencyChange: (HabitFrequency) -> Unit,
    onFrequencyDayToggle: (Int) -> Unit,
    onReminderEnabledChange: (Boolean) -> Unit,
    onReminderTimeChange: (Long) -> Unit
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text(text = stringResource(R.string.title)) },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.priority),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(Modifier.height(12.dp))
        PriorityTabRow(
            priorities = priorities,
            selectedPriority = priority,
            onChange = onPriorityChange
        )
        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.frequency),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(Modifier.height(12.dp))
        FrequencyDropDown(
            selectedFrequency = frequency,
            onFrequencyChange = onFrequencyChange
        )
        Spacer(Modifier.height(12.dp))

        AnimatedVisibility(visible = frequency == HabitFrequency.CUSTOM_DAYS) {
            Column {
                Text(
                    text = stringResource(R.string.select_days),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    daysOfWeek.forEachIndexed { index, day ->
                        DayChip(
                            day = day,
                            selected = frequencyDays.contains(index + 1),
                            onClick = { onFrequencyDayToggle(index + 1) }
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = reminderEnabled,
                onCheckedChange = onReminderEnabledChange
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.reminder),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        var showTimeDialog by remember { mutableStateOf(false) }
        if (showTimeDialog) TimePickerDialog(
            onDismissRequest = { showTimeDialog = false },
            initialTime = reminderTime
        ) {
            onReminderTimeChange(it)
            showTimeDialog = false
        }

        AnimatedVisibility(reminderEnabled) {
            Column {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            showTimeDialog = true
                        }
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.reminder_time),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = formatTime(reminderTime),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text(text = stringResource(R.string.description)) },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PriorityTabRow(
    priorities: List<Priority>,
    selectedPriority: Priority,
    onChange: (Priority) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        priorities.forEach { priority ->
            FilterChip(
                selected = selectedPriority == priority,
                onClick = { onChange(priority) },
                label = {
                    Text(
                        text = stringResource(priority.titleRes),
                        color = if (selectedPriority == priority) Color.White else Color.Gray
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun FrequencyDropDown(
    selectedFrequency: HabitFrequency,
    onFrequencyChange: (HabitFrequency) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (selectedFrequency) {
                    HabitFrequency.DAILY -> stringResource(R.string.daily)
                    HabitFrequency.WEEKLY -> stringResource(R.string.weekly)
                    HabitFrequency.CUSTOM_DAYS -> stringResource(R.string.custom_days)
                    HabitFrequency.WEEKDAYS -> stringResource(R.string.weekdays)
                    HabitFrequency.WEEKENDS -> stringResource(R.string.weekends)
                    HabitFrequency.MONTHLY -> stringResource(R.string.monthly)
                },
                style = MaterialTheme.typography.bodyLarge
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            HabitFrequency.values().forEach { frequency ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onFrequencyChange(frequency)
                    },
                    text = {
                        Text(
                            text = when (frequency) {
                                HabitFrequency.DAILY -> stringResource(R.string.daily)
                                HabitFrequency.WEEKLY -> stringResource(R.string.weekly)
                                HabitFrequency.CUSTOM_DAYS -> stringResource(R.string.custom_days)
                                HabitFrequency.WEEKDAYS -> stringResource(R.string.weekdays)
                                HabitFrequency.WEEKENDS -> stringResource(R.string.weekends)
                                HabitFrequency.MONTHLY -> stringResource(R.string.monthly)
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun DayChip(
    day: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    androidx.compose.material3.FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(day) }
    )
}

@Composable
fun FilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.FilterChip(
        selected = selected,
        onClick = onClick,
        label = label,
        modifier = modifier.padding(horizontal = 4.dp)
    )
}

fun formatTime(timeInMillis: Long): String {
    if (timeInMillis == 0L) return "00:00"
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeInMillis
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
}

@Composable
fun AddHabitBottomSheetContent(
    onAddHabit: (com.mhss.app.domain.model.Habit) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.add_habit),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        androidx.compose.material3.OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.title)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.cancel))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAddHabit(
                            com.mhss.app.domain.model.Habit(
                                title = title,
                                description = "",
                                priority = Priority.LOW,
                                frequency = HabitFrequency.DAILY
                            )
                        )
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}

@Composable
fun TextButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    androidx.compose.material3.TextButton(onClick = onClick) {
        content()
    }
}

@Composable
fun Button(
    onClick: () -> Unit,
    enabled: Boolean,
    content: @Composable () -> Unit
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        enabled = enabled
    ) {
        content()
    }
}
