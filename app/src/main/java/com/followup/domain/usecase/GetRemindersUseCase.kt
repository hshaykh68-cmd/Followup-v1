package com.followup.domain.usecase

import com.followup.domain.model.Reminder
import com.followup.domain.model.ReminderStatus
import com.followup.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving reminders with optional filtering.
 */
class GetRemindersUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    /**
     * Gets all reminders as a reactive stream.
     */
    operator fun invoke(): Flow<List<Reminder>> {
        return reminderRepository.getAllReminders()
    }

    /**
     * Gets reminders by specific status.
     */
    fun byStatus(status: ReminderStatus): Flow<List<Reminder>> {
        return reminderRepository.getRemindersByStatus(status)
    }

    /**
     * Gets pending reminders.
     */
    fun pending(): Flow<List<Reminder>> {
        return reminderRepository.getRemindersByStatus(ReminderStatus.PENDING)
    }

    /**
     * Gets completed reminders.
     */
    fun done(): Flow<List<Reminder>> {
        return reminderRepository.getRemindersByStatus(ReminderStatus.DONE)
    }

    /**
     * Gets today's reminders.
     */
    fun today(): Flow<List<Reminder>> {
        return reminderRepository.getTodayReminders()
    }

    /**
     * Gets overdue reminders.
     */
    fun overdue(): Flow<List<Reminder>> {
        return reminderRepository.getOverdueReminders()
    }
}
