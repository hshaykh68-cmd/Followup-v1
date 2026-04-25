package com.followup.domain.usecase

import com.followup.domain.model.Reminder
import com.followup.domain.model.ReminderStatus
import com.followup.domain.repository.ReminderRepository
import javax.inject.Inject

/**
 * Use case for creating a new reminder.
 */
class CreateReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(
        name: String,
        message: String? = null,
        reminderTime: Long
    ): Result<Long> {
        if (name.isBlank()) {
            return Result.failure(IllegalArgumentException("Name cannot be empty"))
        }

        if (reminderTime <= System.currentTimeMillis()) {
            return Result.failure(IllegalArgumentException("Reminder time must be in the future"))
        }

        val reminder = Reminder(
            name = name.trim(),
            message = message?.trim(),
            reminderTime = reminderTime,
            status = ReminderStatus.PENDING
        )

        return try {
            val id = reminderRepository.createReminder(reminder)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
