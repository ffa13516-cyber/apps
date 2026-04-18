package com.messenger.app.ui.channel

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.messenger.app.databinding.ActivityChannelBinding
import com.messenger.app.ui.chat.MessagesAdapter
import com.messenger.app.utils.SessionManager
import com.messenger.app.utils.hide
import com.messenger.app.utils.show
import com.messenger.app.utils.toast
import kotlinx.coroutines.launch

class ChannelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChannelBinding
    private val viewModel: ChannelViewModel by viewModels()
    private lateinit var adapter: MessagesAdapter
    private lateinit var sessionManager: SessionManager

    private lateinit var channelId: String
    private lateinit var channelName: String
    private lateinit var adminId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChannelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager.getInstance(this)
        channelId = intent.getStringExtra("CHANNEL_ID") ?: ""
        channelName = intent.getStringExtra("CHANNEL_NAME") ?: "Channel"
        adminId = intent.getStringExtra("ADMIN_ID") ?: ""

        setupToolbar()
        setupRecyclerView()
        setupSendArea()
        observeMessages()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = channelName
            subtitle = "Channel"
            setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.tvHeaderInitial.text = channelName.firstOrNull()?.uppercaseChar()?.toString() ?: "C"
    }

    private fun setupRecyclerView() {
        adapter = MessagesAdapter(sessionManager.getUserId())
        val layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
    }

    private fun setupSendArea() {
        val myUid = sessionManager.getUserId()
        val isAdmin = myUid == adminId

        if (isAdmin) {
            binding.sendLayout.show()
            binding.tvReadOnly.hide()
        } else {
            binding.sendLayout.hide()
            binding.tvReadOnly.show()
        }

        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString()
            if (text.isBlank()) return@setOnClickListener
            viewModel.sendMessage(channelId, myUid, sessionManager.getDisplayName(), text, adminId)
            binding.etMessage.setText("")
        }
    }

    private fun observeMessages() {
        viewModel.observeMessages(channelId)
        lifecycleScope.launch {
            viewModel.messages.collect { messages ->
                adapter.submitList(messages) {
                    if (messages.isNotEmpty()) binding.recyclerView.smoothScrollToPosition(messages.size - 1)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.sendState.collect { state ->
                if (state is ChannelSendState.Error) toast(state.message)
            }
        }
    }
}
