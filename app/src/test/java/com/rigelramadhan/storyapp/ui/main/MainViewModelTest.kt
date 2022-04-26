package com.rigelramadhan.storyapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.rigelramadhan.storyapp.DummyData
import com.rigelramadhan.storyapp.MainCoroutineRule
import com.rigelramadhan.storyapp.data.repository.UserRepository
import com.rigelramadhan.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = MainCoroutineRule()

    @Mock
    private lateinit var userRepository: UserRepository
    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(userRepository)
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
            verify(userRepository).getToken()
            assertNotNull(actualToken)
            assertEquals(dummyToken, actualToken)
        } finally {
            mainViewModel.checkIfTokenAvailable().removeObserver(observer)
        }
    }

    @Test
    fun `When logout Should Remove Token`() {
        doNothing().`when`(userRepository).deleteToken()
        mainViewModel.logout()
        verify(userRepository).deleteToken()
    }
}