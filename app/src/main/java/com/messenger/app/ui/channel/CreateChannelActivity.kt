package com.messenger.app.ui.channel

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.messenger.app.databinding.ActivityCreateChannelBinding
import com.messenger.app.utils.SessionManager
import com.messenger.app.utils.hide
import com.messenger.app.utils.show
import com.messenger.app.utils.toast
import kotlinx.coroutines.launch

class CreateChannelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateChannelBinding
    private val viewModel: ChannelViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateChannelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager.getInstance(this)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "New Channel"
            setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.btnCreate.setOnClickListener {
            val name = binding.etChannelName.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            viewModel.createChannel(name, description, sessionManager.getUserId())
        }

        lifecycleScope.launch {
            viewModel.createState.collect { state ->
                when (state) {
                    is ChannelCreateState.Loading -> {
                        binding.progressBar.show()
                        binding.btnCreate.isEnabled = false
                    }
                    is ChannelCreateState.Success -> {
                        binding.progressBar.hide()
                        toast("Channel created!")
                        finish()
                    }
                    is ChannelCreateState.Error -> {
                        binding.progressBar.hide()
                        binding.btnCreate.isEnabled = true
                        toast(state.message)
                    }
                    else -> {
                        binding.progressBar.hide()
                        binding.btnCreate.isEnabled = true
                    }
                }
            }
        }
    }
}
