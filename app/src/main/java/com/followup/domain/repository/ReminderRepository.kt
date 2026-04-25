package com.followup.domain.repository

import com.followup.domain.model.Reminder
import com.followup.domain.model.ReminderStatus
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for reminder operations.
 * Defines the contract for data operations on reminders.
 */
interface ReminderRepository {

    /**
     * Creates a new reminder. Returns the ID of the created reminder.
     */
    suspend fun createReminder(reminder: Reminder): Long

    /**
     * Updates an existing reminder.
     */
    suspend fun updateReminder(reminder: Reminder)

    /**
     * Deletes a reminder by its ID.
     */
    suspend fun deleteReminder(reminderId: Long)

    /**
     * Gets all reminders as a reactive stream.
     */
    fun getAllReminders(): Flow<List<Reminder>>

    /**
     * Gets a single reminder by ID.
     */
    suspend fun getReminderById(reminderId: Long): Reminder?

    /**
     * Gets a single reminder by ID as a reactive stream.
     */
    fun getReminderByIdFlow(reminderId: Long): Flow<Reminder?>

    /**
     * Gets reminders filtered by status.
     */
    fun getRemindersByStatus(status: ReminderStatus): Flow<List<Reminder>>

    /**
     * Gets all pending reminders scheduled for today.
     */
    fun getTodayReminders(): Flow<List<Reminder>>

    /**
     * Gets all overdue pending reminders.
     */
    fun getOverdueReminders(): Flow<List<Reminder>>

    /**
     * Updates the status of a reminder.
     */
    suspend fun updateStatus(reminderId: Long, status: ReminderStatus)

    /**
     * Marks a reminder as done.
     */
    suspend fun markAsDone(reminderId: Long)

    /**
     * Snoozes a reminder to a new time.
     */
    suspend fun snoozeReminder(reminderId: Long, newTime: Long)

    /**
     * Gets the count of pending reminders as a reactive stream.
     */
    fun getPendingCount(): Flow<Int>

    /**
     * Gets the count of completed reminders.
     */
    suspend fun getCompletedCount(): Int

    /**
     * Deletes all completed reminders.
     */
    suspend fun deleteAllCompleted()
}
