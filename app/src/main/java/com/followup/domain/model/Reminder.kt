package com.followup.domain.model

/**
 * Domain model representing a reminder.
 * This is the business entity used across the domain and presentation layers.
 */
data class Reminder(
    val id: Long = 0,
    val name: String,
    val message: String? = null,
    val reminderTime: Long,
    val status: ReminderStatus,
    val createdAt: Long = System.currentTimeMillis()
) {
    val isOverdue: Boolean
        get() = status == ReminderStatus.PENDING && reminderTime < System.currentTimeMillis()

    val isPending: Boolean
        get() = status == ReminderStatus.PENDING

    val isDone: Boolean
        get() = status == ReminderStatus.DONE
}

enum class ReminderStatus {
    PENDING,
    DONE;

    companion object {
        fun fromString(value: String): ReminderStatus =
            entries.find { it.name.equals(value, ignoreCase = true) }
                ?: PENDING
    }
}
