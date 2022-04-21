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
    private val userRepository: UserRepository,
    private val loginPreferences: LoginPreferences
) : ViewModel() {
    fun login(email: String, password: String) = userRepository.login(email, password)

    fun register(name: String, email: String, password: String) =
        userRepository.register(name, email, password)

    fun saveToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loginPreferences.saveToken(token)
        }
    }

    fun checkIfFirstTime(): LiveData<Boolean> {
        return loginPreferences.isFirstTime().asLiveData()
    }
}