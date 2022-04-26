package com.rigelramadhan.storyapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.rigelramadhan.storyapp.DummyData
import com.rigelramadhan.storyapp.data.Result
import com.rigelramadhan.storyapp.data.remote.responses.StoryEntity
import com.rigelramadhan.storyapp.data.repository.StoryRepository
import com.rigelramadhan.storyapp.data.repository.UserRepository
import com.rigelramadhan.storyapp.getOrAwaitValue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class StoryMapViewModelTest {
    @get:Rule
    var instantTaskExecutor = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    @Mock
    private lateinit var userRepository: UserRepository
    private lateinit var storyMapViewModel: StoryMapViewModel

    @Before
    fun setUp() {
        storyMapViewModel = StoryMapViewModel(storyRepository, userRepository)
    }

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
            verify(storyRepository).getStoriesWithLocation(dummyToken)
            assertNotNull(actualStoryMapData)
            assertTrue(actualStoryMapData is Result.Success)
            assertEquals(dummyStoryMapData, (actualStoryMapData as Result.Success).data)
            assertEquals(dummyStoryMapData[0].id, actualStoryMapData.data[0].id)
        } finally {
            storyMapViewModel.getStories(dummyToken).removeObserver(observer)
        }
    }

    @Test
    fun `When Get Token Should Not Null And Should Return String`() {
        val observer = Observer<String> {}
        try {
            val dummyToken = DummyData.generateDummyLoginResponse().loginResult.token
            val expectedToken = MutableLiveData<String>()
            expectedToken.value = dummyToken
            `when`(storyMapViewModel.getToken()).thenReturn(expectedToken)

            val actualToken = storyMapViewModel.getToken().getOrAwaitValue()
            verify(userRepository).getToken()
            assertNotNull(actualToken)
            assertEquals(dummyToken, actualToken)
        } finally {
            storyMapViewModel.getToken().removeObserver(observer)
        }
    }
}