package com.rigelramadhan.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.rigelramadhan.storyapp.data.Result
import com.rigelramadhan.storyapp.data.local.datastore.LoginPreferences
import com.rigelramadhan.storyapp.data.remote.responses.LoginResponse
import com.rigelramadhan.storyapp.data.remote.responses.RegisterResponse
import com.rigelramadhan.storyapp.data.remote.retrofit.ApiService

class FakeUserRepository constructor(
    private val apiService: ApiService,
    private val loginPreferences: LoginPreferences
) : UserDataSource {
    private val loginResult = MutableLiveData<Result<LoginResponse>>()
    private val registerResult = MutableLiveData<Result<RegisterResponse>>()

    override fun getRegisterResult(): LiveData<Result<RegisterResponse>> = registerResult

    override fun login(email: String, password: String): LiveData<Result<LoginResponse>> =
        liveData {
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

    override fun register(
        name: String,
        email: String,
        password: String
    ) = liveData<Result<RegisterResponse>> {
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

    override fun getToken(): LiveData<String> {
        TODO("Not yet implemented")
    }

    override fun deleteToken() {
        TODO("Not yet implemented")
    }

    override fun isFirstTime(): LiveData<Boolean> {
        TODO("Not yet implemented")
    }

    override fun saveToken(token: String) {
        TODO("Not yet implemented")
    }

    override fun setFirstTime(firstTime: Boolean) {
        TODO("Not yet implemented")
    }

}