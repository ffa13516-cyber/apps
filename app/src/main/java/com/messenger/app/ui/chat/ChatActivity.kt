package com.messenger.app.ui.chat

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.messenger.app.databinding.ActivityChatBinding
import com.messenger.app.utils.SessionManager
import com.messenger.app.utils.toast
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var adapter: MessagesAdapter
    private lateinit var sessionManager: SessionManager

    private lateinit var chatId: String
    private lateinit var otherUserId: String
    private lateinit var otherUserName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager.getInstance(this)
        chatId = intent.getStringExtra("CHAT_ID") ?: ""
        otherUserId = intent.getStringExtra("OTHER_USER_ID") ?: ""
        otherUserName = intent.getStringExtra("OTHER_USER_NAME") ?: "Chat"

        setupToolbar()
        setupRecyclerView()
        setupSendButton()
        observeMessages()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = otherUserName
            setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.tvHeaderInitial.text = otherUserName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    }

    private fun setupRecyclerView() {
        val myUid = sessionManager.getUserId()
        adapter = MessagesAdapter(myUid)
        val layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
    }

    private fun setupSendButton() {
        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString()
            if (text.isBlank()) return@setOnClickListener
            viewModel.sendMessage(
                chatId = chatId,
                senderId = sessionManager.getUserId(),
                senderName = sessionManager.getDisplayName(),
                text = text
            )
            binding.etMessage.setText("")
        }
    }

    private fun observeMessages() {
        viewModel.observeMessages(chatId)
        lifecycleScope.launch {
            viewModel.messages.collect { messages ->
                adapter.submitList(messages) {
                    if (messages.isNotEmpty()) {
                        binding.recyclerView.smoothScrollToPosition(messages.size - 1)
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.sendState.collect { state ->
                if (state is SendState.Error) toast(state.message)
            }
        }
    }
}
