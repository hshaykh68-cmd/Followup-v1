package com.followup.domain.usecase

import com.followup.domain.model.Reminder
import com.followup.domain.repository.ReminderRepository
import javax.inject.Inject

/**
 * Use case for updating an existing reminder.
 */
class UpdateReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(reminder: Reminder): Result<Unit> {
        if (reminder.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Name cannot be empty"))
        }

        return try {
            reminderRepository.updateReminder(reminder)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
