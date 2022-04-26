package com.rigelramadhan.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.rigelramadhan.storyapp.data.Result
import com.rigelramadhan.storyapp.data.remote.responses.AddStoryResponse
import com.rigelramadhan.storyapp.data.remote.responses.StoryEntity
import java.io.File

interface StoryDataSource {
    fun getStories(token: String): LiveData<PagingData<StoryEntity>>

    fun getStoriesWithLocation(token: String): LiveData<Result<List<StoryEntity>>>

    fun postStory(
        token: String,
        imageFile: File,
        description: String,
        lat: Float?,
        lon: Float?
    ): LiveData<Result<AddStoryResponse>>
}