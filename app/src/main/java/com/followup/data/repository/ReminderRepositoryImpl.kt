package com.followup.data.repository

import com.followup.data.local.dao.ReminderDao
import com.followup.data.local.entity.ReminderEntity
import com.followup.domain.model.Reminder
import com.followup.domain.model.ReminderStatus
import com.followup.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId

/**
 * Implementation of ReminderRepository.
 * Maps between domain models and database entities.
 */
class ReminderRepositoryImpl(
    private val reminderDao: ReminderDao
) : ReminderRepository {

    override suspend fun createReminder(reminder: Reminder): Long {
        val entity = reminder.toEntity()
        return reminderDao.insertReminder(entity)
    }

    override suspend fun updateReminder(reminder: Reminder) {
        val entity = reminder.toEntity()
        reminderDao.updateReminder(entity)
    }

    override suspend fun deleteReminder(reminderId: Long) {
        reminderDao.deleteReminderById(reminderId)
    }

    override fun getAllReminders(): Flow<List<Reminder>> {
        return reminderDao.getAllReminders().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getReminderById(reminderId: Long): Reminder? {
        return reminderDao.getReminderById(reminderId)?.toDomain()
    }

    override fun getReminderByIdFlow(reminderId: Long): Flow<Reminder?> {
        return reminderDao.getReminderByIdFlow(reminderId).map { entity ->
            entity?.toDomain()
        }
    }

    override fun getRemindersByStatus(status: ReminderStatus): Flow<List<Reminder>> {
        return reminderDao.getRemindersByStatus(status.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTodayReminders(): Flow<List<Reminder>> {
        val now = LocalDate.now(ZoneId.systemDefault())
        val startOfDay = now.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = now.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        return reminderDao.getTodayReminders(startOfDay, endOfDay).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getOverdueReminders(): Flow<List<Reminder>> {
        return reminderDao.getOverdueReminders(System.currentTimeMillis()).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun updateStatus(reminderId: Long, status: ReminderStatus) {
        reminderDao.updateStatus(reminderId, status.name)
    }

    override suspend fun markAsDone(reminderId: Long) {
        reminderDao.updateStatus(reminderId, ReminderStatus.DONE.name)
    }

    override suspend fun snoozeReminder(reminderId: Long, newTime: Long) {
        reminderDao.updateReminderTime(reminderId, newTime)
    }

    override fun getPendingCount(): Flow<Int> {
        return reminderDao.getPendingCount()
    }

    override suspend fun getCompletedCount(): Int {
        return reminderDao.getCompletedCount()
    }

    override suspend fun deleteAllCompleted() {
        reminderDao.deleteAllCompleted()
    }

    /**
     * Maps a ReminderEntity to a domain Reminder.
     */
    private fun ReminderEntity.toDomain(): Reminder {
        return Reminder(
            id = this.id,
            name = this.name,
            message = this.message,
            reminderTime = this.reminderTime,
            status = ReminderStatus.fromString(this.status),
            createdAt = this.createdAt
        )
    }

    /**
     * Maps a domain Reminder to a ReminderEntity.
     */
    private fun Reminder.toEntity(): ReminderEntity {
        return ReminderEntity(
            id = this.id,
            name = this.name,
            message = this.message,
            reminderTime = this.reminderTime,
            status = this.status.name,
            createdAt = this.createdAt
        )
    }
}
