package com.followup.service.notification

import android.app.Notification
import android.app.Person
import android.os.Build
import android.os.Bundle
import android.service.notification.StatusBarNotification
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Extracts sender name and message preview from notifications.
 * Handles various notification formats including messaging-style notifications.
 */
@Singleton
class NotificationExtractor @Inject constructor() {

    companion object {
        private const val MAX_MESSAGE_LENGTH = 200
        private const val UNKNOWN_SENDER = "Unknown"
    }

    /**
     * Extract the sender name from the notification.
     * Tries multiple sources: title, person name, or app name.
     */
    fun extractSender(sbn: StatusBarNotification): String {
        val notification = sbn.notification
        val extras = notification.extras

        // Priority 1: Use messaging-style person name (Android 7.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val messagingStyle = Notification.MessagingStyle.extractMessagingStyleFromNotification(notification)
            if (messagingStyle != null) {
                val conversationTitle = messagingStyle.conversationTitle?.toString()
                if (!conversationTitle.isNullOrBlank()) {
                    return conversationTitle.trim()
                }
            }
        }

        // Priority 2: Use the notification title
        val title = extras.getString(Notification.EXTRA_TITLE)
            ?: extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
        if (!title.isNullOrBlank()) {
            return cleanSenderName(title)
        }

        // Priority 3: Use app name
        return try {
            val appInfo = sbn.packageName
            appInfo?.let { cleanSenderName(it) } ?: UNKNOWN_SENDER
        } catch (e: Exception) {
            UNKNOWN_SENDER
        }
    }

    /**
     * Extract the message preview from the notification.
     * Handles bigText, text, and messaging style content.
     */
    fun extractMessage(sbn: StatusBarNotification): String {
        val notification = sbn.notification
        val extras = notification.extras

        // Priority 1: Check for messaging style messages (Android 7.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val messages = extras.getParcelableArray(Notification.EXTRA_MESSAGES)
            if (!messages.isNullOrEmpty()) {
                val lastMessage = messages.lastOrNull() as? Bundle
                lastMessage?.getCharSequence("text")?.toString()?.let {
                    if (it.isNotBlank()) {
                        return truncateMessage(it)
                    }
                }
            }
        }

        // Priority 2: Use bigText (for expanded notifications)
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
        if (!bigText.isNullOrBlank()) {
            return truncateMessage(bigText)
        }

        // Priority 3: Use standard text
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
        if (!text.isNullOrBlank()) {
            return truncateMessage(text)
        }

        // Priority 4: Check for summary text
        val summaryText = extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT)?.toString()
        if (!summaryText.isNullOrBlank()) {
            return truncateMessage(summaryText)
        }

        // Priority 5: Check subText
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()
        if (!subText.isNullOrBlank()) {
            return truncateMessage(subText)
        }

        return ""
    }

    /**
     * Clean up sender name by removing common prefixes/suffixes and truncating.
     */
    private fun cleanSenderName(name: String): String {
        var cleaned = name.trim()

        // Remove common prefixes
        val prefixes = listOf("New message from ", "Message from ", "New ")
        for (prefix in prefixes) {
            if (cleaned.startsWith(prefix, ignoreCase = true)) {
                cleaned = cleaned.removePrefix(prefix)
            }
        }

        // Handle group chats format: "Group Name (3)" -> "Group Name"
        if (cleaned.contains("(") && cleaned.contains(")")) {
            val match = Regex("(.+?)\\s*\\(\\d+\\)\\s*").find(cleaned)
            if (match != null) {
                cleaned = match.groupValues[1].trim()
            }
        }

        // Truncate very long names
        return if (cleaned.length > 50) {
            cleaned.take(50) + "..."
        } else {
            cleaned
        }
    }

    /**
     * Truncate message to reasonable length for preview.
     */
    private fun truncateMessage(message: String): String {
        val trimmed = message.trim()
        return if (trimmed.length > MAX_MESSAGE_LENGTH) {
            trimmed.take(MAX_MESSAGE_LENGTH) + "..."
        } else {
            trimmed
        }
    }

    /**
     * Check if notification appears to be from a group chat.
     */
    fun isGroupChat(sbn: StatusBarNotification): Boolean {
        val title = sbn.notification.extras.getString(Notification.EXTRA_TITLE) ?: ""
        val sender = extractSender(sbn)

        // If title and sender differ significantly, might be group chat
        if (title.isNotBlank() && sender.isNotBlank() && title != sender) {
            return true
        }

        // Check for group indicators in title
        return title.contains("(") && title.contains(")") ||
                title.contains("group", ignoreCase = true)
    }
}
