package com.rigelramadhan.storyapp.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rigelramadhan.storyapp.data.local.entity.StoryRemoteKeys

@Dao
interface StoryRemoteKeysDao {

    @Query("SELECT * FROM story_remote_keys WHERE id = :id")
    suspend fun getRemoteKeys(id: String): StoryRemoteKeys

    @Query("SELECT * FROM story_remote_keys")
    suspend fun getRemoteKeys(): StoryRemoteKeys

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRemoteKeys(remoteKeys: List<StoryRemoteKeys>)

    @Query("DELETE FROM story_remote_keys")
    suspend fun deleteRemoteKeys()
}