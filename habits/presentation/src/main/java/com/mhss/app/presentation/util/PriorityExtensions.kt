package com.mhss.app.presentation.util

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.mhss.app.domain.model.Priority
import com.mhss.app.ui.R

val Priority.titleRes: Int
    @StringRes
    get() = when (this) {
        Priority.LOW -> R.string.low
        Priority.MEDIUM -> R.string.medium
        Priority.HIGH -> R.string.high
    }

val Priority.color: Color
    get() = when (this) {
        Priority.LOW -> Color(0xFF4CAF50)
        Priority.MEDIUM -> Color(0xFFFF9800)
        Priority.HIGH -> Color(0xFFF44336)
    }
