package com.rigelramadhan.storyapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rigelramadhan.storyapp.data.remote.responses.StoryEntity

@Dao
interface StoryDao {
    @Query("SELECT * FROM story")
    fun getStoriesAsLiveData(): LiveData<List<StoryEntity>>

    @Query("SELECT * FROM story")
    fun getStories(): PagingSource<Int, StoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)

    @Query("DELETE FROM story")
    suspend fun deleteAllStories()

    @Query("SELECT COUNT(id) FROM story")
    suspend fun getCount(): Int
}