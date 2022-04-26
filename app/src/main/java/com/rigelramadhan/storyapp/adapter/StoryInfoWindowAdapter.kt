package com.rigelramadhan.storyapp.adapter

import android.app.Activity
import android.view.View
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.rigelramadhan.storyapp.R
import com.rigelramadhan.storyapp.databinding.StoryInfoWindowBinding
import kotlinx.coroutines.runBlocking

class StoryInfoWindowAdapter(private val context: Activity) :
    GoogleMap.InfoWindowAdapter {
    private val binding: StoryInfoWindowBinding by lazy {
        StoryInfoWindowBinding.inflate(context.layoutInflater)
    }

    override fun getInfoContents(marker: Marker): View {
        binding.tvUserName.text = context.getString(R.string.story_map_title).format(marker.title)
        binding.tvDescription.text = marker.snippet

        return binding.root
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }
}