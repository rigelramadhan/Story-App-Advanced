package com.rigelramadhan.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.rigelramadhan.storyapp.data.local.datastore.LoginPreferences
import com.rigelramadhan.storyapp.data.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val loginPreferences: LoginPreferences
) : ViewModel() {
    @ExperimentalPagingApi
    fun getStories(token: String) = storyRepository.getStories(token)

    fun checkIfTokenAvailable(): LiveData<String> {
        return loginPreferences.getToken().asLiveData()
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            loginPreferences.deleteToken()
        }
    }
}