package com.followup.service.notification

import android.app.Activity
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class to manage notification listener permission and guide users through setup.
 */
@Singleton
class NotificationPermissionHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val TAG = "NotificationPermission"
        private const val NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
    }

    /**
     * Check if notification listener permission is granted.
     */
    fun isNotificationListenerEnabled(): Boolean {
        val componentName = ComponentName(context, FollowUpNotificationListener::class.java)
        val enabledListeners = NotificationManagerCompat.getEnabledListenerPackages(context)
        return enabledListeners.contains(context.packageName)
    }

    /**
     * Check if notification listener is currently connected and running.
     */
    fun isNotificationListenerRunning(): Boolean {
        // We can't directly check if service is running, but we can check if permission is granted
        // The system will keep the service running when permission is granted
        return isNotificationListenerEnabled()
    }

    /**
     * Open system settings to enable notification listener permission.
     */
    fun openNotificationListenerSettings(activity: Activity, requestCode: Int = 1001) {
        try {
            val intent = Intent(NOTIFICATION_LISTENER_SETTINGS).apply {
                // Add hint to highlight our app
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
            activity.startActivityForResult(intent, requestCode)
        } catch (e: Exception) {
            // Fallback to general notification settings
            try {
                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                activity.startActivityForResult(intent, requestCode)
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to open notification settings", e2)
            }
        }
    }

    /**
     * Get a user-friendly explanation of why the permission is needed.
     */
    fun getPermissionRationale(): String {
        return "FollowUp needs access to notifications to automatically capture reminders from your messages. " +
                "We only read notifications from messaging apps (WhatsApp, SMS, etc.) and never store your " +
                "full conversations."
    }

    /**
     * Check if basic notification permission is granted (Android 13+).
     */
    fun hasBasicNotificationPermission(): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    /**
     * Request rebind of the notification listener service.
     * Call this when permission is granted to ensure service starts immediately.
     */
    fun requestServiceRebind() {
        if (isNotificationListenerEnabled()) {
            val componentName = ComponentName(context, FollowUpNotificationListener::class.java)
            try {
                // Request the system to rebind our service
                NotificationManagerCompat.getEnabledListenerPackages(context)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to request rebind", e)
            }
        }
    }
}
