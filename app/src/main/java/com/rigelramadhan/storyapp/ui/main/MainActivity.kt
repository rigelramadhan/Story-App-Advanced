package com.rigelramadhan.storyapp.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.rigelramadhan.storyapp.R
import com.rigelramadhan.storyapp.adapter.StoriesAdapter
import com.rigelramadhan.storyapp.adapter.StoryLoadingStateAdapter
import com.rigelramadhan.storyapp.databinding.ActivityMainBinding
import com.rigelramadhan.storyapp.ui.camera.CameraActivity
import com.rigelramadhan.storyapp.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val mainViewModel: MainViewModel by viewModels()

    private val adapter = StoriesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupAdapter()
        setupButtons()
    }

    @ExperimentalPagingApi
    private fun getData(token: String) {
        lifecycleScope.launch {
            mainViewModel.getStories(token).observe(this@MainActivity) {
                adapter.submitData(this@MainActivity.lifecycle, it)
            }
        }
    }

    private fun setupAdapter() {
        binding.rvStories.apply {
            adapter = this@MainActivity.adapter.withLoadStateHeaderAndFooter(
                header = StoryLoadingStateAdapter {
                    retry()
                },
                footer = StoryLoadingStateAdapter {
                    retry()
                }
            )

            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }

        adapter.addLoadStateListener { loadState ->
            if (loadState.mediator?.refresh is LoadState.Loading) {
                if (adapter.snapshot().isEmpty()) {
                    binding.progressBar.visibility = View.VISIBLE
                }

                binding.tvNoStory.visibility = View.INVISIBLE
            } else {
                binding.progressBar.visibility = View.INVISIBLE
                binding.swipeLayout.isRefreshing = false

                val error = when {
                    loadState.mediator?.refresh is LoadState.Error -> loadState.mediator?.refresh as LoadState.Error
                    loadState.mediator?.prepend is LoadState.Error -> loadState.mediator?.prepend as LoadState.Error
                    loadState.mediator?.append is LoadState.Error -> loadState.mediator?.append as LoadState.Error
                    else -> null
                }

                error?.let {
                    if (adapter.snapshot().isEmpty()) {
                        binding.tvNoStory.visibility = View.VISIBLE
                        binding.tvNoStory.text = it.error.localizedMessage
                    }
                }
            }
        }
    }

    private fun retry() {
        adapter.retry()
    }

    @ExperimentalPagingApi
    override fun onResume() {
        super.onResume()
        checkIfSessionValid()
    }

    @ExperimentalPagingApi
    private fun checkIfSessionValid() {
        mainViewModel.checkIfTokenAvailable().observe(this) {
            if (it == "null") {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                getData("Bearer $it")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            mainViewModel.logout()
        } else if (item.itemId == R.id.settings) {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupButtons() {
        binding.btnAdd.setOnClickListener {
            if (!allPermissionsGranted()) {
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            } else {
                val intent = Intent(this, CameraActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, getString(R.string.permission_needed), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}