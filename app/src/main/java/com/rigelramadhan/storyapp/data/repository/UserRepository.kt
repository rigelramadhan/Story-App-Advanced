package com.rigelramadhan.storyapp.data.repository

import androidx.lifecycle.*
import com.rigelramadhan.storyapp.data.Result
import com.rigelramadhan.storyapp.data.local.datastore.LoginPreferences
import com.rigelramadhan.storyapp.data.remote.responses.LoginResponse
import com.rigelramadhan.storyapp.data.remote.responses.RegisterResponse
import com.rigelramadhan.storyapp.data.remote.retrofit.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val loginPreferences: LoginPreferences
) {
    private val loginResult = MutableLiveData<Result<LoginResponse>>()
    private val registerResult = MutableLiveData<Result<RegisterResponse>>()

    fun getLoginResult(): LiveData<Result<LoginResponse>> = loginResult
    fun getRegisterResult(): LiveData<Result<RegisterResponse>> = registerResult

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        loginResult.postValue(Result.Loading)
        emit(Result.Loading)
        try {
            val response = apiService.login(
                email,
                password
            )
            if (response.error) {
                loginResult.postValue(Result.Error(response.message))
                emit(Result.Error(response.message))
            } else {
                loginResult.postValue(Result.Success(response))
                emit(Result.Success(response))
            }
        } catch (e: Exception) {
            loginResult.postValue(Result.Error(e.message.toString()))
            emit(Result.Error(e.message.toString()))
        }
    }

    fun register(name: String, email: String, password: String) = liveData<Result<RegisterResponse>> {
        registerResult.value = Result.Loading
        try {
            val response = apiService.register(
                name,
                email,
                password
            )
            if (response.error) {
                registerResult.value = Result.Error(response.message)
            } else {
                registerResult.value = Result.Success(response)
            }
        } catch (e: Exception) {
            registerResult.value = Result.Error(e.message.toString())
        }
    }

    fun getToken(): LiveData<String> = loginPreferences.getToken().asLiveData()

    suspend fun deleteToken() = loginPreferences.deleteToken()

    fun isFirstTime(): LiveData<Boolean> = loginPreferences.isFirstTime().asLiveData()

    suspend fun saveToken(token: String) = loginPreferences.saveToken(token)

    suspend fun setFirstTime(firstTime: Boolean) = loginPreferences.setFirstTime(firstTime)


    companion object {
        private val TAG = UserRepository::class.java.simpleName
        private const val LOGIN_ERROR_MESSAGE = "Login failed, please try again later."
        private const val REGISTER_ERROR_MESSAGE = "Register failed, please try again later."
    }
}