@file:OptIn(ExperimentalLayoutApi::class)

package com.mhss.app.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mhss.app.preferences.domain.model.Order
import com.mhss.app.preferences.domain.model.OrderType
import com.mhss.app.ui.R
import com.mhss.app.ui.components.common.LiquidFloatingActionButton
import com.mhss.app.ui.components.common.MyBrainAppBar
import com.mhss.app.ui.navigation.Screen
import com.mhss.app.ui.snackbar.LocalisedSnackbarHost
import com.mhss.app.ui.titleRes
import com.mhss.app.presentation.components.habit.HabitCard
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.rememberLiquidState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    navController: NavHostController,
    viewModel: HabitsViewModel = koinViewModel()
) {
    var orderSettingsVisible by remember { mutableStateOf(false) }
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var openSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val liquidState = rememberLiquidState()

    LaunchedEffect(uiState.navigateToDetail) {
        uiState.navigateToDetail?.let { habitId ->
            navController.navigate(Screen.HabitDetailScreen(habitId))
            viewModel.onEvent(HabitsScreenEvent.Navigate(null))
        }
    }

    Scaffold(
        snackbarHost = { LocalisedSnackbarHost(snackbarHostState) },
        topBar = {
            MyBrainAppBar(stringResource(R.string.habits))
        },
        floatingActionButton = {
            AnimatedVisibility(!sheetState.isVisible) {
                LiquidFloatingActionButton(
                    onClick = {
                        openSheet = true
                    },
                    iconPainter = painterResource(R.drawable.ic_add),
                    contentDescription = stringResource(R.string.add_habit),
                    liquidState = liquidState
                )
            }
        },
    ) { paddingValues ->
        if (openSheet) ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { openSheet = false },
            properties = ModalBottomSheetProperties(
                shouldDismissOnBackPress = true
            )
        ) {
            AddHabitBottomSheetContent(
                onAddHabit = { habit ->
                    viewModel.onEvent(HabitsScreenEvent.Navigate(habit.id))
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) { openSheet = false }
                    }
                },
                onCancel = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) { openSheet = false }
                    }
                }
            )
        }

        if (uiState.habits.isEmpty()) NoHabitsMessage()

        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .liquefiable(liquidState)
        ) {
            Column(
                Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { orderSettingsVisible = !orderSettingsVisible }) {
                        Icon(
                            modifier = Modifier.size(25.dp),
                            painter = painterResource(R.drawable.ic_settings_sliders),
                            contentDescription = stringResource(R.string.order_by)
                        )
                    }
                    IconButton(onClick = {
                        navController.navigate(Screen.HabitSearchScreen)
                    }) {
                        Icon(
                            modifier = Modifier.size(25.dp),
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = stringResource(R.string.search)
                        )
                    }
                }
                AnimatedVisibility(visible = orderSettingsVisible) {
                    HabitsSettingsSection(
                        uiState.habitOrder,
                        onOrderChange = {
                            viewModel.onEvent(
                                HabitsScreenEvent.UpdateOrder(it)
                            )
                        }
                    )
                }
            }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 4.dp)
            ) {
                items(uiState.habits, key = { it.id }) { habit ->
                    HabitCard(
                        habit = habit,
                        isCompletedToday = isHabitCompletedToday(habit),
                        onComplete = {
                            viewModel.completeHabitToday(habit, isHabitCompletedToday(habit))
                        },
                        onClick = {
                            navController.navigate(
                                Screen.HabitDetailScreen(
                                    habitId = habit.id
                                )
                            )
                        },
                        onDelete = {
                            viewModel.onEvent(HabitsScreenEvent.DeleteHabit(habit))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NoHabitsMessage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.no_habits_message),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Image(
            modifier = Modifier.size(125.dp),
            painter = painterResource(id = R.drawable.habits_img),
            contentDescription = stringResource(R.string.no_habits_message),
            alpha = 0.7f
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HabitsSettingsSection(
    order: Order,
    onOrderChange: (Order) -> Unit
) {
    val orders = remember {
        listOf(
            Order.DateCreated(),
            Order.DateModified(),
            Order.Alphabetical(),
            Order.Priority()
        )
    }
    val orderTypes = remember {
        listOf(
            OrderType.ASC,
            OrderType.DESC
        )
    }
    Column(
        Modifier.background(color = MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = stringResource(R.string.order_by),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
        FlowRow(
            modifier = Modifier.padding(end = 8.dp)
        ) {
            orders.forEach {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = order::class == it::class,
                        onClick = {
                            if (order != it)
                                onOrderChange(
                                    it.copyOrder(orderType = order.orderType)
                                )
                        }
                    )
                    Text(
                        text = stringResource(it.titleRes),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        HorizontalDivider()
        FlowRow {
            orderTypes.forEach {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = order.orderType == it,
                        onClick = {
                            if (order != it)
                                onOrderChange(
                                    order.copyOrder(it)
                                )
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(it.titleRes),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

fun isHabitCompletedToday(habit: com.mhss.app.domain.model.Habit): Boolean {
    val today = System.currentTimeMillis()
    val startOfDay = today - (today % (24 * 60 * 60 * 1000))
    return habit.completedDates.any { it >= startOfDay }
}
