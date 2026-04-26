package com.followup.service.notification.di

import com.followup.service.notification.NotificationActionHelper
import com.followup.service.notification.NotificationFilter
import com.followup.service.notification.NotificationExtractor
import com.followup.service.notification.NotificationPermissionHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped

/**
 * Hilt module for notification service dependencies.
 */
@Module
@InstallIn(ServiceComponent::class)
object NotificationServiceModule {

    @Provides
    @ServiceScoped
    fun provideNotificationFilter(): NotificationFilter {
        return NotificationFilter()
    }

    @Provides
    @ServiceScoped
    fun provideNotificationExtractor(): NotificationExtractor {
        return NotificationExtractor()
    }
}
