package com.followup.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.followup.data.local.dao.ReminderDao
import com.followup.data.local.entity.ReminderEntity

/**
 * Room database for the FollowUp app.
 */
@Database(
    entities = [ReminderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao

    companion object {
        const val DATABASE_NAME = "followup_database"
    }
}
