package com.mhss.app.presentation.components.habit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mhss.app.domain.model.HabitFrequency
import com.mhss.app.ui.R

@Composable
fun HabitFrequencySelector(
    selectedFrequency: HabitFrequency,
    onFrequencyChange: (HabitFrequency) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.frequency),
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            HabitFrequency.values().forEach { frequency ->
                FilterChip(
                    selected = selectedFrequency == frequency,
                    onClick = { onFrequencyChange(frequency) },
                    label = {
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
                    },
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        }
    }
}
