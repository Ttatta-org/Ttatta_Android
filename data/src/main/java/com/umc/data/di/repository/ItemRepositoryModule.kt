package com.umc.data.di.repository

import com.umc.core.repository.ItemRepository
import com.umc.data.api.ServerApi
import com.umc.data.implementation.repository.ItemRepositoryImpl
import com.umc.data.preference.AuthPreference
import com.umc.data.preference.ItemPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ItemRepositoryModule {
    @Provides
    @Singleton
    fun provideItemRepository(
        serverApi: ServerApi,
        authPreference: AuthPreference,
        itemPreference: ItemPreference,
    ): ItemRepository {
        return ItemRepositoryImpl(
            serverApi = serverApi,
            authPreference = authPreference,
            itemPreference = itemPreference,
        )
    }
}