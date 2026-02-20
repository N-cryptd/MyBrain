package com.mhss.app.presentation.components.habit

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mhss.app.domain.model.Habit
import com.mhss.app.ui.R

@Composable
fun HabitStatsCard(
    modifier: Modifier = Modifier,
    habit: Habit
) {
    val completionRate = if (habit.createdDate > 0) {
        val totalDays = ((System.currentTimeMillis() - habit.createdDate) / (24 * 60 * 60 * 1000)).toInt() + 1
        if (totalDays > 0) (habit.completedDates.size * 100 / totalDays) else 0
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
                icon = R.drawable.ic_check,
                label = stringResource(R.string.best_streak),
                value = "${habit.bestStreak} ${stringResource(R.string.days)}"
            )

            StatRow(
                icon = R.drawable.ic_check,
                label = stringResource(R.string.total_completions),
                value = "${habit.completedDates.size}"
            )

            StatRow(
                icon = R.drawable.ic_chart,
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
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
        )
    }
}
