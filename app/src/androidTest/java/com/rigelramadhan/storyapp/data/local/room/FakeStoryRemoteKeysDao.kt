package com.rigelramadhan.storyapp.data.local.room

import com.rigelramadhan.storyapp.data.local.entity.StoryRemoteKeys
import com.rigelramadhan.storyapp.data.local.room.StoryRemoteKeysDao

class FakeStoryRemoteKeysDao : StoryRemoteKeysDao {
    private var storyKeys = mutableListOf<StoryRemoteKeys>()

    override suspend fun getRemoteKeys(id: String): StoryRemoteKeys? {
        var key: StoryRemoteKeys? = null
        for (it in storyKeys) {
            if (it.id == id) {
                key = it
                break
            }
        }

        return key
    }

    override suspend fun addRemoteKeys(remoteKeys: List<StoryRemoteKeys>) {
        storyKeys.addAll(remoteKeys)
    }

    override suspend fun deleteRemoteKeys() {
        storyKeys.removeAll(storyKeys)
    }
}