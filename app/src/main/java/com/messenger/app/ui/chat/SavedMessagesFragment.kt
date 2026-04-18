package com.messenger.app.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.messenger.app.data.model.Message
import com.messenger.app.data.repository.ChatRepository
import com.messenger.app.databinding.FragmentSavedMessagesBinding
import com.messenger.app.utils.SessionManager
import com.messenger.app.utils.toast
import kotlinx.coroutines.launch

class SavedMessagesFragment : Fragment() {

    private var _binding: FragmentSavedMessagesBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: MessagesAdapter
    private val chatRepository = ChatRepository()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSavedMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager.getInstance(requireContext())

        val myUid = sessionManager.getUserId()
        // Saved messages chat ID is just the user's own UID
        val savedChatId = "saved_$myUid"

        adapter = MessagesAdapter(myUid)
        val layoutManager = LinearLayoutManager(requireContext()).apply { stackFromEnd = true }
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        // Observe saved messages
        lifecycleScope.launch {
            chatRepository.observeMessages(savedChatId).collect { messages ->
                adapter.submitList(messages) {
                    if (messages.isNotEmpty()) {
                        binding.recyclerView.smoothScrollToPosition(messages.size - 1)
                    }
                }
            }
        }

        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString().trim()
            if (text.isBlank()) return@setOnClickListener
            lifecycleScope.launch {
                val message = Message(
                    senderId = myUid,
                    senderName = sessionManager.getDisplayName(),
                    text = text,
                    timestamp = System.currentTimeMillis()
                )
                chatRepository.sendMessage(savedChatId, message)
                binding.etMessage.setText("")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
