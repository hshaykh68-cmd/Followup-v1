package com.followup.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.ui.graphics.vector.ImageVector
import com.followup.R

enum class TimePreset(
    val labelResId: Int,
    val durationMillis: Long,
    val icon: ImageVector
) {
    MINUTES_30(R.string.time_30min, 30 * 60 * 1000L, Icons.Default.Schedule),
    HOUR_1(R.string.time_1hour, 60 * 60 * 1000L, Icons.Default.Schedule),
    TONIGHT(R.string.time_tonight, -1L, Icons.Default.Nightlight),
    TOMORROW(R.string.time_tomorrow, 24 * 60 * 60 * 1000L, Icons.Default.CalendarToday);

    companion object {
        fun getTonightTime(): Long {
            val calendar = java.util.Calendar.getInstance()
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 20)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            
            val tonight = calendar.timeInMillis
            return if (tonight > System.currentTimeMillis()) {
                tonight
            } else {
                tonight + 24 * 60 * 60 * 1000L
            }
        }
    }
}

