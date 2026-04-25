package com.followup.domain.usecase

import com.followup.domain.repository.ReminderRepository
import javax.inject.Inject

/**
 * Use case for deleting a reminder.
 */
class DeleteReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(reminderId: Long): Result<Unit> {
        return try {
            reminderRepository.deleteReminder(reminderId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
