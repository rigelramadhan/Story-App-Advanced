package com.rigelramadhan.storyapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import app.cash.turbine.test
import com.rigelramadhan.storyapp.DummyData
import com.rigelramadhan.storyapp.MainCoroutineRule
import com.rigelramadhan.storyapp.data.Result
import com.rigelramadhan.storyapp.data.local.datastore.LoginPreferences
import com.rigelramadhan.storyapp.data.remote.responses.LoginResponse
import com.rigelramadhan.storyapp.data.remote.responses.RegisterResponse
import com.rigelramadhan.storyapp.data.remote.retrofit.ApiService
import com.rigelramadhan.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UserRepositoryTest {
    private val dummyName = "Test"
    private val dummyEmail = "test@email.com"
    private val dummyPassword = "123456"

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = MainCoroutineRule()

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var loginPreferences: LoginPreferences
    private lateinit var userRepository: UserRepository

    @Before
    fun setUp() {
        userRepository = UserRepository(apiService, loginPreferences)
    }

    @Test
    fun `When Register Info Valid Should Return Success`() = runTest {
        val dummyData = DummyData.generateDummyRegisterResponse()
        val expectedResult = MutableLiveData<Result<RegisterResponse>>()
        expectedResult.value = Result.Success(dummyData)

        `when`(apiService.register(anyString(), anyString(), anyString())).thenReturn(
            dummyData
        )

        userRepository.register(dummyName, dummyEmail, dummyPassword).asFlow().test {
            val item = awaitItem()
            assertTrue(item is Result.Success)
            assertEquals(Result.Success(dummyData), item)
        }

        verify(apiService).register(dummyName, dummyEmail, dummyPassword)
    }

    @Test
    fun `When login Info Valid Should Return Success`() = runTest {
        val dummyResponse = DummyData.generateDummyLoginResponse()
        val expectedResult = MutableLiveData<Result<LoginResponse>>()
        expectedResult.value = Result.Success(dummyResponse)

        `when`(apiService.login(anyString(), anyString())).thenReturn(
            dummyResponse
        )

        userRepository.login(dummyEmail, dummyPassword).asFlow().test {
            val item = awaitItem()
            assertTrue(item is Result.Success)
            assertEquals(Result.Success(dummyResponse), item)
        }

        verify(apiService).login(dummyEmail, dummyPassword)
    }

    @Test
    fun `When getToken Should Return String`() {
        val dummyToken = DummyData.generateDummyLoginResponse().loginResult.token
        `when`(loginPreferences.getToken()).thenReturn(flow { emit(dummyToken) })

        val actualToken = userRepository.getToken().getOrAwaitValue()
        verify(loginPreferences).getToken()
        assertEquals(dummyToken, actualToken)
    }

    @Test
    fun `When deleteToken Should Run In Preference Once`() = runTest {
        val expectedTokenAfterDelete = "null"
        doNothing().`when`(loginPreferences).deleteToken()
        userRepository.deleteToken()
        verify(loginPreferences).getToken()
        assertEquals(
            expectedTokenAfterDelete,
            loginPreferences.getToken().asLiveData().getOrAwaitValue()
        )
    }

    @Test
    fun `When isFirstTime Should Return Boolean And Match LoginPreferences`() {
        val dummyFirstTime = true
        `when`(loginPreferences.isFirstTime()).thenReturn(flow { emit(dummyFirstTime) })

        val actualFirstTime = userRepository.isFirstTime().getOrAwaitValue()
        verify(loginPreferences).isFirstTime()
        assertEquals(dummyFirstTime, actualFirstTime)
    }

    @Test
    fun `When saveToken Should Run In Preference Once`() = runTest {
        val dummyToken = DummyData.generateDummyLoginResponse().loginResult.token
        doNothing().`when`(loginPreferences).saveToken(anyString())
        userRepository.saveToken(dummyToken)
        verify(loginPreferences).saveToken(anyString())
    }
}