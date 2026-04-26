package com.followup.service.notification

import android.app.Notification
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.followup.domain.model.ReminderStatus
import com.followup.domain.repository.ReminderRepository
import com.followup.domain.repository.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * NotificationListenerService that captures message notifications from messaging apps
 * and provides a "Remind later" action to instantly save reminders.
 */
@AndroidEntryPoint
class FollowUpNotificationListener : NotificationListenerService() {

    @Inject
    lateinit var reminderRepository: ReminderRepository

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var notificationFilter: NotificationFilter

    @Inject
    lateinit var notificationExtractor: NotificationExtractor

    @Inject
    lateinit var notificationActionHelper: NotificationActionHelper

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        private const val TAG = "FollowUpNotification"
        private val PROCESSED_NOTIFICATIONS = LruCache<String, Long>(100)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "NotificationListenerService created")
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d(TAG, "NotificationListenerService destroyed")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // Run in background to avoid blocking
        serviceScope.launch {
            try {
                processNotification(sbn)
            } catch (e: Exception) {
                Log.e(TAG, "Error processing notification", e)
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Notification was removed (user dismissed it)
        // We don't need to do anything here - the "Remind later" action
        // is independent of notification lifecycle
    }

    private suspend fun processNotification(sbn: StatusBarNotification) {
        // Check if auto-capture is enabled
        val autoCaptureEnabled = try {
            settingsRepository.isAutoCaptureEnabled().first()
        } catch (e: Exception) {
            true // Default to enabled if we can't read the setting
        }

        if (!autoCaptureEnabled) {
            Log.d(TAG, "Auto-capture disabled, skipping notification")
            return
        }

        val packageName = sbn.packageName

        // Check if this is a messaging app we care about
        if (!notificationFilter.isMessagingApp(packageName)) {
            return
        }

        // Check if notification is valid for processing
        if (!notificationFilter.shouldProcess(sbn)) {
            return
        }

        // Check for duplicates using notification key
        val notificationKey = sbn.key
        if (isDuplicate(notificationKey)) {
            Log.d(TAG, "Skipping duplicate notification: $notificationKey")
            return
        }

        // Extract notification data
        val sender = notificationExtractor.extractSender(sbn)
        val message = notificationExtractor.extractMessage(sbn)

        if (sender.isBlank() && message.isBlank()) {
            Log.d(TAG, "Skipping notification with no content")
            return
        }

        // Mark as processed
        PROCESSED_NOTIFICATIONS.put(notificationKey, System.currentTimeMillis())

        // Inject the "Remind later" action into the notification
        // Note: On Android 13+, we can only modify notifications from our own app
        // On older versions, we need to use a different approach
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            // Try to add action for older Android versions
            // This may not work on all devices due to OEM customizations
            tryInjectAction(sbn, sender, message)
        }

        Log.d(TAG, "Processed notification from $packageName: $sender - $message")
    }

    private fun isDuplicate(key: String): Boolean {
        return PROCESSED_NOTIFICATIONS.get(key) != null
    }

    private fun tryInjectAction(sbn: StatusBarNotification, sender: String, message: String) {
        try {
            notificationActionHelper.injectAction(sbn, sender, message)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to inject action", e)
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "NotificationListener connected")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "NotificationListener disconnected")
        // Try to reconnect
        requestRebind(componentName)
    }
}

/**
 * Simple LRU cache for deduplication
 */
private class LruCache<K, V>(private val maxSize: Int) {
    private val map = LinkedHashMap<K, V>(maxSize, 0.75f, true)

    @Synchronized
    fun put(key: K, value: V) {
        map[key] = value
        if (map.size > maxSize) {
            map.remove(map.keys.first())
        }
    }

    @Synchronized
    fun get(key: K): V? = map[key]
}
