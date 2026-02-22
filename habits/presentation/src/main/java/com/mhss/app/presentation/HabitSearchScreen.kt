package com.mhss.app.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mhss.app.presentation.components.habit.HabitCard
import com.mhss.app.ui.R
import com.mhss.app.ui.navigation.Screen
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun HabitSearchScreen(
    navController: NavHostController,
    viewModel: HabitsViewModel = koinViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        delay(300)
        viewModel.onEvent(HabitsScreenEvent.SearchHabits(searchQuery))
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.search_habits),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.search)) }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (searchQuery.isNotBlank()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    items(viewModel.uiState.searchHabits, key = { it.id }) { habit ->
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
                            }
                        )
                    }
                }
            }
        }
    }
}
