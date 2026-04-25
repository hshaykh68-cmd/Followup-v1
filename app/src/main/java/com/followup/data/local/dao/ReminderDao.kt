package com.followup.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.followup.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for reminder operations.
 */
@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE id = :reminderId")
    suspend fun deleteReminderById(reminderId: Long)

    @Query("SELECT * FROM reminders ORDER BY reminder_time ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :reminderId")
    suspend fun getReminderById(reminderId: Long): ReminderEntity?

    @Query("SELECT * FROM reminders WHERE id = :reminderId")
    fun getReminderByIdFlow(reminderId: Long): Flow<ReminderEntity?>

    @Query("SELECT * FROM reminders WHERE status = :status ORDER BY reminder_time ASC")
    fun getRemindersByStatus(status: String): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE status = 'pending' AND reminder_time < :currentTime ORDER BY reminder_time ASC")
    fun getOverdueReminders(currentTime: Long = System.currentTimeMillis()): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE status = 'pending' AND reminder_time >= :startOfDay AND reminder_time < :endOfDay ORDER BY reminder_time ASC")
    fun getTodayReminders(startOfDay: Long, endOfDay: Long): Flow<List<ReminderEntity>>

    @Query("SELECT COUNT(*) FROM reminders WHERE status = 'pending'")
    fun getPendingCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM reminders WHERE status = 'done'")
    suspend fun getCompletedCount(): Int

    @Query("UPDATE reminders SET status = :newStatus WHERE id = :reminderId")
    suspend fun updateStatus(reminderId: Long, newStatus: String)

    @Query("UPDATE reminders SET reminder_time = :newTime WHERE id = :reminderId")
    suspend fun updateReminderTime(reminderId: Long, newTime: Long)

    @Query("DELETE FROM reminders WHERE status = 'done'")
    suspend fun deleteAllCompleted()
}
