package com.rigelramadhan.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import com.rigelramadhan.storyapp.data.Result
import com.rigelramadhan.storyapp.data.remote.responses.AddStoryResponse
import com.rigelramadhan.storyapp.data.remote.responses.StoryEntity
import com.rigelramadhan.storyapp.DummyData
import java.io.File

class FakeStoryRepository : BaseStoryRepository {
    private var storiesData = mutableListOf<StoryEntity>()

    override fun getStories(token: String): LiveData<PagingData<StoryEntity>> {
        val observablePagingDataStories = MutableLiveData<PagingData<StoryEntity>>()
        observablePagingDataStories.value = PagingData.from(storiesData)
        return observablePagingDataStories
    }

    override fun getStoriesWithLocation(token: String): LiveData<Result<List<StoryEntity>>> {
        val observableStories = MutableLiveData<Result<List<StoryEntity>>>()
        observableStories.value = Result.Success(storiesData)
        return observableStories
    }

    override fun postStory(
        token: String,
        imageFile: File,
        description: String,
        lat: Float?,
        lon: Float?
    ): LiveData<Result<AddStoryResponse>> {
        val observableAddStoryResponse = MutableLiveData<Result<AddStoryResponse>>()
        observableAddStoryResponse.value = Result.Success(DummyData.generateDummyAddStoryResponse())
        return observableAddStoryResponse
    }
}