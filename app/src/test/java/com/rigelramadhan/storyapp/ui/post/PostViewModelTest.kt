package com.rigelramadhan.storyapp.ui.post

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.rigelramadhan.storyapp.data.local.datastore.LoginPreferences
import com.rigelramadhan.storyapp.data.repository.StoryRepository
import com.rigelramadhan.storyapp.DummyData
import com.rigelramadhan.storyapp.data.Result
import com.rigelramadhan.storyapp.data.remote.responses.AddStoryResponse
import com.rigelramadhan.storyapp.data.remote.responses.StoryEntity
import com.rigelramadhan.storyapp.data.repository.UserRepository
import com.rigelramadhan.storyapp.getOrAwaitValue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class PostViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var userRepository: UserRepository
    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var postViewModel: PostViewModel

    @Before
    fun setUp() {
        postViewModel = PostViewModel(storyRepository, userRepository)
    }

    @Test
    fun `When postStory Should Not Null And Return Success`() {
        val observer = Observer<Result<AddStoryResponse>> {}
        val dummyToken = DummyData.generateDummyLoginResponse().loginResult.token
        try {
            val dummyAddStoryResponse = DummyData.generateDummyAddStoryResponse()
            val expectedAddStoryResponse = MutableLiveData<Result<AddStoryResponse>>()
            expectedAddStoryResponse.value = Result.Success(dummyAddStoryResponse)
            Mockito.`when`(
                postViewModel.postStory(
                    dummyToken,
                    File(""),
                    "This is description",
                    100f,
                    100f
                )
            ).thenReturn(expectedAddStoryResponse)

            val actualAddStoryResponse = postViewModel.postStory(
                dummyToken,
                File(""),
                "This is description",
                100f,
                100f
            ).getOrAwaitValue()
            Mockito.verify(storyRepository).postStory(
                dummyToken,
                File(""),
                "This is description",
                100f,
                100f
            )
            Assert.assertNotNull(actualAddStoryResponse)
            Assert.assertTrue(actualAddStoryResponse is Result.Success)
            Assert.assertEquals(
                dummyAddStoryResponse,
                (actualAddStoryResponse as Result.Success).data
            )
            Assert.assertEquals(dummyAddStoryResponse.message, actualAddStoryResponse.data.message)
        } finally {
            postViewModel.postStory(
                dummyToken,
                File(""),
                "This is description",
                100f,
                100f
            ).removeObserver(observer)
        }
    }

    @Test
    fun `When getToken Should Not Null And Return String`() {
        val observer = Observer<String> {}
        try {
            val dummyToken = DummyData.generateDummyLoginResponse().loginResult.token
            val expectedToken = MutableLiveData<String>()
            expectedToken.value = dummyToken
            Mockito.`when`(postViewModel.getToken()).thenReturn(expectedToken)

            val actualToken = postViewModel.getToken().getOrAwaitValue()
            Mockito.verify(userRepository).getToken()
            Assert.assertNotNull(actualToken)
            Assert.assertEquals(dummyToken, actualToken)
        } finally {
            postViewModel.getToken().removeObserver(observer)
        }
    }
}