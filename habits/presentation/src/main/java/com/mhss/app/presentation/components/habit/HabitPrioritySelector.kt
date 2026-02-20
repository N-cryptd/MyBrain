package com.mhss.app.presentation.components.habit

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mhss.app.domain.model.Priority
import com.mhss.app.ui.R

@Composable
fun HabitPrioritySelector(
    selectedPriority: Priority,
    onPriorityChange: (Priority) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
    ) {
        Priority.values().forEach { priority ->
            FilterChip(
                selected = selectedPriority == priority,
                onClick = { onPriorityChange(priority) },
                label = {
                    Text(
                        text = stringResource(priority.titleRes),
                        color = if (selectedPriority == priority) Color.White else priority.color
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp)
            )
        }
    }
}
