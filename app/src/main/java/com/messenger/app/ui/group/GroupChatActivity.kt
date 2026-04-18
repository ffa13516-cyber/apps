package com.messenger.app.ui.group

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.messenger.app.databinding.ActivityGroupChatBinding
import com.messenger.app.ui.chat.MessagesAdapter
import com.messenger.app.utils.SessionManager
import com.messenger.app.utils.toast
import kotlinx.coroutines.launch

class GroupChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupChatBinding
    private val viewModel: GroupViewModel by viewModels()
    private lateinit var adapter: MessagesAdapter
    private lateinit var sessionManager: SessionManager

    private lateinit var groupId: String
    private lateinit var groupName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager.getInstance(this)
        groupId = intent.getStringExtra("GROUP_ID") ?: ""
        groupName = intent.getStringExtra("GROUP_NAME") ?: "Group"

        setupToolbar()
        setupRecyclerView()
        setupSendButton()
        observeMessages()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = groupName
            setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.tvHeaderInitial.text = groupName.firstOrNull()?.uppercaseChar()?.toString() ?: "G"
    }

    private fun setupRecyclerView() {
        adapter = MessagesAdapter(sessionManager.getUserId())
        val layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
    }

    private fun setupSendButton() {
        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString()
            if (text.isBlank()) return@setOnClickListener
            viewModel.sendMessage(
                groupId = groupId,
                senderId = sessionManager.getUserId(),
                senderName = sessionManager.getDisplayName(),
                text = text
            )
            binding.etMessage.setText("")
        }
    }

    private fun observeMessages() {
        viewModel.observeMessages(groupId)
        lifecycleScope.launch {
            viewModel.messages.collect { messages ->
                adapter.submitList(messages) {
                    if (messages.isNotEmpty()) binding.recyclerView.smoothScrollToPosition(messages.size - 1)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.sendState.collect { state ->
                if (state is GroupSendState.Error) toast(state.message)
            }
        }
    }
}
