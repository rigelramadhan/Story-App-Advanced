package com.rigelramadhan.storyapp.ui.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.rigelramadhan.storyapp.MainCoroutineRule
import com.rigelramadhan.storyapp.data.Result
import com.rigelramadhan.storyapp.data.remote.responses.RegisterResponse
import com.rigelramadhan.storyapp.getOrAwaitValue
import com.rigelramadhan.storyapp.DummyData
import com.rigelramadhan.storyapp.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doNothing
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {
    @get:Rule
    var instantTaskExecutor = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var userRepository: UserRepository
    private lateinit var registerViewModel: RegisterViewModel

    @Before
    fun setUp() {
        registerViewModel = RegisterViewModel(userRepository)
    }

    @Test
    fun `When registerUser Valid Should Return Success`() {
        val observer = Observer<Result<RegisterResponse>> {}
        val dummyName = "Test"
        val dummyEmail = "test@mail.com"
        val dummyPassword = "123456"
        try {
            val dummyRegisterResponse = DummyData.generateDummyRegisterResponse()
            val expectedRegisterResponse = MutableLiveData<Result<RegisterResponse>>()
            expectedRegisterResponse.value = Result.Success(dummyRegisterResponse)
            `when`(registerViewModel.registerUser(dummyName, dummyEmail, dummyPassword)).thenReturn(
                expectedRegisterResponse
            )

            val actualRegisterResponse =
                registerViewModel.registerUser(dummyName, dummyEmail, dummyPassword)
                    .getOrAwaitValue()
            Mockito.verify(userRepository).register(dummyName, dummyEmail, dummyPassword)
            assertNotNull(actualRegisterResponse)
            assertTrue(actualRegisterResponse is Result.Success)
            assertEquals(
                dummyRegisterResponse.message,
                (actualRegisterResponse as Result.Success).data.message
            )
        } finally {
            registerViewModel.registerUser(dummyName, dummyEmail, dummyPassword).removeObserver(observer)
        }
    }
}