package com.rigelramadhan.storyapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rigelramadhan.storyapp.DummyData
import com.rigelramadhan.storyapp.adapter.StoriesAdapter
import com.rigelramadhan.storyapp.data.local.room.StoryDatabase
import com.rigelramadhan.storyapp.data.remote.responses.StoryEntity
import com.rigelramadhan.storyapp.data.remote.retrofit.ApiService
import com.rigelramadhan.storyapp.data.remote.retrofit.FakeApiService
import com.rigelramadhan.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class StoryRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var apiService: ApiService
    private lateinit var database: StoryDatabase
    private lateinit var storyRepository: StoryRepository

    @Before
    fun setUp() {
        apiService = FakeApiService()
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StoryDatabase::class.java
        ).build()
        storyRepository = StoryRepository(apiService, database)
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun a() = runBlockingTest {
        val dummyToken = DummyData.generateDummyLoginResponse().loginResult.token
        val dummyStories = DummyData.generateDummyStoryEntityList()
        val data = PagingData.from(dummyStories)
        val stories = MutableLiveData<PagingData<StoryEntity>>()
        stories.value = data

        val actualStories = storyRepository.getStories(dummyToken).getOrAwaitValue()
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}