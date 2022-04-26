package com.rigelramadhan.storyapp.data.datastore

class FakeLoginPreference {
    private var token: String = "null"
    private var firstTime: Boolean = true

    fun getToken() = token

    fun isFirstTime() = firstTime

    fun saveToken(token: String) {
        this.token = token
    }

    fun setFirstTime(firstTime: Boolean) {
        this.firstTime = firstTime
    }

    fun deleteToken() {
        token = "null"
    }
}