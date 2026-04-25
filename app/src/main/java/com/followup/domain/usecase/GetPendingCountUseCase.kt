package com.followup.domain.usecase

import com.followup.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting the count of pending reminders.
 */
class GetPendingCountUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    operator fun invoke(): Flow<Int> {
        return reminderRepository.getPendingCount()
    }
}
