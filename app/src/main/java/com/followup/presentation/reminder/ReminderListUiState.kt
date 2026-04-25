package com.followup.presentation.reminder

import com.followup.domain.model.Reminder
import com.followup.domain.util.TimeUtil

/**
 * UI state for the reminder list screen.
 */
data class ReminderListUiState(
    val reminders: List<Reminder> = emptyList(),
    val isLoading: Boolean = false,
    val selectedFilter: ReminderFilter = ReminderFilter.PENDING,
    val errorMessage: String? = null
) {
    val isEmpty: Boolean
        get() = reminders.isEmpty() && !isLoading

    val filteredReminders: List<Reminder>
        get() = when (selectedFilter) {
            ReminderFilter.ALL -> reminders
            ReminderFilter.PENDING -> reminders.filter { it.isPending && !it.isOverdue }
            ReminderFilter.TODAY -> reminders.filter { 
                it.isPending && TimeUtil.isToday(it.reminderTime) 
            }
            ReminderFilter.OVERDUE -> reminders.filter { it.isOverdue }
            ReminderFilter.DONE -> reminders.filter { it.isDone }
        }
}
