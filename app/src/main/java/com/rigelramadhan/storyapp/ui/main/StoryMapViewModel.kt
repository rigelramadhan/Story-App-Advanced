package com.rigelramadhan.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rigelramadhan.storyapp.data.local.datastore.LoginPreferences
import com.rigelramadhan.storyapp.data.repository.StoryRepository
import com.rigelramadhan.storyapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoryMapViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    fun getStories(token: String) = storyRepository.getStoriesWithLocation(token)

    fun getToken(): LiveData<String> {
        return userRepository.getToken()
    }
}