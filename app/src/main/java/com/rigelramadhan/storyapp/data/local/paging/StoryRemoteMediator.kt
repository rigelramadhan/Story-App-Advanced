package com.rigelramadhan.storyapp.data.local.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.rigelramadhan.storyapp.data.ITEMS_PER_PAGE
import com.rigelramadhan.storyapp.data.local.entity.StoryEntity
import com.rigelramadhan.storyapp.data.local.entity.StoryRemoteKeys
import com.rigelramadhan.storyapp.data.local.room.StoryDatabase
import com.rigelramadhan.storyapp.data.remote.retrofit.ApiService
import retrofit2.HttpException
import java.io.IOException

@ExperimentalPagingApi
class StoryRemoteMediator(
    private val token: String,
    private val apiService: ApiService,
    private val database: StoryDatabase
) : RemoteMediator<Int, StoryEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val key = when (loadType) {
            LoadType.REFRESH -> if (database.storyDao()
                    .getCount() > 0
            ) return MediatorResult.Success(false) else null
            LoadType.PREPEND -> return MediatorResult.Success(true)
            LoadType.APPEND -> database.storyRemoteKeysDao().getRemoteKeys()
        }

        try {
            if (key != null) {
                if (key.nextPage == null) return MediatorResult.Success(true)
            }

            val page = key?.nextPage ?: 1
            val response = apiService.getStories(token, page, ITEMS_PER_PAGE)
            val storyList = response.listStory

            val endOfPaginationReached = storyList.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.storyDao().deleteAllStories()
                    database.storyRemoteKeysDao().deleteRemoteKeys()
                }

                val nextKey = if (endOfPaginationReached) null else page + 1
                val prevKey = if (page == 1) null else page - 1

                val keys = storyList.map {
                    StoryRemoteKeys(
                        it.id,
                        prevKey,
                        nextKey
                    )
                }

                val stories = storyList.map {
                    StoryEntity(
                        it.photoUrl,
                        it.createdAt,
                        it.name,
                        it.description,
                        it.lon,
                        it.id,
                        it.lat
                    )
                }

                database.storyRemoteKeysDao().addRemoteKeys(keys)
                database.storyDao().insertStories(stories)
            }

            return  MediatorResult.Success(endOfPaginationReached)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
}