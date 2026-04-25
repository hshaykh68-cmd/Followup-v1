package com.followup.data.di

import android.content.Context
import androidx.room.Room
import com.followup.data.local.AppDatabase
import com.followup.data.local.dao.ReminderDao
import com.followup.data.repository.ReminderRepositoryImpl
import com.followup.data.repository.SettingsRepositoryImpl
import com.followup.domain.repository.ReminderRepository
import com.followup.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for database and repository dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    companion object {
        @Provides
        @Singleton
        fun provideAppDatabase(
            @ApplicationContext context: Context
        ): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                AppDatabase.DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        @Provides
        fun provideReminderDao(database: AppDatabase): ReminderDao {
            return database.reminderDao()
        }
    }

    @Binds
    @Singleton
    abstract fun bindReminderRepository(
        impl: ReminderRepositoryImpl
    ): ReminderRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository
}
