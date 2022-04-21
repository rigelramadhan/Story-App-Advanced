package com.rigelramadhan.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.*
import com.rigelramadhan.storyapp.data.ITEMS_PER_PAGE
import com.rigelramadhan.storyapp.data.Result
import com.rigelramadhan.storyapp.data.local.entity.StoryEntity
import com.rigelramadhan.storyapp.data.local.paging.StoryRemoteMediator
import com.rigelramadhan.storyapp.data.local.room.StoryDatabase
import com.rigelramadhan.storyapp.data.remote.responses.AddStoryResponse
import com.rigelramadhan.storyapp.data.remote.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepository @Inject constructor(
    private val apiService: ApiService,
    private val database: StoryDatabase
) {
    private val postStoryResult = MediatorLiveData<Result<AddStoryResponse>>()

    @ExperimentalPagingApi
    fun getStories(token: String): LiveData<PagingData<StoryEntity>> {
        val pagingSourceFactory = { database.storyDao().getStories() }

        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE
            ),
            remoteMediator = StoryRemoteMediator(
                token,
                apiService,
                database
            ),
            pagingSourceFactory = pagingSourceFactory
        ).liveData
    }

    fun postStory(token: String, imageFile: File, description: String): LiveData<Result<AddStoryResponse>> {
        postStoryResult.postValue(Result.Loading)

        val textPlainMediaType = "text/plain".toMediaType()
        val imageMediaType = "image/jpeg".toMediaTypeOrNull()

        val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            imageFile.asRequestBody(imageMediaType)
        )
        val descriptionRequestBody = description.toRequestBody(textPlainMediaType)

        val client = apiService.postStory(token, imageMultiPart, descriptionRequestBody)
        client.enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>
            ) {
                if (response.isSuccessful) {
                    val responseInfo = response.body()
                    if (responseInfo != null) {
                        postStoryResult.postValue(Result.Success(responseInfo))
                    } else {
                        postStoryResult.postValue(Result.Error(POST_ERROR_MESSAGE))
                        Log.e(TAG, "Failed: story post info is null")
                    }
                } else {
                    postStoryResult.postValue(Result.Error(POST_ERROR_MESSAGE))
                    Log.e(TAG, "Failed: story post response unsuccessful - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                postStoryResult.postValue(Result.Error(POST_ERROR_MESSAGE))
                Log.e(TAG, "Failed: story post response failure - ${t.message.toString()}")
            }
        })

        return postStoryResult
    }

    companion object {
        private val TAG = StoryRepository::class.java.simpleName
        private const val POST_ERROR_MESSAGE = "Story was not posted, please try again later."
    }
}