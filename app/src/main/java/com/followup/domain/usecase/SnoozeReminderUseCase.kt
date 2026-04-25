package com.followup.domain.usecase

import com.followup.domain.repository.ReminderRepository
import javax.inject.Inject

/**
 * Use case for snoozing a reminder.
 */
class SnoozeReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(
        reminderId: Long,
        durationMillis: Long
    ): Result<Unit> {
        if (durationMillis <= 0) {
            return Result.failure(IllegalArgumentException("Snooze duration must be positive"))
        }

        val newTime = System.currentTimeMillis() + durationMillis

        return try {
            reminderRepository.snoozeReminder(reminderId, newTime)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
