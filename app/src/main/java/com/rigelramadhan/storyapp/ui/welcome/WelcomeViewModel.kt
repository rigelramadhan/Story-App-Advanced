package com.rigelramadhan.storyapp.ui.welcome

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rigelramadhan.storyapp.data.repository.UserRepository
import com.rigelramadhan.storyapp.di.AppModule

class WelcomeViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun setFirstTime(firstTime: Boolean) {
        userRepository.setFirstTime(firstTime)
    }

    class WelcomeViewModelFactory private constructor(private val userRepository: UserRepository) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WelcomeViewModel::class.java)) {
                return WelcomeViewModel(userRepository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: WelcomeViewModelFactory? = null

            fun getInstance(context: Context): WelcomeViewModelFactory =
                instance ?: synchronized(this) {
                    instance ?: WelcomeViewModelFactory(AppModule.provideUserRepository(context))
                }.also { instance = it }
        }
    }
}