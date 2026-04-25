package com.followup.domain.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

/**
 * Utility functions for time calculations.
 */
object TimeUtil {

    /**
     * Gets current timestamp in milliseconds.
     */
    fun now(): Long = System.currentTimeMillis()

    /**
     * Converts minutes to milliseconds.
     */
    fun minutesToMillis(minutes: Long): Long = TimeUnit.MINUTES.toMillis(minutes)

    /**
     * Converts hours to milliseconds.
     */
    fun hoursToMillis(hours: Long): Long = TimeUnit.HOURS.toMillis(hours)

    /**
     * Checks if a timestamp is in the past.
     */
    fun isInPast(timestamp: Long): Boolean = timestamp < now()

    /**
     * Checks if a timestamp is today.
     */
    fun isToday(timestamp: Long, zoneId: ZoneId = ZoneId.systemDefault()): Boolean {
        val targetDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId).toLocalDate()
        val today = LocalDate.now(zoneId)
        return targetDate == today
    }

    /**
     * Gets the start of today in milliseconds.
     */
    fun startOfToday(zoneId: ZoneId = ZoneId.systemDefault()): Long {
        return LocalDate.now(zoneId)
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()
    }

    /**
     * Gets the end of today in milliseconds.
     */
    fun endOfToday(zoneId: ZoneId = ZoneId.systemDefault()): Long {
        return LocalDate.now(zoneId)
            .plusDays(1)
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()
    }

    /**
     * Preset time options for quick reminder creation.
     */
    object Presets {
        val MINUTES_30: Long = minutesToMillis(30)
        val MINUTES_15: Long = minutesToMillis(15)
        val HOURS_1: Long = hoursToMillis(1)
        val HOURS_2: Long = hoursToMillis(2)
        val TOMORROW: Long = hoursToMillis(24)
    }
}
