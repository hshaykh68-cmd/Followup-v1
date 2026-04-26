package com.followup.service.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.followup.domain.model.ReminderStatus
import com.followup.domain.repository.ReminderRepository
import com.followup.domain.repository.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * BroadcastReceiver that handles the "Remind later" action from notifications.
 * Creates a reminder with smart defaults and shows confirmation to the user.
 */
@AndroidEntryPoint
class NotificationActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var reminderRepository: ReminderRepository

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val receiverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        private const val TAG = "NotificationActionReceiver"
        private const val DEFAULT_REMINDER_DELAY_MS = 60 * 60 * 1000L // 1 hour default
        private val PROCESSED_ACTIONS = java.util.Collections.synchronizedSet(HashSet<String>())
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            NotificationActionHelper.ACTION_REMIND_LATER -> {
                handleRemindLater(context, intent)
            }
        }
    }

    private fun handleRemindLater(context: Context, intent: Intent) {
        val sender = intent.getStringExtra(NotificationActionHelper.EXTRA_SENDER) ?: "Unknown"
        val message = intent.getStringExtra(NotificationActionHelper.EXTRA_MESSAGE) ?: ""
        val timestamp = intent.getLongExtra(NotificationActionHelper.EXTRA_TIMESTAMP, System.currentTimeMillis())
        val notificationKey = intent.getStringExtra(NotificationActionHelper.EXTRA_NOTIFICATION_KEY)
            ?: "${sender}_${timestamp}"

        // Prevent duplicate processing
        if (PROCESSED_ACTIONS.contains(notificationKey)) {
            return
        }
        PROCESSED_ACTIONS.add(notificationKey)

        // Clean up old entries periodically
        if (PROCESSED_ACTIONS.size > 100) {
            PROCESSED_ACTIONS.clear()
        }

        // Process in background
        receiverScope.launch {
            try {
                createReminder(context, sender, message)
            } catch (e: Exception) {
                showErrorToast(context)
            }
        }
    }

    private suspend fun createReminder(context: Context, sender: String, message: String) {
        // Get default reminder time from settings
        val defaultTime = try {
            settingsRepository.getDefaultReminderTime().first()
        } catch (e: Exception) {
            DEFAULT_REMINDER_DELAY_MS
        }

        val reminderTime = System.currentTimeMillis() + defaultTime

        // Create reminder name from sender
        val reminderName = buildReminderName(sender, message)

        // Build reminder object
        val reminder = com.followup.domain.model.Reminder(
            name = reminderName,
            message = if (message.isNotBlank()) "From $sender: $message" else "Reply to $sender",
            reminderTime = reminderTime,
            status = ReminderStatus.PENDING
        )

        // Save to database
        val reminderId = reminderRepository.createReminder(reminder)

        // Show success feedback on main thread
        CoroutineScope(Dispatchers.Main).launch {
            showSuccessToast(context, sender)
        }
    }

    private fun buildReminderName(sender: String, message: String): String {
        return when {
            sender != "Unknown" && message.isNotBlank() -> {
                // Create a contextual reminder name
                val preview = if (message.length > 30) message.take(30) + "..." else message
                "Reply to $sender: $preview"
            }
            sender != "Unknown" -> "Reply to $sender"
            message.isNotBlank() -> {
                val preview = if (message.length > 40) message.take(40) + "..." else message
                "Reply: $preview"
            }
            else -> "Reply to message"
        }
    }

    private fun showSuccessToast(context: Context, sender: String) {
        try {
            val message = if (sender != "Unknown") {
                "Reminder saved: Reply to $sender"
            } else {
                "Reminder saved"
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Toast can fail if context is no longer valid
        }
    }

    private fun showErrorToast(context: Context) {
        try {
            Toast.makeText(context, "Could not save reminder", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Toast can fail if context is no longer valid
        }
    }
}
