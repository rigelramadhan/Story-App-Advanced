package com.rigelramadhan.storyapp.ui.register

import androidx.lifecycle.ViewModel
import com.rigelramadhan.storyapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class RegisterViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {
    fun getRegisterResult() = userRepository.getRegisterResult()

    fun registerUser(name: String, email: String, password: String) =
        userRepository.register(name, email, password)
}