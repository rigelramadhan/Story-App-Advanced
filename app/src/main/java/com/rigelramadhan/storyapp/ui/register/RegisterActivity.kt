package com.rigelramadhan.storyapp.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.rigelramadhan.storyapp.R
import com.rigelramadhan.storyapp.data.Result
import com.rigelramadhan.storyapp.databinding.ActivityRegisterBinding
import com.rigelramadhan.storyapp.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    private val registerViewModel: RegisterViewModel by viewModels {
        RegisterViewModel.RegisterViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.btnRegister.setOnClickListener {
            val name = binding.etLayoutName.text
            val email = binding.etLayoutEmail.text
            val password = binding.etLayoutPassword.text
            if (!name.isNullOrEmpty() && !email.isNullOrEmpty() && !password.isNullOrEmpty()) {
                registerViewModel.registerUser(
                    name.toString(),
                    email.toString(),
                    password.toString()
                ).observe(this) {
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
                            Toast.makeText(this, getString(R.string.register_successful), Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            } else {
                if (name.isNullOrEmpty()) binding.etLayoutName.error =
                    getString(R.string.name_cannot_empty)
                if (email.isNullOrEmpty()) binding.etLayoutEmail.error =
                    getString(R.string.email_cannot_empty)
                if (email.isNullOrEmpty()) binding.etLayoutPassword.error =
                    getString(R.string.password_minimum)
            }
        }
    }
}