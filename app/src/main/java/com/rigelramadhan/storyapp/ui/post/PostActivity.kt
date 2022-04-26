package com.rigelramadhan.storyapp.ui.post

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.rigelramadhan.storyapp.R
import com.rigelramadhan.storyapp.data.Result
import com.rigelramadhan.storyapp.databinding.ActivityPostBinding
import com.rigelramadhan.storyapp.ui.login.LoginActivity
import com.rigelramadhan.storyapp.ui.main.MainActivity
import com.rigelramadhan.storyapp.utils.AppExecutors
import com.rigelramadhan.storyapp.utils.reduceFileImage
import com.rigelramadhan.storyapp.utils.rotateBitmap
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class PostActivity : AppCompatActivity() {

    private val binding: ActivityPostBinding by lazy {
        ActivityPostBinding.inflate(layoutInflater)
    }

    private val postViewModel: PostViewModel by viewModels()

    private val appExecutor: AppExecutors by lazy {
        AppExecutors()
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var location: Location? = null
    private var file: File? = null
    private var isBack: Boolean = true
    private var reducingDone: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.setDisplayShowHomeEnabled(true)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
        bindResult()
        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        checkIfSessionValid()
    }

    private fun checkIfSessionValid() {
        postViewModel.checkIfTokenAvailable().observe(this) {
            if (it == "null") {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    private fun setupButtons() {
        binding.btnPost.setOnClickListener {
            postViewModel.checkIfTokenAvailable().observe(this) {
                if (reducingDone) {
                    if (it == "null") {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        uploadImage("Bearer $it")
                    }
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.wait_for_processing),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun bindResult() {
        file = intent.getSerializableExtra(PHOTO_RESULT_EXTRA) as File
        isBack = intent.getBooleanExtra(IS_CAMERA_BACK_EXTRA, true)

        val result = rotateBitmap(BitmapFactory.decodeFile((file as File).path), isBack)
        result.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))

        appExecutor.diskIO.execute {
            file = reduceFileImage(file as File)
            reducingDone = true
        }


        binding.imgStory.setImageBitmap(result)
    }

    private fun uploadImage(token: String) {
        if (binding.etDescription.text.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.description_cannot_empty), Toast.LENGTH_SHORT)
                .show()
        } else {
            if (file != null) {
                binding.progressBar.visibility = View.VISIBLE
                val description = binding.etDescription.text.toString()

                val result = postViewModel.postStory(
                    token,
                    file as File,
                    description,
                    location?.latitude?.toFloat(),
                    location?.longitude?.toFloat()
                )

                result.observe(this) {
                    when (it) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Result.Error -> {
                            binding.progressBar.visibility = View.INVISIBLE
                            val error = it.error
                            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                        }

                        is Result.Success -> {
                            binding.progressBar.visibility = View.INVISIBLE
                            Toast.makeText(
                                this,
                                getString(R.string.image_posted),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getLastLocation()
                }
            }
        }

    private fun getLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                this.location = location
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val PHOTO_RESULT_EXTRA = "photo_result_extra"
        const val IS_CAMERA_BACK_EXTRA = "is_camera_back_extra"
    }
}