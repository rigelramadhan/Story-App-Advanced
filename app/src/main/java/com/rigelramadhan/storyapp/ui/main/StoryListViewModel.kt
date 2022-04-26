package com.rigelramadhan.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.rigelramadhan.storyapp.data.repository.StoryRepository
import com.rigelramadhan.storyapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoryListViewModel @Inject constructor(private val storyRepository: StoryRepository, private val userRepository: UserRepository) :
    ViewModel() {
    @ExperimentalPagingApi
    fun getStories(token: String) = storyRepository.getStories(token)

    fun getToken(): LiveData<String> {
        return userRepository.getToken()
    }
}