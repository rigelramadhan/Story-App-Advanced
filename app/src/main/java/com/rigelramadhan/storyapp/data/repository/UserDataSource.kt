package com.rigelramadhan.storyapp.data.repository

import androidx.lifecycle.LiveData
import com.rigelramadhan.storyapp.data.Result
import com.rigelramadhan.storyapp.data.remote.responses.LoginResponse
import com.rigelramadhan.storyapp.data.remote.responses.RegisterResponse

interface UserDataSource {

    fun getRegisterResult(): LiveData<Result<RegisterResponse>>

    fun login(email: String, password: String): LiveData<Result<LoginResponse>>

    fun register(name: String, email: String, password: String): LiveData<Result<RegisterResponse>>

    fun getToken(): LiveData<String>

    fun deleteToken()

    fun isFirstTime(): LiveData<Boolean>

    fun saveToken(token: String)

    fun setFirstTime(firstTime: Boolean)
}