package com.rigelramadhan.storyapp.ui.login

import androidx.lifecycle.*
import com.rigelramadhan.storyapp.data.local.datastore.LoginPreferences
import com.rigelramadhan.storyapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    fun login(email: String, password: String) = userRepository.login(email, password)

    fun getLoginResult() = userRepository.getLoginResult()

    fun saveToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.saveToken(token)
        }
    }

    fun checkIfFirstTime(): LiveData<Boolean> {
        return userRepository.isFirstTime()
    }
}