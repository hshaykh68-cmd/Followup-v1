package com.followup.service.di

import com.followup.service.notification.NotificationActionHelper
import com.followup.service.notification.NotificationPermissionHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for service layer dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun provideNotificationPermissionHelper(
        context: android.content.Context
    ): NotificationPermissionHelper {
        return NotificationPermissionHelper(context)
    }

    @Provides
    @Singleton
    fun provideNotificationActionHelper(
        context: android.content.Context
    ): NotificationActionHelper {
        return NotificationActionHelper(context)
    }
}
