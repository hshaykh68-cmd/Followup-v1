package com.followup.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a reminder in the database.
 */
@Entity(
    tableName = "reminders",
    indices = [
        Index(value = ["status", "reminder_time"]),
        Index(value = ["reminder_time"])
    ]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "message")
    val message: String? = null,

    @ColumnInfo(name = "reminder_time")
    val reminderTime: Long,

    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
