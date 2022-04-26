package com.rigelramadhan.storyapp.ui.welcome

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.rigelramadhan.storyapp.data.repository.UserRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.doNothing
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class WelcomeViewModelTest {
    @get:Rule
    var instantTaskExecutor = InstantTaskExecutorRule()

    @Mock
    private lateinit var userRepository: UserRepository
    private lateinit var welcomeViewModel: WelcomeViewModel

    @Before
    fun setUp() {
        welcomeViewModel = WelcomeViewModel(userRepository)
    }

    @Test
    fun `When setFirstTime Should Run In Repository Once`() {
        val dummyBoolean = true
        doNothing().`when`(userRepository).setFirstTime(dummyBoolean)
        welcomeViewModel.setFirstTime(dummyBoolean)
        Mockito.verify(userRepository).setFirstTime(dummyBoolean)
    }
}