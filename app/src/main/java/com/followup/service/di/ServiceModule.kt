package com.followup.service.di

import android.content.Context
import com.followup.service.notification.NotificationActionHelper
import com.followup.service.notification.NotificationPermissionHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
        @ApplicationContext context: Context
    ): NotificationPermissionHelper {
        return NotificationPermissionHelper(context)
    }

    @Provides
    @Singleton
    fun provideNotificationActionHelper(
        @ApplicationContext context: Context
    ): NotificationActionHelper {
        return NotificationActionHelper(context)
    }
}
