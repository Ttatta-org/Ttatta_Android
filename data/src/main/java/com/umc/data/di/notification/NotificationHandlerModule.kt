package com.umc.data.di.notification

import android.content.Context
import com.umc.core.notification.NotificationHandler
import com.umc.data.notification.FirebaseNotificationHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationHandlerModule {
    @Provides
    @Singleton
    fun provideNotificationHandler(
        @ApplicationContext context: Context
    ): NotificationHandler {
        return FirebaseNotificationHandler(context = context)
    }
}