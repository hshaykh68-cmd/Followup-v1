package com.followup.presentation.onboarding

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.followup.service.notification.NotificationPermissionHelper
import kotlinx.coroutines.delay

/**
 * Onboarding screen specifically for notification listener permission.
 * Guides users to enable the permission with clear explanations.
 */
@Composable
fun NotificationPermissionScreen(
    notificationPermissionHelper: NotificationPermissionHelper,
    onPermissionGranted: () -> Unit,
    onSkip: () -> Unit
) {
    val context = LocalContext.current
    var isEnabled by remember { mutableStateOf(notificationPermissionHelper.isNotificationListenerEnabled()) }
    var showSuccess by remember { mutableStateOf(false) }

    // Check permission status periodically
    LaunchedEffect(Unit) {
        while (true) {
            val newStatus = notificationPermissionHelper.isNotificationListenerEnabled()
            if (newStatus != isEnabled) {
                isEnabled = newStatus
                if (isEnabled) {
                    showSuccess = true
                    delay(1500)
                    onPermissionGranted()
                }
            }
            delay(500)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section with illustration
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                // Animated illustration
                NotificationIllustration(isEnabled = isEnabled)

                Spacer(modifier = Modifier.height(32.dp))

                // Title
                Text(
                    text = if (isEnabled) "All set!" else "Enable auto-capture",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = if (isEnabled) {
                        "You'll now see 'Remind later' actions on message notifications."
                    } else {
                        "FollowUp can automatically capture reminders from your messages. " +
                                "Enable notification access to get started."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Middle section - Privacy info card
            if (!isEnabled) {
                PrivacyInfoCard()
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Bottom section - Actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = !isEnabled,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut()
                ) {
                    Column {
                        // Primary action button
                        Button(
                            onClick = {
                                if (context is Activity) {
                                    notificationPermissionHelper.openNotificationListenerSettings(context)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Enable in Settings",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Skip button
                        TextButton(
                            onClick = onSkip,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Skip for now",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = showSuccess,
                    enter = scaleIn() + fadeIn()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Permission granted!",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun NotificationIllustration(isEnabled: Boolean) {
    val scale by animateFloatAsState(
        targetValue = if (isEnabled) 1.1f else 1f,
        animationSpec = tween(500),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isEnabled) 1f else 0.7f,
        animationSpec = tween(300),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .size(200.dp)
            .scale(scale)
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {
        // Background glow
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = if (isEnabled) {
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.0f)
                            )
                        } else {
                            listOf(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.0f)
                            )
                        }
                    )
                )
        )

        // Central icon
        Surface(
            shape = CircleShape,
            color = if (isEnabled) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            modifier = Modifier.size(80.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isEnabled) {
                        Icons.Default.NotificationsActive
                    } else {
                        Icons.Outlined.Notifications
                    },
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = if (isEnabled) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }

        // Notification badges around the central icon
        if (!isEnabled) {
            NotificationBadge(
                modifier = Modifier.align(Alignment.TopStart)
            )
            NotificationBadge(
                modifier = Modifier.align(Alignment.TopEnd)
            )
            NotificationBadge(
                modifier = Modifier.align(Alignment.BottomStart)
            )
            NotificationBadge(
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
private fun NotificationBadge(modifier: Modifier = Modifier) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.error,
        modifier = modifier.size(16.dp)
    ) {}
}

@Composable
private fun PrivacyInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Your privacy matters",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• Only reads message notifications (WhatsApp, SMS, etc.)\n" +
                            "• Never stores full conversations\n" +
                            "• All data stays on your device",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}
