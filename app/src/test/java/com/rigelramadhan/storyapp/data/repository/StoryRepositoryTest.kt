package com.rigelramadhan.storyapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.rigelramadhan.storyapp.DummyData
import com.rigelramadhan.storyapp.MainCoroutineRule
import com.rigelramadhan.storyapp.adapter.StoriesAdapter
import com.rigelramadhan.storyapp.data.Result
import com.rigelramadhan.storyapp.data.local.room.StoryDatabase
import com.rigelramadhan.storyapp.data.remote.responses.StoryEntity
import com.rigelramadhan.storyapp.data.remote.retrofit.ApiService
import com.rigelramadhan.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

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

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `When getStories Should Not Null And Return Success`() =
        mainCoroutineRules.runBlockingTest {
            val dummyToken = DummyData.generateDummyLoginResponse().loginResult.token
            val dummyStories = DummyData.generateDummyStoryEntityList()
            val pagingData = PagingData.from(dummyStories)
            val stories = MutableLiveData<PagingData<StoryEntity>>()
            stories.value = pagingData

            `when`(storyRepository.getStories(dummyToken)).thenReturn(stories)
            val actualStories = storyRepository.getStories(dummyToken).getOrAwaitValue()

            val differ = AsyncPagingDataDiffer(
                diffCallback = StoriesAdapter.StoryDiffCallback,
                updateCallback = noopListUpdateCallback,
                mainDispatcher = mainCoroutineRules.dispatcher,
                workerDispatcher = mainCoroutineRules.dispatcher
            )

            differ.submitData(actualStories)
            advanceUntilIdle()

            verify(database).storyDao().getStories()
            assertNotNull(differ.snapshot())
            assertEquals(dummyStories.size, differ.snapshot().size)
            assertEquals(dummyStories[0].id, differ.snapshot()[0]?.id)
        }

    @Test
    fun `When getStoriesWithLocation Should Not Null And Return Success`() =
        mainCoroutineRules.runBlockingTest {
            val dummyToken = DummyData.generateDummyLoginResponse().loginResult.token
            val dummyStories = DummyData.generateDummyStoryEntityList()
            val stories = MutableLiveData<Result<List<StoryEntity>>>()
            stories.value = Result.Success(dummyStories)

            `when`(storyRepository.getStoriesWithLocation(dummyToken)).thenReturn(stories)
            val actualStories = storyRepository.getStoriesWithLocation(dummyToken)
            verify(apiService).getStories(dummyToken)
            assertNotNull(actualStories)
        }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}