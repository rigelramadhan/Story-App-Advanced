package com.rigelramadhan.storyapp.ui.welcome

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rigelramadhan.storyapp.data.local.datastore.LoginPreferences
import com.rigelramadhan.storyapp.data.repository.UserRepository
import com.rigelramadhan.storyapp.di.AppModule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

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