package com.rigelramadhan.storyapp.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.rigelramadhan.storyapp.MainCoroutineRule
import com.rigelramadhan.storyapp.data.Result
import com.rigelramadhan.storyapp.data.remote.responses.LoginResponse
import com.rigelramadhan.storyapp.getOrAwaitValue
import com.rigelramadhan.storyapp.DummyData
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
class LoginViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = MainCoroutineRule()

    @Mock
    private lateinit var loginViewModel: LoginViewModel

    @Test
    fun `When Get LoginResponse Should Not Null And Return Success`() {
        val observer = Observer<Result<LoginResponse>> {}
        try {
            val dummyLoginResponse = DummyData.generateDummyLoginResponse()
            val expectedLoginResponse = MutableLiveData<Result<LoginResponse>>()
            expectedLoginResponse.value = Result.Success(dummyLoginResponse)
            `when`(loginViewModel.getLoginResult()).thenReturn(expectedLoginResponse)

            val actualLoginResponse = loginViewModel.getLoginResult().getOrAwaitValue()
            Mockito.verify(loginViewModel).getLoginResult()
            assertNotNull(actualLoginResponse)
            assertTrue(actualLoginResponse is Result.Success)
            assertEquals(
                dummyLoginResponse.loginResult.token,
                (actualLoginResponse as Result.Success).data.loginResult.token
            )
        } finally {
            loginViewModel.getLoginResult().removeObserver(observer)
        }
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
            Mockito.verify(loginViewModel).checkIfFirstTime()
            assertNotNull(actualCheckIfFirstTime)
            assertEquals(dummyCheckIfFirstTime, actualCheckIfFirstTime)
        } finally {
            loginViewModel.checkIfFirstTime().removeObserver(observer)
        }
    }
}