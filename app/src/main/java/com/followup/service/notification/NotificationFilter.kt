package com.followup.service.notification

import android.app.Notification
import android.os.Build
import android.service.notification.StatusBarNotification
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Filters notifications to identify messaging apps and valid message notifications.
 * Only captures notifications from relevant messaging apps and ignores system/promotional content.
 */
@Singleton
class NotificationFilter @Inject constructor() {

    companion object {
        private const val TAG = "NotificationFilter"

        // Priority messaging apps - these are the main targets
        private val MESSAGING_APPS = setOf(
            // WhatsApp variants
            "com.whatsapp",
            "com.whatsapp.w4b",
            // SMS/MMS apps
            "com.google.android.apps.messaging",
            "com.android.mms",
            "com.samsung.android.messaging",
            "com.sonyericsson.conversations",
            "com.motorola.messaging",
            // Other popular messaging apps
            "com.facebook.orca", // Facebook Messenger
            "com.instagram.android", // Instagram Direct
            "com.telegram.messenger",
            "com.telegram.messenger.web",
            "com.discord",
            "com.google.android.apps.dynamite", // Google Chat
            "com.google.android.talk", // Google Hangouts
            "com.microsoft.teams",
            "com.slack",
            "com.tencent.mm", // WeChat
            "com.viber.voip",
            "com.linecorp.lineandroid",
            "com.skype.raider",
            "com.android.email",
            "com.google.android.gm", // Gmail
        )

        // Apps to explicitly ignore
        private val BLOCKED_APPS = setOf(
            // System apps
            "android",
            "com.android.systemui",
            "com.android.settings",
            "com.google.android.gms",
            "com.google.android.systemui",
            // Promotional/marketing apps
            "com.google.android.apps.maps", // Maps has promos
            "com.amazon.mShop.android.shopping",
            "com.facebook.katana", // Facebook main app (not messenger)
        )

        // Notification categories that suggest it's not a personal message
        private val IGNORED_CATEGORIES = setOf(
            Notification.CATEGORY_PROMO,
            Notification.CATEGORY_ADVERTISEMENT,
            Notification.CATEGORY_SYSTEM,
            Notification.CATEGORY_SERVICE,
            Notification.CATEGORY_PROGRESS,
            Notification.CATEGORY_STATUS,
        )

        // Group key patterns that suggest group/system messages
        private val IGNORED_GROUP_PATTERNS = listOf(
            "summary",
            "bundle",
            "silent",
            "system",
            "persistent"
        )
    }

    /**
     * Check if the package is a messaging app we should monitor.
     */
    fun isMessagingApp(packageName: String): Boolean {
        // Explicitly blocked
        if (BLOCKED_APPS.contains(packageName)) {
            return false
        }

        // Known messaging app
        if (MESSAGING_APPS.contains(packageName)) {
            return true
        }

        // Heuristic: Check if package name suggests messaging
        val lowerPackage = packageName.lowercase()
        return when {
            lowerPackage.contains("sms") -> true
            lowerPackage.contains("mms") -> true
            lowerPackage.contains("message") -> true
            lowerPackage.contains("chat") -> true
            lowerPackage.contains("messenger") -> true
            lowerPackage.contains("whatsapp") -> true
            else -> false
        }
    }

    /**
     * Determine if this notification should be processed as a reminder candidate.
     */
    fun shouldProcess(sbn: StatusBarNotification): Boolean {
        // Check if notification is ongoing/persistent (usually not a message)
        if (sbn.isOngoing || sbn.isClearable == false) {
            Log.d(TAG, "Skipping ongoing/persistent notification")
            return false
        }

        val notification = sbn.notification

        // Check category
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val category = notification.category
            if (category != null && IGNORED_CATEGORIES.contains(category)) {
                Log.d(TAG, "Skipping notification with category: $category")
                return false
            }

            // Prefer messaging category if available
            if (category == Notification.CATEGORY_MESSAGE ||
                category == Notification.CATEGORY_EMAIL ||
                category == Notification.CATEGORY_CALL
            ) {
                return true
            }
        }

        // Check group key for patterns suggesting non-message notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val groupKey = sbn.groupKey?.lowercase() ?: ""
            if (IGNORED_GROUP_PATTERNS.any { groupKey.contains(it) }) {
                Log.d(TAG, "Skipping notification with group key: $groupKey")
                return false
            }
        }

        // Check for content
        val extras = notification.extras
        val hasTitle = extras.getString(Notification.EXTRA_TITLE)?.isNotBlank() == true ||
                extras.getCharSequence(Notification.EXTRA_TITLE)?.isNotBlank() == true
        val hasText = extras.getCharSequence(Notification.EXTRA_TEXT)?.isNotBlank() == true ||
                extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.isNotBlank() == true

        if (!hasTitle && !hasText) {
            Log.d(TAG, "Skipping notification with no content")
            return false
        }

        return true
    }
}
