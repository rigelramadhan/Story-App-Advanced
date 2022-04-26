package com.rigelramadhan.storyapp.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.rigelramadhan.storyapp.DummyData
import com.rigelramadhan.storyapp.MainCoroutineRule
import com.rigelramadhan.storyapp.data.Result
import com.rigelramadhan.storyapp.data.remote.responses.LoginResponse
import com.rigelramadhan.storyapp.data.repository.UserRepository
import com.rigelramadhan.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
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
class LoginViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = MainCoroutineRule()

    @Mock
    private lateinit var userRepository: UserRepository
    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(userRepository)
    }

    @Test
    fun `When Login Response Should Not Null And Return Success`() {
        val observer = Observer<Result<LoginResponse>> {}
        val dummyEmail = "email@mail.com"
        val dummyPassword = "123456"
        try {
            val dummyLoginResponse = DummyData.generateDummyLoginResponse()
            val expectedLoginResponse = MutableLiveData<Result<LoginResponse>>()
            expectedLoginResponse.value = Result.Success(dummyLoginResponse)
            `when`(
                loginViewModel.login(
                    dummyEmail,
                    dummyPassword
                )
            ).thenReturn(expectedLoginResponse)

            val actualLoginResponse =
                loginViewModel.login(dummyEmail, dummyPassword).getOrAwaitValue()
            verify(userRepository).login(dummyEmail, dummyPassword)
            assertNotNull(actualLoginResponse)
            assertTrue(actualLoginResponse is Result.Success)
            assertEquals(
                dummyLoginResponse.loginResult.token,
                (actualLoginResponse as Result.Success).data.loginResult.token
            )
        } finally {
            loginViewModel.login(dummyEmail, dummyPassword).removeObserver(observer)
        }
    }

    @Test
    fun `When login Info Invalid Should Return Error`() {
        val observer = Observer<Result<LoginResponse>> {}
        val dummyEmail = "email@mail.com"
        val dummyPassword = "123"
        try {
            val dummyLoginResponse = DummyData.generateDummyErrorLoginResponse()
            val expectedLoginResponse = MutableLiveData<Result<LoginResponse>>()
            expectedLoginResponse.value = Result.Error("Invalid password")
            `when`(
                loginViewModel.login(
                    dummyEmail,
                    dummyPassword
                )
            ).thenReturn(expectedLoginResponse)

            val actualLoginResponse =
                loginViewModel.login(dummyEmail, dummyPassword).getOrAwaitValue()
            verify(userRepository).login(dummyEmail, dummyPassword)
            assertNotNull(actualLoginResponse)
            assertTrue(actualLoginResponse is Result.Error)
            assertEquals(
                dummyLoginResponse.message,
                (actualLoginResponse as Result.Error).error
            )
        } finally {
            loginViewModel.login(dummyEmail, dummyPassword).removeObserver(observer)
        }
    }

    @Test
    fun `When saveToken Should Run In Repository Once`() {
        val dummyToken = DummyData.generateDummyLoginResponse().loginResult.token
        doNothing().`when`(userRepository).saveToken(dummyToken)
        loginViewModel.saveToken(dummyToken)
        verify(userRepository).saveToken(dummyToken)
    }

    @Test
    fun `When CheckIfFirstTime Should Not Null`() {
        val observer = Observer<Boolean> {}
        try {
            val dummyCheckIfFirstTime = true
            val expectedCheckIfFirstTime = MutableLiveData<Boolean>()
            expectedCheckIfFirstTime.value = dummyCheckIfFirstTime
            `when`(loginViewModel.checkIfFirstTime()).thenReturn(expectedCheckIfFirstTime)

            val actualCheckIfFirstTime = loginViewModel.checkIfFirstTime().getOrAwaitValue()
            verify(userRepository).isFirstTime()
            assertNotNull(actualCheckIfFirstTime)
            assertEquals(dummyCheckIfFirstTime, actualCheckIfFirstTime)
        } finally {
            loginViewModel.checkIfFirstTime().removeObserver(observer)
        }
    }
}