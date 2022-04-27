package com.rigelramadhan.storyapp.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rigelramadhan.storyapp.data.repository.StoryRepository
import com.rigelramadhan.storyapp.data.repository.UserRepository
import com.rigelramadhan.storyapp.di.AppModule

class StoryMapViewModel(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    fun getStories(token: String) = storyRepository.getStoriesWithLocation(token)

    fun getToken(): LiveData<String> {
        return userRepository.getToken()
    }

    class StoryMapViewModelFactory private constructor(
        private val storyRepository: StoryRepository,
        private val userRepository: UserRepository
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StoryMapViewModel::class.java)) {
                return StoryMapViewModel(storyRepository, userRepository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: StoryMapViewModelFactory? = null

            fun getInstance(
                context: Context
            ): StoryMapViewModelFactory = instance ?: synchronized(this) {
                instance ?: StoryMapViewModelFactory(
                    AppModule.provideStoryRepository(context),
                    AppModule.provideUserRepository(context)
                )
            }.also { instance = it }
        }
    }
}