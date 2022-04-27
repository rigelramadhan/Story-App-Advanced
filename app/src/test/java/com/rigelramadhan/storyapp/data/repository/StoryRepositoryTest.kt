package com.rigelramadhan.storyapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import app.cash.turbine.test
import com.rigelramadhan.storyapp.DummyData
import com.rigelramadhan.storyapp.MainCoroutineRule
import com.rigelramadhan.storyapp.adapter.StoriesAdapter
import com.rigelramadhan.storyapp.data.Result
import com.rigelramadhan.storyapp.data.local.room.StoryDao
import com.rigelramadhan.storyapp.data.local.room.StoryDatabase
import com.rigelramadhan.storyapp.data.local.room.StoryRemoteKeysDao
import com.rigelramadhan.storyapp.data.remote.responses.StoryEntity
import com.rigelramadhan.storyapp.data.remote.retrofit.ApiService
import com.rigelramadhan.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = MainCoroutineRule()

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var database: StoryDatabase
    private lateinit var storyRepository: StoryRepository

    @Before
    fun setUp() {
        storyRepository = StoryRepository(apiService, database)
    }

    @Test
    fun `When getStoriesWithLocation Should Not Null And Return Success`() = runTest {
        val dummyToken = DummyData.generateDummyLoginResponse().loginResult.token
        val dummyStories = DummyData.generateDummyStoriesResponse()
        val expectedStories = MutableLiveData<Result<List<StoryEntity>>>()
        expectedStories.value = Result.Success(dummyStories.story)

        `when`(apiService.getStories(dummyToken)).thenReturn(dummyStories)
        storyRepository.getStoriesWithLocation(dummyToken).asFlow().test {
            val item = awaitItem()
            assertTrue(item is Result.Success)
            assertEquals(item, expectedStories.value)
        }
        verify(apiService).getStories(dummyToken)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}