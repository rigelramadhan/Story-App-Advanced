package com.rigelramadhan.storyapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import com.rigelramadhan.storyapp.data.local.room.StoryDao
import com.rigelramadhan.storyapp.data.remote.responses.StoryEntity

class FakeStoryDao : StoryDao {
    private var storiesData = mutableListOf<StoryEntity>()

    override fun getStoriesAsLiveData(): LiveData<List<StoryEntity>> {
        val observableStories = MutableLiveData<List<StoryEntity>>()
        observableStories.value = storiesData
        return observableStories
    }

    override fun getStories(): PagingSource<Int, StoryEntity> {
        TODO()
    }

    override suspend fun insertStories(stories: List<StoryEntity>) {
        storiesData.addAll(stories)
    }

    override suspend fun deleteAllStories() {
        storiesData.removeAll(storiesData)
    }

    override suspend fun getCount() = storiesData.size
}