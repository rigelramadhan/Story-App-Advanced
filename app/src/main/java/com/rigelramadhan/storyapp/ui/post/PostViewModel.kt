package com.rigelramadhan.storyapp.ui.post

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rigelramadhan.storyapp.data.repository.StoryRepository
import com.rigelramadhan.storyapp.data.repository.UserRepository
import com.rigelramadhan.storyapp.di.AppModule
import java.io.File

class PostViewModel (
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    fun postStory(
        token: String,
        imageFile: File,
        description: String,
        lat: Float? = null,
        lon: Float? = null
    ) =
        storyRepository.postStory(token, imageFile, description, lat, lon)

    fun getToken(): LiveData<String> {
        return userRepository.getToken()
    }

    class PostViewModelFactory private constructor(
        private val storyRepository: StoryRepository,
        private val userRepository: UserRepository
    ) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
                return PostViewModel(storyRepository, userRepository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: PostViewModelFactory? = null

            fun getInstance(
                context: Context
            ): PostViewModelFactory =
                instance ?: synchronized(this) {
                    instance ?: PostViewModelFactory(
                        AppModule.provideStoryRepository(context),
                        AppModule.provideUserRepository(context)
                    )
                }.also { instance = it }
        }
    }
}