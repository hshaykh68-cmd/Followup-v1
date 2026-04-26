package com.followup.service.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.notification.StatusBarNotification
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class to inject "Remind later" actions into notifications.
 *
 * Note: On Android 13+ (API 33+), apps can only modify their own notifications.
 * For third-party notifications, we use an alternative approach:
 * - On Android 13+: Post our own notification with the action
 * - On older versions: Try to inject action directly (may not work on all OEM skins)
 */
@Singleton
class NotificationActionHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val ACTION_REMIND_LATER = "com.followup.action.REMIND_LATER"
        const val EXTRA_SENDER = "extra_sender"
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_TIMESTAMP = "extra_timestamp"
        const val EXTRA_NOTIFICATION_KEY = "extra_notification_key"
    }

    /**
     * Attempt to inject action into notification.
     * Only effective on Android 12 and below, and may not work on all devices.
     */
    fun injectAction(sbn: StatusBarNotification, sender: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Can't modify notifications on Android 13+ from other apps
            return
        }

        // On older versions, we would need to use reflection or system APIs
        // This is unreliable across OEM skins, so we skip it
        // Instead, we'll create our own companion notification
    }

    /**
     * Create a PendingIntent for the "Remind later" action.
     */
    fun createRemindLaterIntent(
        sender: String,
        message: String,
        notificationKey: String
    ): PendingIntent {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_REMIND_LATER
            putExtra(EXTRA_SENDER, sender)
            putExtra(EXTRA_MESSAGE, message)
            putExtra(EXTRA_TIMESTAMP, System.currentTimeMillis())
            putExtra(EXTRA_NOTIFICATION_KEY, notificationKey)
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getBroadcast(
            context,
            notificationKey.hashCode(),
            intent,
            flags
        )
    }
}
