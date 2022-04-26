package com.rigelramadhan.storyapp.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rigelramadhan.storyapp.data.local.datastore.LoginPreferences
import com.rigelramadhan.storyapp.data.repository.StoryRepository
import com.rigelramadhan.storyapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
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

    fun checkIfTokenAvailable(): LiveData<String> {
        return userRepository.getToken()
    }
}