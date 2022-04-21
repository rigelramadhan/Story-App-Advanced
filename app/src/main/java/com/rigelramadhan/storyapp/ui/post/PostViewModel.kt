package com.rigelramadhan.storyapp.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rigelramadhan.storyapp.data.local.datastore.LoginPreferences
import com.rigelramadhan.storyapp.data.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val loginPreferences: LoginPreferences
) : ViewModel() {
    fun postStory(token: String, imageFile: File, description: String) =
        storyRepository.postStory(token, imageFile, description)

    fun checkIfTokenAvailable(): LiveData<String> {
        return loginPreferences.getToken().asLiveData()
    }
}