package com.rigelramadhan.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.rigelramadhan.storyapp.data.local.datastore.LoginPreferences
import com.rigelramadhan.storyapp.data.local.room.StoryDatabase
import com.rigelramadhan.storyapp.data.remote.retrofit.ApiConfig
import com.rigelramadhan.storyapp.data.remote.retrofit.ApiService
import com.rigelramadhan.storyapp.data.repository.StoryRepository
import com.rigelramadhan.storyapp.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

private const val LOGIN_PREFERENCES = "login"

object AppModule {
    private fun provideDatabase(context: Context): StoryDatabase {
        return StoryDatabase.getInstance(context)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        return StoryRepository.getInstance(provideApiService(), provideDatabase(context))
    }

    fun provideUserRepository(context: Context): UserRepository {
        return UserRepository.getInstance(
            provideApiService(),
            provideLoginPreferences(context)
        )
    }

    private fun providesPreferencesDataStore(context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(produceNewData = { emptyPreferences() }),
            migrations = listOf(SharedPreferencesMigration(context, LOGIN_PREFERENCES)),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile(LOGIN_PREFERENCES) })
    }

    private fun provideLoginPreferences(context: Context): LoginPreferences = LoginPreferences.getInstance(
        providesPreferencesDataStore(context)
    )

    private fun provideApiService(): ApiService {
        return ApiConfig.getApiService()
    }
}