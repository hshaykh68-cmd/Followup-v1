package com.followup.presentation.reminder

/**
 * Filter options for the reminder list.
 */
enum class ReminderFilter {
    ALL,
    PENDING,
    TODAY,
    OVERDUE,
    DONE;

    val displayName: String
        get() = when (this) {
            ALL -> "All"
            PENDING -> "Pending"
            TODAY -> "Today"
            OVERDUE -> "Overdue"
            DONE -> "Done"
        }
}
