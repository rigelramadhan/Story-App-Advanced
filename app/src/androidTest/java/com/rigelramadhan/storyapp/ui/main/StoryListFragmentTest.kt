package com.rigelramadhan.storyapp.ui.main

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rigelramadhan.storyapp.R
import com.rigelramadhan.storyapp.data.remote.retrofit.ApiService
import com.rigelramadhan.storyapp.di.NetworkModule
import com.rigelramadhan.storyapp.launchFragmentInHiltContainer
import com.rigelramadhan.storyapp.utils.EspressoIdlingResource
import com.rigelramadhan.storyapp.utils.JsonConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.endsWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@UninstallModules(NetworkModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class StoryListFragmentTest {

    private val mockWebServer = MockWebServer()

    @Module
    @InstallIn(SingletonComponent::class)
    class FakeNetworkModule {

        @Provides
        @Singleton
        fun provideRetrofit(): Retrofit {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
            return Retrofit.Builder()
                .baseUrl("http://127.0.0.1:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        @Provides
        @Singleton
        fun provideApiService(retrofit: Retrofit): ApiService {
            return retrofit.create(ApiService::class.java)
        }
    }

    @Before
    fun setUp() {
        mockWebServer.start(8080)
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun getStoryList_Success() {
        launchFragmentInHiltContainer<StoryListFragment>(themeResId = R.style.Theme_StoryApp)

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("success_response.json"))

        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.rv_stories)).check(matches(isDisplayed()))
        onView(withText(endsWith("tehyung"))).check(matches(isDisplayed()))
        onView(withId(R.id.rv_stories)).perform(
            RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
            hasDescendant(withText("Reisalin"))
        ))
    }
}