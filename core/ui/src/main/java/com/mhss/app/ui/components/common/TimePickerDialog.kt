package com.mhss.app.ui.components.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mhss.app.ui.R
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    initialTime: Long,
    onTimePicked: (Long) -> Unit,
) {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = initialTime
    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE)
    )

    TimePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    val newCalendar = Calendar.getInstance()
                    newCalendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    newCalendar.set(Calendar.MINUTE, timePickerState.minute)
                    onTimePicked(newCalendar.timeInMillis)
                }
            ) {
                Text(stringResource(R.string.okay))
            }
        }
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            TimePicker(
                state = timePickerState,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
