package com.rigelramadhan.storyapp.ui.storymap

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.rigelramadhan.storyapp.MainCoroutineRule
import com.rigelramadhan.storyapp.data.Result
import com.rigelramadhan.storyapp.data.remote.responses.StoryEntity
import com.rigelramadhan.storyapp.getOrAwaitValue
import com.rigelramadhan.storyapp.DummyData
import com.rigelramadhan.storyapp.ui.main.StoryMapViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryMapViewModelTest {
    @get:Rule
    var instantTaskExecutor = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var storyMapViewModel: StoryMapViewModel

    @Test
    fun `When Get StoryMap Should Not Null And Return Success`() {
        val observer = Observer<Result<List<StoryEntity>>> {}
        val dummyToken = DummyData.generateDummyLoginResponse().loginResult.token
        try {
            val dummyStoryMapData = DummyData.generateDummyStoryEntityList()
            val expectedStoryMapData = MutableLiveData<Result<List<StoryEntity>>>()
            expectedStoryMapData.value = Result.Success(dummyStoryMapData)
            `when`(storyMapViewModel.getStories(dummyToken)).thenReturn(expectedStoryMapData)

            val actualStoryMapData = storyMapViewModel.getStories(dummyToken).getOrAwaitValue()
            Mockito.verify(storyMapViewModel).getStories(dummyToken)
            assertNotNull(actualStoryMapData)
            assertTrue(actualStoryMapData is Result.Success)
            assertEquals(dummyStoryMapData, (actualStoryMapData as Result.Success).data)
            assertEquals(dummyStoryMapData[0].id, actualStoryMapData.data[0].id)
        } finally {
            storyMapViewModel.getStories(dummyToken).removeObserver(observer)
        }
    }
}