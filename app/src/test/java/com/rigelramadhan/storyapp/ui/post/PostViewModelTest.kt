package com.rigelramadhan.storyapp.ui.post

import com.rigelramadhan.storyapp.data.local.datastore.LoginPreferences
import com.rigelramadhan.storyapp.data.repository.StoryRepository
import com.rigelramadhan.storyapp.DummyData
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PostViewModelTest {
    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var loginPreferences: LoginPreferences
    private lateinit var postViewModel: PostViewModel
    private val dummyAddStoryResponse = DummyData.generateDummyAddStoryResponse()

    @Before
    fun setUp() {
        postViewModel = PostViewModel(storyRepository, loginPreferences)
    }

}