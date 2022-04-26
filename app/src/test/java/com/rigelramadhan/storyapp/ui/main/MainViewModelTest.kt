package com.rigelramadhan.storyapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.rigelramadhan.storyapp.MainCoroutineRule
import com.rigelramadhan.storyapp.adapter.StoriesAdapter
import com.rigelramadhan.storyapp.data.remote.responses.StoryEntity
import com.rigelramadhan.storyapp.getOrAwaitValue
import com.rigelramadhan.storyapp.DummyData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
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
class MainViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = MainCoroutineRule()

    @Mock
    private lateinit var mainViewModel: MainViewModel

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `When Get Stories Should Not Null`() = mainCoroutineRules.runBlockingTest {
        val dummyToken = DummyData.generateDummyLoginResponse().loginResult.token
        val dummyStories = DummyData.generateDummyStoryEntityList()
        val data = PagingData.from(dummyStories)
        val stories = MutableLiveData<PagingData<StoryEntity>>()
        stories.value = data

        `when`(mainViewModel.getStories(dummyToken)).thenReturn(stories)
        val actualStories = mainViewModel.getStories(dummyToken).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.StoryDiffCallback,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainCoroutineRules.dispatcher,
            workerDispatcher = mainCoroutineRules.dispatcher
        )

        differ.submitData(actualStories)
        advanceUntilIdle()

        Mockito.verify(mainViewModel).getStories(dummyToken)
        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories[0].id, differ.snapshot()[0]?.id)
    }

    @Test
    fun `When Get Token Should Not Null And Should Return String`() {
        val observer = Observer<String> {}
        try {
            val dummyToken = DummyData.generateDummyLoginResponse().loginResult.token
            val expectedToken = MutableLiveData<String>()
            expectedToken.value = dummyToken
            `when`(mainViewModel.checkIfTokenAvailable()).thenReturn(expectedToken)

            val actualToken = mainViewModel.checkIfTokenAvailable().getOrAwaitValue()
            Mockito.verify(mainViewModel).checkIfTokenAvailable()
            assertNotNull(actualToken)
            assertEquals(dummyToken, actualToken)
        } finally {
            mainViewModel.checkIfTokenAvailable().removeObserver(observer)
        }
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}