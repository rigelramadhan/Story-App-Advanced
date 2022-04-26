package com.rigelramadhan.storyapp.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rigelramadhan.storyapp.R
import com.rigelramadhan.storyapp.adapter.StoryInfoWindowAdapter
import com.rigelramadhan.storyapp.data.Result
import com.rigelramadhan.storyapp.data.remote.responses.StoryEntity
import com.rigelramadhan.storyapp.databinding.FragmentStoryMapBinding
import com.rigelramadhan.storyapp.ui.detail.DetailActivity
import dagger.hilt.android.AndroidEntryPoint

class StoryMapFragment : Fragment() {

    private var _binding: FragmentStoryMapBinding? = null
    private val binding get() = _binding!!

    private val storyMapViewModel: StoryMapViewModel by viewModels {
        StoryMapViewModel.StoryMapViewModelFactory.getInstance(requireContext())
    }

    @SuppressLint("PotentialBehaviorOverride")
    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.setInfoWindowAdapter(StoryInfoWindowAdapter(requireActivity()))
        googleMap.setOnInfoWindowClickListener {
            val story = it.tag as StoryEntity
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.STORY_EXTRA, story)
            startActivity(intent)
        }

        loadStoriesIntoMap(googleMap)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentStoryMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun loadStoriesIntoMap(googleMap: GoogleMap) {
        storyMapViewModel.getToken().observe(this) { token ->
            if (token != "null") {
                val myToken = "Bearer $token"
                storyMapViewModel.getStories(myToken).observe(this) {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Error -> {}
                        is Result.Success -> {
                            setupMarkers(googleMap, it.data)
                        }
                    }
                }
            }
        }
    }

    private fun setupMarkers(googleMap: GoogleMap, stories: List<StoryEntity>) {
        stories.forEach { story ->
            val marker = MarkerOptions()
            marker.position(LatLng(story.lat, story.lon))
                .title(story.name)
                .snippet(story.description)

            val markerTag = googleMap.addMarker(marker)
            markerTag?.tag = story
        }
    }
}