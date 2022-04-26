package com.rigelramadhan.storyapp.data.remote.retrofit

import com.rigelramadhan.storyapp.data.remote.responses.AddStoryResponse
import com.rigelramadhan.storyapp.data.remote.responses.LoginResponse
import com.rigelramadhan.storyapp.data.remote.responses.RegisterResponse
import com.rigelramadhan.storyapp.data.remote.responses.StoriesResponse
import com.rigelramadhan.storyapp.data.remote.retrofit.ApiService
import com.rigelramadhan.storyapp.DummyData
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeApiService : ApiService {
    override suspend fun getStories(
        token: String,
        page: Int?,
        size: Int?,
        location: Int?
    ): StoriesResponse {
        return DummyData.generateDummyStoriesResponse()
    }

    override suspend fun getStories(token: String, location: Int): StoriesResponse {
        return DummyData.generateDummyStoriesResponse()
    }

    override suspend fun postStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): AddStoryResponse {
        return DummyData.generateDummyAddStoryResponse()
    }

    override suspend fun login(email: String, password: String): LoginResponse {
        return DummyData.generateDummyLoginResponse()
    }

    override suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return DummyData.generateDummyRegisterResponse()
    }
}