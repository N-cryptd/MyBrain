package com.mhss.app.presentation.components.habit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mhss.app.domain.model.Habit
import com.mhss.app.domain.model.Priority
import com.mhss.app.presentation.HabitsScreen
import com.mhss.app.ui.R
import com.mhss.app.ui.color
import com.mhss.app.util.date.formatDateDependingOnDay

@Composable
fun HabitCard(
    habit: Habit,
    isCompletedToday: Boolean,
    onComplete: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit = {}
) {
    val context = LocalContext.current
    val formattedDate by remember(habit.updatedDate) {
        derivedStateOf { habit.updatedDate.formatDateDependingOnDay(context) }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .padding(8.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                HabitCheckBox(
                    isComplete = isCompletedToday,
                    priority = habit.priority,
                    onComplete = { onComplete() }
                )
                Spacer(Modifier.width(6.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = habit.title,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = if (isCompletedToday) FontWeight.Normal else FontWeight.Bold
                    )
                    if (habit.description.isNotEmpty()) {
                        Text(
                            text = habit.description,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(14.dp),
                    painter = painterResource(R.drawable.ic_fire),
                    contentDescription = null,
                    tint = Color(0xFFFF6B35)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${habit.streakCount} ${stringResource(R.string.day_streak)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.frequency_short, getFrequencyText(habit.frequency)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun HabitCheckBox(
    isComplete: Boolean,
    priority: Priority,
    onComplete: () -> Unit
) {
    androidx.compose.material3.IconButton(
        onClick = onComplete,
        modifier = Modifier.size(32.dp)
    ) {
        androidx.compose.material3.Icon(
            painter = painterResource(
                id = if (isComplete) R.drawable.ic_check_circle else R.drawable.ic_circle
            ),
            contentDescription = null,
            tint = if (isComplete) Color(0xFF4CAF50) else priority.color,
            modifier = Modifier.size(24.dp)
        )
    }
}

fun getFrequencyText(frequency: com.mhss.app.domain.model.HabitFrequency): String {
    return when (frequency) {
        com.mhss.app.domain.model.HabitFrequency.DAILY -> "Daily"
        com.mhss.app.domain.model.HabitFrequency.WEEKLY -> "Weekly"
        com.mhss.app.domain.model.HabitFrequency.CUSTOM_DAYS -> "Custom"
        com.mhss.app.domain.model.HabitFrequency.WEEKDAYS -> "Weekdays"
        com.mhss.app.domain.model.HabitFrequency.WEEKENDS -> "Weekends"
        com.mhss.app.domain.model.HabitFrequency.MONTHLY -> "Monthly"
    }
}
