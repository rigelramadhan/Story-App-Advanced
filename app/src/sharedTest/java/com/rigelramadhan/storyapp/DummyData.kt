package com.rigelramadhan.storyapp

import com.rigelramadhan.storyapp.data.local.entity.StoryRemoteKeys
import com.rigelramadhan.storyapp.data.remote.responses.*


object DummyData {
    fun generateDummyStoryEntityList(): List<StoryEntity> {
        val storyList = ArrayList<StoryEntity>()
        for (i in 1..10) {
            val story = StoryEntity(
                "https://pbs.twimg.com/profile_images/1455185376876826625/s1AjSxph_400x400.jpg",
                "2022-04-21T06:41:06.470Z",
                "User $i",
                "Description of post $i",
                100.0 + i*2,
                "story-$i",
                100.0 + i*2
            )

            storyList.add(story)
        }

        return storyList
    }

    fun generateDummyStoryRemoteKeysList(): List<StoryRemoteKeys> {
        val storyKeysList = ArrayList<StoryRemoteKeys>()
        val data = generateDummyStoryEntityList()
        data.slice(0..4).forEach {
            val id = it.id
            val key = StoryRemoteKeys(id, null, 2)
            storyKeysList.add(key)
        }

        data.slice(5..9).forEach {
            val id = it.id
            val key = StoryRemoteKeys(id, 1, 3)
            storyKeysList.add(key)
        }

        return storyKeysList
    }

    fun generateDummyLoginResponse(): LoginResponse {
        return LoginResponse(
            LoginResult("12345"),
            false,
            "success"
        )
    }

    fun generateDummyErrorLoginResponse(): LoginResponse {
        return LoginResponse(
            LoginResult(""),
            true,
            "Invalid password"
        )
    }

    fun generateDummyRegisterResponse(): RegisterResponse {
        return RegisterResponse(
            false,
            "User created"
        )
    }

    fun generateDummyStoriesResponse(): StoriesResponse {
        return StoriesResponse(
            generateDummyStoryEntityList(),
            false,
            "Stories fetched successfully"
        )
    }

    fun generateDummyAddStoryResponse(): AddStoryResponse {
        return AddStoryResponse(
            error = false,
            message = "Story created successfully"
        )
    }
}