package com.rigelramadhan.storyapp.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rigelramadhan.storyapp.data.local.datastore.LoginPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class WelcomeViewModel @Inject constructor(private val loginPreferences: LoginPreferences) : ViewModel() {

    fun setFirstTime(firstTime: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            loginPreferences.setFirstTime(firstTime)
        }
    }
}