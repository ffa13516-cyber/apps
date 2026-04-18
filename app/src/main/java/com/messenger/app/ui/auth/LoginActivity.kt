package com.messenger.app.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.messenger.app.databinding.ActivityLoginBinding
import com.messenger.app.ui.home.HomeActivity
import com.messenger.app.utils.SessionManager
import com.messenger.app.utils.hide
import com.messenger.app.utils.show
import com.messenger.app.utils.toast

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager.getInstance(this)

        // Already logged in
        if (sessionManager.isLoggedIn()) {
            goToHome()
            return
        }

        setupObservers()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val phone = binding.etPhone.text.toString().trim()
            viewModel.login(phone)
        }
    }

    private fun setupObservers() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginState.Loading -> {
                    binding.progressBar.show()
                    binding.btnLogin.isEnabled = false
                }
                is LoginState.Success -> {
                    binding.progressBar.hide()
                    binding.btnLogin.isEnabled = true
                    sessionManager.saveSession(
                        uid = state.user.uid,
                        phone = state.user.phoneNumber,
                        name = state.user.displayName
                    )
                    goToHome()
                }
                is LoginState.Error -> {
                    binding.progressBar.hide()
                    binding.btnLogin.isEnabled = true
                    toast(state.message)
                }
            }
        }
    }

    private fun goToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
